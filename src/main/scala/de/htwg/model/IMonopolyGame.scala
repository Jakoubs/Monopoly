package de.htwg.model

import de.htwg.Board
import de.htwg.model.modelBaseImple.{BoardField, GameState, Player, PropertyField, StartTurnState}
import de.htwg.controller.{Controller, OpEnum}

import scala.util.Try


trait IMonopolyGame {
  def players: Vector[Player]
  def board: Board
  def currentPlayer: Player
  def sound: Boolean
  def state: GameState = StartTurnState()

  def rollDice(valid: Boolean): (Int, Int) 
  def rollDice(dice1: Int, dice2: Int): IMonopolyGame
  
  def movePlayer(steps: Int): IMonopolyGame
  def endTurn(): IMonopolyGame
  def toggleSound(): IMonopolyGame

  def withUpdatedPlayer(newPlayer: Player): IMonopolyGame
  def withUpdatedBoardAndPlayer(field: BoardField, player: Player): IMonopolyGame
  def withNextPlayer: IMonopolyGame

  def handle(input: OpEnum, controller: Controller): IMonopolyGame

  def buyHouse(field: PropertyField, player: Player): Try[IMonopolyGame]
}