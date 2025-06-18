package de.htwg.model

import de.htwg.Board
import de.htwg.model.modelBaseImple.{BoardField, PropertyField}

import scala.util.Try


trait IMonopolyGame {
  def players: Vector[IPlayer]
  def board: Board
  def currentPlayer: IPlayer
  def sound: Boolean

  def rollDice(valid: Boolean): (Int, Int)
  def rollDice(dice1: Int, dice2: Int): IMonopolyGame

  def movePlayer(steps: Int): IMonopolyGame
  def endTurn(): IMonopolyGame
  def toggleSound(): IMonopolyGame

  def withUpdatedPlayer(newPlayer: IPlayer): IMonopolyGame
  def withUpdatedBoardAndPlayer(field: BoardField, player: IPlayer): IMonopolyGame
  def withNextPlayer: IMonopolyGame


  def buyHouse(field: PropertyField, player: IPlayer): Try[IMonopolyGame]
}