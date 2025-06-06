package de.htwg.model

import de.htwg.model.modelBaseImple.Player
import scala.util.Try 

trait IPlayer {
  def name: String
  def balance: Int
  def position: Int
  def isInJail: Boolean
  def consecutiveDoubles: Int

  def moveToIndex(player: Player, index: Int): Player
  def incrementDoubles(player: Player): Player
  def resetDoubles(player: Player): Player
  def releaseFromJail(player: Player): Player
  def changeBalance(player: Player, amount: Int): Try[Player]
  def goToJail(player: Player): Player
  
  }