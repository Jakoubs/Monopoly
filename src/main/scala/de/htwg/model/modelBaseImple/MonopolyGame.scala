package de.htwg.model.modelBaseImple

import de.htwg.Board
import de.htwg.controller.GameState

case class MonopolyGame(
                         players: Vector[Player],
                         board: Board,
                         currentPlayer: Player,
                         sound: Boolean,  
                         state: GameState
                       )