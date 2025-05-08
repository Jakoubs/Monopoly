package de.htwg.model

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class TradeSpec extends AnyWordSpec {
  "Trade" should {
    "be able to trade when only p1 gets Money" in {
      val p1 = Player("TestPlayer1", 1500)
      val p2 = Player("TestPlayer2", 200)

      val tradeAmountP1ToP2 = 0
      val tradeAmountP2ToP1 = 200

      val result = Trade().tradeCall(p1, p2, tradeAmountP1ToP2, tradeAmountP2ToP1)

      result should not be empty
      val (newP1, newP2) = result.get
      newP1.balance should be(1700)
      newP2.balance should be(0)
    }

    "be able to trade when only p2 gets Money" in {
      val p1 = Player("TestPlayer1", 1500)
      val p2 = Player("TestPlayer2", 200)

      val tradeAmountP1ToP2 = 1500
      val tradeAmountP2ToP1 = 0

      val result = Trade().tradeCall(p1, p2, tradeAmountP1ToP2, tradeAmountP2ToP1)

      result should not be empty
      val (newP1, newP2) = result.get
      newP1.balance should be(0)
      newP2.balance should be(1700)
    }

    "deliver false, if the balance of p2 is lower than the trade offer" in {
      val p1 = Player("TestPlayer1", 1500)
      val p2 = Player("TestPlayer2", 200)

      val tradeAmountP1ToP2 = 0
      val tradeAmountP2ToP1 = 3000

      Trade().tradeCall(p1, p2, tradeAmountP1ToP2, tradeAmountP2ToP1) should be(None)
    }

    "deliver false, if the balance of p1 is lower than the trade offer" in {
      val p1 = Player("TestPlayer1", 1500)
      val p2 = Player("TestPlayer2", 200)

      val tradeAmountP1ToP2 = 2000
      val tradeAmountP2ToP1 = 0

      Trade().tradeCall(p1, p2, tradeAmountP1ToP2, tradeAmountP2ToP1) should be(None)
    }

    "deliver false if every trade item is 0" in {
      val p1 = Player("TestPlayer1", 1500)
      val p2 = Player("TestPlayer2", 200)

      val tradeAmountP1ToP2 = 0
      val tradeAmountP2ToP1 = 0

      Trade().tradeCall(p1, p2, tradeAmountP1ToP2, tradeAmountP2ToP1) should be(None)
    }
  }
}
