package de.htwg.model.modelBaseImple

import de.htwg.Board
import de.htwg.model.{IMonopolyGame, IPlayer}
import de.htwg.model.modelBaseImple.Player

import scala.util.Try

case class MonopolyGame(
                         players: Vector[IPlayer],
                         board: Board,
                         currentPlayer: IPlayer,
                         sound: Boolean,
                       ) extends IMonopolyGame {

  def createGame: IMonopolyGame = {
    println("⚠️ [DEBUG] createGame wurde aufgerufen!") // <--- TEST
    MonopolyGame(
      players = players,
      board = board,
      currentPlayer = players.head,
      sound = sound,
    )
  }

  override def withUpdatedPlayer(newPlayer: IPlayer): IMonopolyGame = {
    // Find player by name (most reliable identifier)
    val playerIndex = players.indexWhere(_.name == newPlayer.name)

    if (playerIndex >= 0) {
      val ps = players.updated(playerIndex, newPlayer)
      this.copy(players = ps, currentPlayer = newPlayer)
    } else {
      // This should not happen in normal game flow
      throw new IllegalStateException(s"Player ${newPlayer.name} not found in game. Current players: ${players.map(_.name).mkString(", ")}")
    }
  }

  override def withUpdatedPlayers(newPlayers: Vector[IPlayer]): IMonopolyGame = {
    this.copy(players = newPlayers)
  }

  override def withUpdatedBoard(newBoard: Board): MonopolyGame = {
    MonopolyGame(players, newBoard, currentPlayer, sound)
  }

  override def withUpdatedBoardAndPlayer(field: BoardField, player: IPlayer): IMonopolyGame = {
    val updatedFields = board.fields.updated(field.index - 1, field)
    val b = board.copy(fields = updatedFields)
    val ps = players.updated(players.indexOf(currentPlayer), player)
    this.copy(board = b, players = ps, currentPlayer = player)
  }

  override def withNextPlayer: IMonopolyGame = {
    val idx = players.indexOf(currentPlayer)
    val next = players((idx + 1) % players.size)
    this.copy(currentPlayer = next)
  }

  override def buyHouse(field: PropertyField, player: IPlayer): Try[IMonopolyGame] = {
    PropertyField.House().buyHouse(player, field, this).map {
      case (updatedField, updatedPlayer) =>
        this.withUpdatedBoardAndPlayer(updatedField, updatedPlayer)
    }
  }

  override def rollDice(valid: Boolean): (Int, Int) = {
    Dice().rollDice(sound)
  }

  override def endTurn(): IMonopolyGame = {
    this.withNextPlayer
  }



  override def movePlayer(steps: Int): IMonopolyGame = {
    val updatedPlayer = currentPlayer.moveToIndex(((currentPlayer.position + steps) % 40))
    this.copy(currentPlayer = updatedPlayer)
  }

  override def rollDice(dice1: Int, dice2: Int): IMonopolyGame = {
    val updatedPlayer = currentPlayer.moveToIndex((currentPlayer.position + dice1 + dice2) % 40)
    this.copy(currentPlayer = updatedPlayer)
  }

  override def toggleSound(): IMonopolyGame = {
    this.copy(sound = !sound)
  }
}