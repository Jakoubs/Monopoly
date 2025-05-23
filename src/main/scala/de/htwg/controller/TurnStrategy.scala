package de.htwg.controller
import de.htwg.model.{Dice, Player}

trait TurnStrategy {
  def executeTurn(player: Player, dice: () => (Int, Int)): Player
}
case class RegularTurnStrategy() extends TurnStrategy {
  override def executeTurn(player: Player, dice: () => (Int, Int)): Player = {
    if (player.isInJail) return player

    val (diceA, diceB) = dice()

    val newPosition = (player.position + diceA + diceB) % 40
    val updatedPlayer = if ((player.position + diceA + diceB) > 40) {
      player.copy(balance = player.balance + 200)
    } else {
      player
    }
    updatedPlayer.moveToIndex(newPosition)
  }
}

case class JailTurnStrategy() extends TurnStrategy {
  override def executeTurn(player: Player, dice: () => (Int, Int)): Player = {
    if (player.isInJail) {
      val (diceA, diceB) = dice()
      if (diceA == diceB) {
        val updatedPlayer = player.releaseFromJail()
        val updatedPlayerMove = updatedPlayer.moveToIndex((player.position + diceA + diceB) % 40)
        val updatedPlayerDoubels = updatedPlayerMove.resetDoubles()
        updatedPlayerDoubels
      } else {
        val jailTurns = player.consecutiveDoubles + 1
        if (jailTurns >= 3) {
          val updatedPlayer = player.copy(consecutiveDoubles = 0)
            .changeBalance(-50)
            .releaseFromJail()
          updatedPlayer
        } else {
          player.copy(consecutiveDoubles = jailTurns)
        }
      }
    } else player
  }
}