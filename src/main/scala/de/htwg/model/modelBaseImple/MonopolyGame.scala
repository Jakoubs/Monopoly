package de.htwg.model.modelBaseImple

import de.htwg.Board
import de.htwg.model.IMonopolyGame
import de.htwg.model.Player
import de.htwg.controller.{GameState, StartTurnState}

case class MonopolyGame(
                        players: Vector[Player],
                        board: Board,
                        currentPlayer: Player,
                        sound: Boolean,
                        state: GameState
                      ) extends IMonopolyGame {
 def createGame: IMonopolyGame = {MonopolyGame(
   players = players,
   board = board,
   currentPlayer = players.head,
   sound = sound,
   state = StartTurnState()
 )}
}