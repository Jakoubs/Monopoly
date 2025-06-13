package de.htwg.model.modelMockImpl

import de.htwg.model.IPlayer
import de.htwg.model.modelBaseImple.Player
import scala.util.{Try, Success}

object MockPlayer extends IPlayer {
  override def name: String = "Mock"
  override def balance: Int = 0
  override def position: Int = 1
  override def isInJail: Boolean = false
  override def consecutiveDoubles: Int = 0

  override def moveToIndex(player: de.htwg.model.modelBaseImple.Player, index: Int) = player
  override def incrementDoubles(player: de.htwg.model.modelBaseImple.Player) = player
  override def resetDoubles(player: de.htwg.model.modelBaseImple.Player) = player
  override def releaseFromJail(player: de.htwg.model.modelBaseImple.Player) = player
  override def changeBalance(player: de.htwg.model.modelBaseImple.Player, amount: Int) = scala.util.Success(player)
  override def goToJail(player: de.htwg.model.modelBaseImple.Player) = player
}