package de.htwg.model

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class TradeSpec extends AnyWordSpec {
  "Trade" should {
    "be able to trade when only p1 gets Money" in {
      val p1 = new Player("TestPlayer1",1500)
      val p2 = new Player("TestPlayer2",200)
      //val FieldForTradeP1 = new Field()
      //val FieldList = BoardField();
      val tradeAmountP1ToP2 = 0
      val tradeAmountP2ToP1 = 200
      val d1 = 1
      Trade().tradeCall(p1,p2,tradeAmountP2ToP1, tradeAmountP1ToP2) should be(true)
      p2.balance should be(0)
      p1.balance should be(1700)
    }
    "be able to trade wehn only p2 gets Money" in {
      val p1 = new Player("TestPlayer1", 1500)
      val p2 = new Player("TestPlayer2", 200)
      //val FieldForTradeP1 = new Field()
      //val FieldList = BoardField();
      val tradeAmountP1ToP2 = 1500
      val tradeAmountP2ToP1 = 0
      val d1 = 1
      Trade().tradeCall(p1,p2,tradeAmountP2ToP1, tradeAmountP1ToP2) should be(true)
      p1.balance should be(0)
      p2.balance should be(1700)
    }
    "deliver false, if the balance of p2 is lower than the trade offer" in {
      val p1 = new Player("TestPlayer1", 1500)
      val p2 = new Player("TestPlayer2", 200)
      //val FieldForTradeP1 = new Field()
      //val FieldList = BoardField();
      val tradeAmountP1ToP2 = 0
      val tradeAmountP2ToP1 = 3000
      val d1 = 1
      Trade().tradeCall(p1, p2, tradeAmountP1ToP2, tradeAmountP2ToP1) should be(false)
    }
    "deliver false, if the balance of p1 is lower than the trade offer" in {
      val p1 = new Player("TestPlayer1", 1500)
      val p2 = new Player("TestPlayer2", 200)
      //val FieldForTradeP1 = new Field()
      //val FieldList = BoardField();
      val tradeAmountP1ToP2 = 2000
      val tradeAmountP2ToP1 = 0
      val d1 = 1
      Trade().tradeCall(p1, p2, tradeAmountP1ToP2, tradeAmountP2ToP1) should be(false)
    }
    

    //wenn playerbalance negativ durch trade-> geht nicht

    }
}