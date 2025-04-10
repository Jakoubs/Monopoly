package de.htwg.model
import de.htwg.Monopoly

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class MonopolySpec extends AnyWordSpec with Matchers {
  "Monopoly main" should {
    "run without throwing an exception" in {
      noException should be thrownBy Monopoly.main(Array.empty)
    }
  }
}
