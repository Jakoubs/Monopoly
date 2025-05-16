package de.htwg.controller

import de.htwg.model.{BoardPrinter, Player, SoundPlayer, *}
import de.htwg.model.PropertyField.calculateRent
import de.htwg.{Board, MonopolyGame}
import de.htwg.util.util.Observable
import de.htwg.controller.GameState
import java.awt.Choice
import scala.io.StdIn.readLine
case class TurnInfo(
                     diceRoll1: Int = 0,
                     diceRoll2: Int = 0,
                     landedField: Option[BoardField] = None,
                     boughtProperty: Option[BoardField] = None,
                     builtHouse: Option[PropertyField] = None,
                     paidRent: Option[Int] = None,
                     rentPaidTo: Option[Player] = None
                   )

class Controller(var game: MonopolyGame, val dice: Dice) extends Observable{
  var currentTurnInfo: TurnInfo = TurnInfo()
  def getTurnInfo: TurnInfo = currentTurnInfo
  def updateTurnInfo(newInfo: TurnInfo): Unit = { // Setter-Methode
    currentTurnInfo = newInfo
  }


  var state: GameState = StartTurnState()
  def currentPlayer: Player = game.currentPlayer
  def board: Board = game.board
  def players: Vector[Player] = game.players
  def sound: Boolean = game.sound

  def handleInput(input: String): Unit = {
    state = state.handle(input, this)
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
    game = game.copy(board = updatedBoard, players = updatedPlayers, currentPlayer = player)
  }

  def switchToNextPlayer(): Unit = {
    val currentIndex = game.players.indexOf(game.currentPlayer)
    val nextIndex = (currentIndex + 1) % game.players.size
    val nextPlayer = game.players(nextIndex)
    game = game.copy(currentPlayer = nextPlayer)
  }

  def isGameOver: Boolean = game.players.count(_.balance > 0) <= 1


  def getBoardString: String = {
      BoardPrinter.getBoardAsString(game)
  }

  def getCurrentPlayerStatus: String = {
      val p = currentPlayer
      s"${p.name} | Balance: ${p.balance}€ | Position: ${p.position} | " +
        s"In Jail: ${if (p.isInJail) "Yes" else "No"}"
  }


}

