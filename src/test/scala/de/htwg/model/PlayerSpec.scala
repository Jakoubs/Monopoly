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
    }

    "be able to change position index when not in jail" in {
      val player = Player("TestPlayer", 100, 5)
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
      val player = Player("TestPlayer", 100)
      val updatedPlayer = player.goToJail()
      updatedPlayer.position shouldEqual 11
      updatedPlayer.isInJail should be(true)
    }

    "roll the dice and move position" in {
      val player = Player("TestPlayer", 100)
      val mockRollDice = () => (3, 4)
      val updatedPlayer = player.playerMove(mockRollDice)
      updatedPlayer.position shouldEqual 8
    }

    "remain in jail when trying to move while jailed" in {
      val player = Player("TestPlayer", 2000, position = 10, isInJail = true)
      val mockRollDice = () => (5, 6)
      val updatedPlayer = player.playerMove(mockRollDice)
      updatedPlayer.position shouldEqual 10
      updatedPlayer.isInJail shouldEqual true
    }

    "move to jail after rolling doubles three times" in {
      val player = Player("TestPlayer", 1500)
      val mockRollDice = () => (6, 6)

      val jailedPlayer = player.playerMove(mockRollDice)
      jailedPlayer.position shouldEqual 11
      jailedPlayer.isInJail shouldEqual true
    }

    "never be on index 0" in {
      val player = Player("TestPlayer", 2000, position = 35)
      val mockRollDice = () => (2, 3)
      val updatedPlayer = player.playerMove(mockRollDice)
      updatedPlayer.position shouldEqual 40
    }

    "never be on index over 40" in {
      val player = Player("TestPlayer", 2000, position = 35)
      val mockRollDice = () => (2, 5)
      val updatedPlayer = player.playerMove(mockRollDice)
      updatedPlayer.position shouldEqual 2
    }

    "change balance when collecting money" in {
      val player = Player("TestPlayer", 1000, position = 35)
      val updatedPlayer = player.changeBalance(50)
      updatedPlayer.balance shouldEqual 1050
    }

    "earn 200 Money when go over Go" in {
      val player = Player("TestPlayer", 1000, position = 35)
      val mockRollDice = () => (4, 3)
      val updatedPlayer = player.playerMove(mockRollDice)
      updatedPlayer.balance shouldEqual 1200
    }

  }
}
