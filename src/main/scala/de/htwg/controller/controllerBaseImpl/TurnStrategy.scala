package de.htwg.controller

import de.htwg.model.IPlayer

trait TurnStrategy {
  def executeTurn(player: IPlayer, dice: () => (Int, Int)): IPlayer
}

case class RegularTurnStrategy() extends TurnStrategy {
  override def executeTurn(player: IPlayer, dice: () => (Int, Int)): IPlayer = {
    val (diceA, diceB) = dice()
    val newPosition = (player.position + diceA + diceB) % 40
    val passedGo = (player.position + diceA + diceB) > 40

    val updatedPlayer = if (passedGo) {
      player.changeBalance(200).getOrElse(player)
    } else player

    updatedPlayer.moveToIndex(newPosition)
  }
}

case class JailTurnStrategy() extends TurnStrategy {
  override def executeTurn(player: IPlayer, dice: () => (Int, Int)): IPlayer = {
    if (player.isInJail) {
      val (diceA, diceB) = dice()
      if (diceA == diceB) {
        val updatedPlayer = player.releaseFromJail
        val updatedPlayerMove = updatedPlayer.moveToIndex(((player.position + diceA + diceB) % 40))
        val updatedPlayerDoubles = updatedPlayerMove.resetDoubles
        updatedPlayerDoubles
      } else {
        val jailTurns = player.consecutiveDoubles + 1
        if (jailTurns >= 3) {
          player.releaseFromJail
            .changeBalance(-50)
            .getOrElse(player)
        } else {
          player.incrementDoubles
          player.incrementDoubles
        }
      }
    } else player
  }
}