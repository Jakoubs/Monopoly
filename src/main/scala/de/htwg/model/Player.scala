package de.htwg.model

case class Player(name: String,
                  balance: Int,
                  position: Int = 0,
                  isInJail: Boolean = false,
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
                 rollcount: Int = 1): Player = {
    if (rollcount == 3) {
      this.goToJail()
    } else if(!isInJail) {
      val (diceA, diceB) = rollDice()
      val updatedPlayer = if ((position + diceA + diceB) > 40) 
        this.copy(balance = balance + 200) else this 

      val newPlayer = updatedPlayer.moveToIndex((position + diceA + diceB) % 40)
      if (diceA == diceB) {
        return newPlayer.playerMove(rollDice,rollcount + 1)
      }
      newPlayer
    } else {
      this
    }
  }

}