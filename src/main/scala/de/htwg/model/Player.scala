package de.htwg.model

case class Player(name: String,
                  balance: Int,
                  position: Int = 0,
                  isInJail: Boolean = false,
                  //properties: List[PropertyField] = List()
                 ) {

  def moveToIndex(index: Int): Player = {
    if (!isInJail) {
      val newPlayer = this.copy(position = index)
      return newPlayer
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

  def playerMove(rollcount: Int = 1): Player = {
    if (rollcount == 3) {
      println("You rolled doubles 3 times -> Jail :(")
      return this.goToJail()
    } else if(!isInJail) {
      val (diceA, diceB) = rollDice()
      val newPlayer = this.moveToIndex(diceA + diceB)
      if (diceA == diceB) {
        return newPlayer.playerMove(rollcount + 1)
      }
      newPlayer
    } else {
      this
    }
  }

}