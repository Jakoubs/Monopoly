package de.htwg.model

import de.htwg.model.Player
import org.scalatest.matchers.should.Matchers.{shouldEqual, *}
import org.scalatest.wordspec.AnyWordSpec

class PlayerSpec extends AnyWordSpec {

  "Player" should {
    "have a name, a balance, a position and a Jail status" in {
      val player = Player("TestPlayer", 1000, 5, false)
      player.name should be("TestPlayer")
      player.balance should be(1000)
      player.position should be(5)
      player.isInJail should be(false)
    }

    "be able to change position index when not in jail" in {
      val player = Player("TestPlayer", 100, 5, false)
      val updatedPlayer = player.moveToIndex(4)
      updatedPlayer.position shouldEqual 4
      updatedPlayer.isInJail should be(false)
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
      val player = Player("TestPlayer", 100, 0, false)
      val updatedPlayer = player.goToJail()
      updatedPlayer.position shouldEqual 11
      updatedPlayer.isInJail should be(true)
    }

    "roll the dice and move position" in {
      val player = Player("TestPlayer", 100,0,false)
      val updatedPlayer = player.playerMove(1)
      updatedPlayer.position should be > 0
    }

    "remain in jail when trying to move while jailed" in {
      val player = Player("TestPlayer", 2000, position = 10, isInJail = true)
      val updatedPlayer = player.playerMove(1)
      updatedPlayer.position shouldEqual 10
      updatedPlayer.isInJail shouldEqual true
    }

    "move to jail after rolling doubles three times" in {
      val player = Player("TestPlayer", 1500)
      val jailedPlayer = player.playerMove(3)
      jailedPlayer.position shouldEqual 11
      jailedPlayer.isInJail shouldEqual true
    }

    "never be above index 40" in {
      val player = Player("TestPlayer", 1500,40)
      val jailedPlayer = player.playerMove(1)
      jailedPlayer.position should be < 40
    }
  }
}
