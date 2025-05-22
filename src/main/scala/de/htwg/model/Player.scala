package de.htwg.model
import scala.io.StdIn.readLine
import de.htwg.model.Dice
import de.htwg.MonopolyGame

case class Player(name: String,
                  balance: Int,
                  position: Int = 1,
                  isInJail: Boolean = false,
                  consecutiveDoubles: Int
                 ) {

  def moveToIndex(index: Int): Player = {
    if (!isInJail) {
      if(index == 0){
        val newPlayer = this.copy(position = 40)
        return newPlayer
      }else {
        val newPlayer = this.copy(position = index)
        return newPlayer
      }
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