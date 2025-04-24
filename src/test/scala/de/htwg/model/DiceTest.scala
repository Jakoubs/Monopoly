package de.htwg.model
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
class DiceTest extends AnyWordSpec {
  "Dice" should {
    "be in range 1-6" in {
      val (d1, d2) = Dice().rollDice(false)
      d1 should (be >= 1 and be <= 6)
      d2 should (be >= 1 and be <= 6)
    }
    " return the params if added" in {
      val (d1, d2) = Dice().rollDice(1, 5)
      d1 should (be(1) and be >= 1 and be <= 6)
      d2 should (be(5) and be >= 1 and be <= 6)
    }
    " return custom" in {
      val (d1, d2) = Dice().rollDice(7, 5)
      d1 should be(7)
      d2 should be(5)
    }

  }
}
