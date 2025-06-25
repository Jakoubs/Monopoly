package de.htwg.controller.controllerBaseImpl

import de.htwg.controller.controllerBaseImpl.{GameState, OpEnum, TurnInfo}
import de.htwg.model.*
import de.htwg.model.modelBaseImple.PropertyField.calculateRent
import de.htwg.model.modelBaseImple.*
import de.htwg.util.util.Observable
import de.htwg.view.BoardPrinter
import de.htwg.Board
import de.htwg.model.modelBaseImple.MonopolyGame
import de.htwg.model.IPlayer
import de.htwg.controller.IController
import de.htwg.model.FileIOComponent.FileIOFactory

import java.awt.Choice
import scala.collection.mutable
import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}

enum OpEnum:
  case roll
  case pay
  case buy
  case end
  case y
  case n
  case enter
  case fieldSelected(id: Int)
  case undo
  case redo

case class TurnInfo(
                     diceRoll1: Int = 0,
                     diceRoll2: Int = 0,
                     landedField: Option[BoardField] = None,
                     boughtProperty: Option[BoardField] = None,
                     builtHouse: Option[PropertyField] = None,
                     paidRent: Option[Int] = None,
                     rentPaidTo: Option[IPlayer] = None
                   )

class Controller(var game: IMonopolyGame) extends IController with Observable{
  given controller: Controller = this
  var currentTurnInfo: TurnInfo = TurnInfo()
  private val undoStack: mutable.Stack[Command] = mutable.Stack()
  private val redoStack: mutable.Stack[Command] = mutable.Stack()

  def getTurnInfo: TurnInfo = currentTurnInfo

  def updateTurnInfo(newInfo: TurnInfo): Unit = {
    currentTurnInfo = newInfo
  }


  var state: GameState = StartTurnState()
  def currentPlayer: IPlayer = game.currentPlayer
  def board: Board = game.board
  def players: Vector[IPlayer] = game.players
  def sound: Boolean = game.sound

  def handleInput(input: OpEnum): Unit = {
    given Controller = this
    input match {
      case OpEnum.undo => undo()
      case OpEnum.redo => redo()
      case _ =>
        state = state.handle(input)
        println(state)
    }
    notifyObservers()

  }

  def updatePlayer(newPlayer: IPlayer): Unit = {
    game = game.withUpdatedPlayer(newPlayer)
  }

  def updateBoardAndPlayer(field: BoardField, player: IPlayer): Unit = {
    game = game.withUpdatedBoardAndPlayer(field, player)
  }

  def switchToNextPlayer(): Unit = {
    game = game.withNextPlayer
  }

  def executeCommand(cmd: Command): Unit = {
    cmd.previousGameStates = Some(state)   // Zustand vor Ausführung
    cmd.execute()
    cmd.nextGameStates = Some(state)       // Zustand nach Ausführung
    undoStack.push(cmd)
    redoStack.clear()
  }

  def saveGame(filename: String, format: String = "xml"): Try[Unit] = {
    val fileIO = FileIOFactory.createFileIO(format)
    fileIO.save(game, filename)
  }

  def loadGame(filename: String, format: String = "xml"): Try[Unit] = {
    val fileIO = FileIOFactory.createFileIO(format)
    fileIO.load(filename) match {
      case Success(loadedGame) =>
        game = loadedGame
        state = StartTurnState()
        notifyObservers()
        Success(())
      case Failure(exception) =>
        Failure(exception)
    }
  }

  def undo(): Unit = {
    if (undoStack.nonEmpty) {
      val cmd = undoStack.pop()
      cmd.undo()
      cmd.previousGameStates.foreach(state = _) 
      redoStack.push(cmd)
    }
  }

  def setState(newState: GameState): Unit = {
    state = newState
    notifyObservers()
  }

  def redo(): Unit = {
    if (redoStack.nonEmpty) {
      val cmd = redoStack.pop()
      cmd.execute()
      cmd.nextGameStates.foreach(state = _) 
      undoStack.push(cmd)
      notifyObservers()
    }

  }

  def isGameOver: Boolean = game.players.count(_.balance > 0) <= 1


  def getBoardString: String = {
      BoardPrinter.getBoardAsString(game)
  }

  def getInventory: String = {
    BoardPrinter.getInventoryString(game)
  }

  def getCurrentPlayerStatus: String = {
      val p = currentPlayer
      s"${p.name} | Balance: ${p.balance}€ | Position: ${p.position} | " +
        s"In Jail: ${if (p.isInJail) "Yes" else "No"}"
  }

  def getOwnedProperties(): Map[IPlayer, List[PropertyField]] = {
    board.fields.collect {
        case p: PropertyField if p.owner.isDefined => (p.owner.get, p)
      }.groupBy(_._1)
      .map { case (player, tuples) => player -> tuples.map(_._2).toList }
  }

  def getOwnedTrainStations(): Map[IPlayer, Int] = {
    board.fields.collect {
        case t: TrainStationField if t.owner.isDefined => (t.owner.get, 1)
      }.groupBy(_._1)
      .view
      .mapValues(_.size)
      .toMap
  }

  def getOwnedUtilities(): Map[IPlayer, Int] = {
    board.fields.collect {
        case u: UtilityField if u.owner.isDefined => (u.owner.get, 1)
      }.groupBy(_._1)
      .view
      .mapValues(_.size)
      .toMap
  }
}

