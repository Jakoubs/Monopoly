package de.htwg.model.modelBaseImple

import de.htwg.Board
import de.htwg.controller.GameState
import de.htwg.model.{IMonopolyGame, IPlayer}
import de.htwg.model.modelBaseImple.Player

case class MonopolyGame (
                         players: Vector[Player],
                         board: Board,
                         currentPlayer: Player,
                         sound: Boolean,  
                         state: GameState
                       ) extends IMonopolyGame {
  override def withUpdatedPlayer(newPlayer: Player): IMonopolyGame = {
    val ps = players.updated(players.indexOf(currentPlayer), newPlayer)
    this.copy(players = ps, currentPlayer = newPlayer)
  }

  override def withUpdatedBoardAndPlayer(field: BoardField, player: Player): IMonopolyGame = {
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
}