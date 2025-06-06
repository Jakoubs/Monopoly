package de.htwg.model

trait DiceInterface {
  def rollDice(valid: Boolean): (Int, Int)
  def rollDice(d1: Int, d2: Int): (Int, Int)
}
