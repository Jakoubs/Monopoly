package de.htwg.controller

import de.htwg.model.*
import de.htwg.model.modelBaseImple.PropertyField.calculateRent
import de.htwg.Board
import de.htwg.model.IMonopolyGame
import de.htwg.model.modelBaseImple.*
import de.htwg.util.util.Observable
import de.htwg.model.modelBaseImple.{BoardField, Dice, Player, PropertyField, TrainStationField, UtilityField}
import de.htwg.util.UndoManager
import de.htwg.view.BoardPrinter

import java.awt.Choice
import scala.collection.mutable
import scala.io.StdIn.readLine

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
                     rentPaidTo: Option[Player] = None
                   )

class Controller(var game: IMonopolyGame) extends Observable{
  var currentTurnInfo: TurnInfo = TurnInfo()

  def getTurnInfo: TurnInfo = currentTurnInfo
  def updateTurnInfo(newInfo: TurnInfo): Unit = { // Setter-Methode
    currentTurnInfo = newInfo
  }

  private var undoManager = new UndoManager()
  def currentPlayer: IPlayer = game.currentPlayer
  def board: Board = game.board
  def players: Vector[IPlayer] = game.players
  def sound: Boolean = game.sound

  def handleInput(input: OpEnum): Unit = {
    input match {
      case OpEnum.undo => undo()
      case OpEnum.redo => redo()
      case _ =>
        game = game.handle(input, this)
    }
    notifyObservers()
  }

  def updatePlayer(player: Player): Unit = {
    val updatedPlayers = game.players.updated(game.players.indexOf(game.currentPlayer), player)
    game = game.copy(players = updatedPlayers, currentPlayer = player)
  }

  def updateBoardAndPlayer(field: BoardField, player: Player): Unit = {
    val updatedFields = game.board.fields.updated(field.index-1, field)
    val updatedBoard = game.board.copy(fields = updatedFields)
    val updatedPlayers = game.players.updated(game.players.indexOf(game.currentPlayer), player)
    game = game.updateBoardPlayer(board = updatedBoard, players = updatedPlayers, currentPlayer = player)
    //game = game.copy(board = updatedBoard, players = updatedPlayers, currentPlayer = player)
  }

  def switchToNextPlayer(): Unit = {
    val currentIndex = game.players.indexOf(game.currentPlayer)
    val nextIndex = (currentIndex + 1) % game.players.size
    val nextPlayer = game.players(nextIndex)
    game = game.copy(currentPlayer = nextPlayer)
  }


  def executeCommand(newGame: IMonopolyGame): Unit = {
    undoManager = undoManager.doStep(game, newGame)
    game = newGame
    notifyObservers()
  }

  def undo(): Unit = {
    val (previousGame, updatedManager) = undoManager.undo(game)
    undoManager = updatedManager
    game = previousGame
    notifyObservers()
  }

  def redo(): Unit = {
    val (nextGame, updatedManager) = undoManager.redo(game)
    undoManager = updatedManager
    game = nextGame
    notifyObservers()
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

  def getOwnedProperties(): Map[Player, List[PropertyField]] = {
    board.fields.collect {
        case p: PropertyField if p.owner.isDefined => (p.owner.get, p)
      }.groupBy(_._1)
      .map { case (player, tuples) => player -> tuples.map(_._2).toList }
  }

  def getOwnedTrainStations(): Map[Player, Int] = {
    board.fields.collect {
        case t: TrainStationField if t.owner.isDefined => (t.owner.get, 1)
      }.groupBy(_._1)
      .view
      .mapValues(_.size)
      .toMap
  }

  def getOwnedUtilities(): Map[Player, Int] = {
    board.fields.collect {
        case u: UtilityField if u.owner.isDefined => (u.owner.get, 1)
      }.groupBy(_._1)
      .view
      .mapValues(_.size)
      .toMap
  }
}

