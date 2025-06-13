package de.htwg.model.modelBaseImple

import de.htwg.model.modelBaseImple.MonopolyGame
import de.htwg.model.*

import scala.io.StdIn.readLine

case class Player(name: String,
                  balance: Int,
                  position: Int = 1,
                  isInJail: Boolean = false,
                  consecutiveDoubles: Int = 0
                 ) {

  def moveToIndex(index: Int): Player = {
    if (!isInJail) {
        val newPlayer = this.copy(position = index)
        return newPlayer
    }
    this
  }
  
  def incrementDoubles(): Player = copy(consecutiveDoubles = consecutiveDoubles + 1)
  def resetDoubles(): Player = copy(consecutiveDoubles = 0)

  def releaseFromJail(): Player = {
    val newPlayer = this.copy(isInJail = false)
    newPlayer
  }
  def changeBalance(amount: Int): Player = {
    val newPlayer = this.copy(balance = balance + amount)
    newPlayer
  }

  def goToJail(): Player = {
    val newPlayer = this.copy(position = 11, isInJail = true,consecutiveDoubles = 0)
    newPlayer
  }

}

trait TurnStrategy {
  def executeTurn(player: Player, dice: () => (Int, Int)): Player
}