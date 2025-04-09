package de.htwg.model

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class TradeSpec extends AnyWordSpec {
  "Trade" should {
    "be able to trade" in {
      val p1 = new Player("TestPlayer1",0)
      val p2 = new Player("TestPlayer2",0)
      //val FieldForTradeP1 = new Field()
      //val FieldList = BoardField();
      val d1 = 1
      //trade(p1,p2,100)
      d1 should be(1)
    }

    }
}