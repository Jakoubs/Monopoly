package de.htwg.model.modelMockImpl

import de.htwg.model.PlayerInterface
import de.htwg.model.modelBaseImple.Player
import scala.util.{Try, Success}

object MockPlayer extends PlayerInterface {

  // Optional: ein Dummy-Player, falls du Standarddaten brauchst
  val dummyPlayer: Player = Player("Mocky", position = 1, balance = 1000, isInJail = false, consecutiveDoubles = 0)

  override def moveToIndex(player: Player, index: Int): Player =
    player.copy(position = index)

  override def incrementDoubles(player: Player): Player =
    player.copy(consecutiveDoubles = player.consecutiveDoubles + 1)

  override def resetDoubles(player: Player): Player =
    player.copy(consecutiveDoubles = 0)

  override def releaseFromJail(player: Player): Player =
    player.copy(isInJail = false)

  override def changeBalance(player: Player, amount: Int): Try[Player] =
    Success(player.copy(balance = player.balance + amount))

  override def goToJail(player: Player): Player =
    player.copy(position = 10, isInJail = true, consecutiveDoubles = 0)
}
