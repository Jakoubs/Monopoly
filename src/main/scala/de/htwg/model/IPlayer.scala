package de.htwg.model

import de.htwg.model.IPlayer
import de.htwg.model.modelBaseImple.Player

import scala.util.Try

trait IPlayer {
  def name: String
  def balance: Int
  def position: Int
  def isInJail: Boolean
  def consecutiveDoubles: Int

  def moveToIndex(index: Int): IPlayer
  def incrementDoubles: IPlayer
  def resetDoubles: IPlayer
  def releaseFromJail: IPlayer
  def changeBalance(amount: Int): Try[IPlayer]
  def goToJail: IPlayer
  
  }
object IPlayer {
  def create(
              name: String,
              balance: Int,
              position: Int = 1,
              isInJail: Boolean = false,
              consecutiveDoubles: Int = 0): IPlayer = Player(name, balance, position, isInJail, consecutiveDoubles)
}