package de.htwg.model

import de.htwg.model.IPlayer
import de.htwg.model.modelBaseImple.Player
import scala.util.Try
import de.htwg.model.modelBaseImple.BoardField // Importieren, falls BoardField benötigt wird
import de.htwg.model.modelBaseImple.BuyableField // Importieren, falls BuyableField benötigt wird

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
  def changeBalance(amount: Int): IPlayer
  def goToJail: IPlayer
  def copyPlayer(balance: Int = this.balance,
                 position: Int = this.position,
                 isInJail: Boolean = this.isInJail,
                 consecutiveDoubles: Int = 0): IPlayer
  def getProperties(boardFields: Vector[BoardField]): Vector[BuyableField]
}
object IPlayer {
  def create(
              name: String,
              balance: Int,
              position: Int = 1,
              isInJail: Boolean = false,
              consecutiveDoubles: Int = 0): IPlayer = Player(name, balance, position, isInJail, consecutiveDoubles)
}