package de.htwg.model
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
class DiceTest extends AnyWordSpec {
  "Dice" should {
    "be in range 1-6" in {
      val (d1, d2) = Dice().rollDice()
      d1 should (be >= 1 and be <= 6)
      d2 should (be >= 1 and be <= 6)
    }
    " return the params if added" in {
      val (d1, d2) = Dice().rollDice(1, 5)
      d1 should (be(1) and be >= 1 and be <= 6)
      d2 should (be(5) and be >= 1 and be <= 6)
    }
    " return -1 -1 when there are numbers out of range" in {
      val (d1, d2) = Dice().rollDice(1, 5)
      d1 should (be(1) and be >= 1 and be <= 6)
      d2 should (be(5) and be >= 1 and be <= 6)
    }
  }
}
