package de.htwg.model.modelMockImpl

import de.htwg.model.IPlayer
import scala.util.{Try, Success}

object MockPlayer extends IPlayer {
  override def name: String = "Mock"
  override def balance: Int = 0
  override def position: Int = 1
  override def isInJail: Boolean = false
  override def consecutiveDoubles: Int = 0

  override def moveToIndex(index: Int): IPlayer = this
  override def incrementDoubles: IPlayer = this
  override def resetDoubles: IPlayer = this
  override def releaseFromJail: IPlayer = this
  override def changeBalance(amount: Int): Try[IPlayer] = Success(this)
  override def goToJail: IPlayer = this
}