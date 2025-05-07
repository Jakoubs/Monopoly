package de.htwg.model
import scala.io.StdIn.readLine

case class Player(name: String,
                  balance: Int,
                  position: Int = 0,
                  isInJail: Boolean = false,
                  jailTurns: Int = 0
                  //properties: List[PropertyField] = List()
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

  def releaseFromJail(): Player = {
    val newPlayer = this.copy(isInJail = false)
    newPlayer
  }

  def goToJail(): Player = {
    val newPlayer = this.copy(position = 11, isInJail = true)
    newPlayer
  }

  def playerMove(rollDice: () => (Int, Int),
                 rollcount: Int = 0): Player = {
    if (rollcount == 3) {
      this.goToJail()
    } else if(!isInJail) {
      println("press enter to roll dice")
      readLine("Rolling dice...")
      val rand = new scala.util.Random
      val rollNewDice = () => (rand.nextInt(6) + 1, rand.nextInt(6) + 1)
      val (diceA, diceB) = rollNewDice()
      val updatedPlayer = if ((position + diceA + diceB) > 40)
        this.copy(balance = balance + 200) else this
      println(s"You rolled $diceA and $diceB! That's ${diceA + diceB} moves.")
      println(s"Your new position is ${(position + diceA + diceB) % 40}")

      val newPlayer = updatedPlayer.moveToIndex((position + diceA + diceB) % 40)
      if (diceA == diceB) {
        println("You rolled doubles -> Roll again")
        return newPlayer.playerMove(rollDice,rollcount + 1)
      }
      newPlayer
    } else {
      println("You rolled doubles 3 times -> Jail :(")
      this
    }
  }

}