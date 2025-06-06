package de.htwg.model.modelMockImpl

import de.htwg.model.DiceInterface

class MockDice(fixed1: Int = 1, fixed2: Int = 1) extends DiceInterface {

  override def rollDice(valid: Boolean): (Int, Int) =
    (fixed1, fixed2)

  override def rollDice(d1: Int, d2: Int): (Int, Int) =
    (d1, d2)

}