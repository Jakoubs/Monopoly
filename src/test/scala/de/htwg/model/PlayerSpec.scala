package de.htwg.model

import org.scalatest.matchers.should.Matchers.{shouldEqual, *}
import org.scalatest.wordspec.AnyWordSpec

class PlayerSpec extends AnyWordSpec {

  "Player" should {
    "have a name, a balance, a position and a Jail status" in {
      val player = Player("TestPlayer", 1000, 5)
      player.name should be("TestPlayer")
      player.balance should be(1000)
      player.position should be(5)
      player.isInJail should be(false)
      player.consecutiveDoubles should be(0)
    }

    "be able to change position index when not in jail" in {
      val player = Player("TestPlayer", 100, 5)
      val updatedPlayer = player.moveToIndex(4)
      updatedPlayer.position shouldEqual 4
      updatedPlayer.isInJail should be(false)
    }

    "increase consecutiveDoubles when incrementDoubles" in {
      val player = Player("TestPlayer", 100, 5)
      val updatedPlayer = player.incrementDoubles()
      updatedPlayer.consecutiveDoubles shouldEqual 1
    }

    "not be able to change index when in jail" in {
      val player = Player("TestPlayer", 100 ,5 ,true)
      val updatedPlayer = player.moveToIndex(4)
      updatedPlayer.position shouldEqual 5
      updatedPlayer.isInJail should be(true)
    }

    "be released from Jail" in {
      val player = Player("TestPlayer", 100, 5, true)
      val updatedPlayer = player.releaseFromJail()
      updatedPlayer.isInJail should be(false)
    }

    "go to Jail" in {
      val player = Player("TestPlayer", 100)
      val updatedPlayer = player.goToJail()
      updatedPlayer.position shouldEqual 11
      updatedPlayer.isInJail should be(true)
    }
    "change balance when collecting money" in {
      val player = Player("TestPlayer", 1000, position = 35)
      val updatedPlayer = player.changeBalance(50)
      updatedPlayer.balance shouldEqual 1050
    }

  }
}
