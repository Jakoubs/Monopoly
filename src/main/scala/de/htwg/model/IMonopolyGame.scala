package de.htwg.model

import de.htwg.Board
import de.htwg.controller.{GameState, StartTurnState}


trait IMonopolyGame {
  def players: Vector[IPlayer]
  def board: Board
  def currentPlayer: IPlayer
  def sound: Boolean
  def state: GameState = StartTurnState()

  def rollDice(): (Int, Int)
  def movePlayer(steps: Int): IMonopolyGame
  def buyProperty(): (Boolean, IMonopolyGame)
  def endTurn(): IMonopolyGame
  def toggleSound(): IMonopolyGame
  
}