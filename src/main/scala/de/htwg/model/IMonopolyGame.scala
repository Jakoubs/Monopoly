package de.htwg.model

import de.htwg.Board
import de.htwg.model.modelBaseImple.{GameState, StartTurnState}
import de.htwg.controller.{Controller, GameState, OpEnum, StartTurnState}
import de.htwg.model.modelBaseImple.{BoardField, Player}


trait IMonopolyGame {
  def players: Vector[Player]
  def board: Board
  def currentPlayer: Player
  def sound: Boolean
  def state: GameState = StartTurnState()

  def rollDice(): (Int, Int)
  def movePlayer(steps: Int): IMonopolyGame
  def buyProperty(): (Boolean, IMonopolyGame)
  def endTurn(): IMonopolyGame
  def toggleSound(): IMonopolyGame

  def withUpdatedPlayer(newPlayer: Player): IMonopolyGame
  def withUpdatedBoardAndPlayer(field: BoardField, player: Player): IMonopolyGame
  def withNextPlayer: IMonopolyGame

  def handle(input: OpEnum, controller: Controller): IMonopolyGame
}