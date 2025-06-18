package de.htwg.model.modelBaseImple

import de.htwg.model.IPlayer
import scala.util.{Try, Success}

case class Player(
                   name: String,
                   balance: Int,
                   position: Int = 1,
                   isInJail: Boolean = false,
                   consecutiveDoubles: Int = 0
                 ) extends IPlayer {

  override def moveToIndex(index: Int): IPlayer = {
    if (!isInJail) this.copy(position = index) else this
  }

  override def incrementDoubles: IPlayer = {
    this.copy(consecutiveDoubles = consecutiveDoubles + 1)
  }

  override def resetDoubles: IPlayer = {
    this.copy(consecutiveDoubles = 0)
  }

  override def releaseFromJail: IPlayer = {
    this.copy(isInJail = false)
  }

  override def changeBalance(amount: Int): Try[IPlayer] = {
    Success(this.copy(balance = balance + amount))
  }

  override def goToJail: IPlayer = {
    this.copy(position = 11, isInJail = true, consecutiveDoubles = 0)
  }

  override def copyPlayer(balance: Int,
                          position: Int = 1,
                          isInJail: Boolean = false,
                          consecutiveDoubles: Int = 0
                         ): IPlayer = {
    this.copy(name = this.name,balance,position,isInJail,consecutiveDoubles)
  }
}

trait TurnStrategy {
  def executeTurn(player: Player, dice: () => (Int, Int)): Player
}