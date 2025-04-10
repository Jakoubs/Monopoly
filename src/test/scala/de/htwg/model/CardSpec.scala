package de.htwg.model

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class CardSpec extends AnyWordSpec {

  "MoneyCard" should {
    "have a name, a description and an amount and an action" in {
      val card = MoneyCard("Cash Bonus", "Gain 200 money", 200)
      card.name shouldEqual "Cash Bonus"
      card.description shouldEqual "Gain 200 money"
      card.amount shouldEqual 200
      card.action shouldEqual GainMoney(200)
    }
  }

  "MoveCard" should {
    "have a name, a description and an index and an actio" in {
      val card = MoveCard("Move Ahead", "Move to position 10", 10)
      card.name shouldEqual "Move Ahead"
      card.description shouldEqual "Move to position 10"
      card.index shouldEqual 10
      card.action shouldEqual CardMoveTo(10)
    }
  }

  "PenaltyCard" should {
    "have a name, a description and an amount and an actio" in {
      val card = PenaltyCard("Penalty", "Lose 200 money", 200)
      card.name shouldEqual "Penalty"
      card.description shouldEqual "Lose 200 money"
      card.amount shouldEqual 200
      card.action shouldEqual LoseMoney(200)
    }
  }

  "JailCard" should {
    "have a name and a description and an actio" in {
      val card = JailCard("Go to Jail","Move directly to jail")
      card.name shouldEqual "Go to Jail"
      card.description shouldEqual "Move directly to jail"
      card.action shouldEqual CardToJail
    }
  }
}

