package de.htwg.controller
import de.htwg.model.{Player}

trait TurnStrategy {
  def executeTurn(player: Player, dice: () => (Int, Int)): Player
}
case class RegularTurnStrategy() extends TurnStrategy {
  override def executeTurn(player: Player, dice: () => (Int, Int)): Player = {
    if (!player.isInJail) {
      val (diceA, diceB) = dice()
      val updatedPlayer = if ((player.position + diceA + diceB) > 40)
        player.copy(balance = player.balance + 200)
      else player

      val newPlayer = updatedPlayer.moveToIndex((player.position + diceA + diceB) % 40)
      if (diceA == diceB) {
        return newPlayer.playerMove(dice, 1)
      }
      newPlayer
    } else player
  }
}

case class JailTurnStrategy() extends TurnStrategy {
  override def executeTurn(player: Player, dice: () => (Int, Int)): Player = {
    if (player.isInJail) {
      val (diceA, diceB) = dice()
      if (diceA == diceB) {
        player.releaseFromJail()
          .moveToIndex((player.position + diceA + diceB) % 40)
      } else {
        val jailTurns = player.jailTurns + 1
        if (jailTurns >= 3) {
          player.copy(jailTurns = 0)
            .changeBalance(-50)
            .releaseFromJail()
        } else {
          player.copy(jailTurns = jailTurns)
        }
      }
    } else player
  }
}