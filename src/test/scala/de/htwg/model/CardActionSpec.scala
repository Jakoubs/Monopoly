package de.htwg.model

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class CardActionSpec extends AnyWordSpec {

  "GainMoney" should {
    "increase the player's balance by the given amount" in {
      val player = Player("TestPlayer", 1000, 5)
      val action = GainMoney(200)
      val updatedPlayer = action.apply(player)
      updatedPlayer.balance shouldEqual 1200
    }
  }

  "LoseMoney" should {
    "decrease the player's balance by the given amount" in {
      val player = Player("TestPlayer", 1000, 5)
      val action = LoseMoney(200)
      val freeParkingField = FreeParkingField(0)
      val (updatedPlayer, updatedField) = action.apply(player,freeParkingField)
      updatedPlayer.balance shouldEqual 800
    }
  }

  "CardToJail" should {
    "send the player to jail" in {
      val player = Player("TestPlayer", 1000, 5)
      val action = CardToJail
      val updatedPlayer = action.apply(player)
      updatedPlayer.isInJail shouldBe true
      updatedPlayer.position shouldEqual 11
    }
  }

  "CardMoveTo" should {
    "move the player to the specified index" in {
      val player = Player("TestPlayer", 1000, 5)
      val action = CardMoveTo(10,false)
      val updatedPlayer = action.apply(player)
      updatedPlayer.position shouldEqual 10
    }
    "if collectMoney -> true collect money on Go" in{
      val player = Player("TestPlayer", 1000, 30)
      val action = CardMoveTo(1,true)
      val updatedPlayer = action.apply(player)
      updatedPlayer.position should be(1)
      updatedPlayer.balance should be(1200)
    }
    "if collectMoney -> false collect no money" in {
      val player = Player("TestPlayer", 1000, 30)
      val action = CardMoveTo(1, false)
      val updatedPlayer = action.apply(player)
      updatedPlayer.position should be(1)
      updatedPlayer.balance should be(1000)
    }
    "if collectMoney -> true and no move over GO collect no money" in {
      val player = Player("TestPlayer", 1000, 30)
      val action = CardMoveTo(35, true)
      val updatedPlayer = action.apply(player)
      updatedPlayer.position should be(35)
      updatedPlayer.balance should be(1000)
    }
  }
}
