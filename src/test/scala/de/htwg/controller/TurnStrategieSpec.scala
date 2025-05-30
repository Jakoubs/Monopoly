package de.htwg.controller

import de.htwg.model.Player
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TurnStrategySpec extends AnyWordSpec with Matchers {

  "RegularTurnStrategy" should {
    "move a player and not give extra turn if dice are different" in {
      val strategy = RegularTurnStrategy()
      val initialPlayer = Player("Test Player", 1500, 5, isInJail = false, 0)
      val mockDice = () => (3, 4)
      val updatedPlayer = strategy.executeTurn(initialPlayer, mockDice)
      updatedPlayer.position should be(12)
      updatedPlayer.consecutiveDoubles should be(0)
    }

    "move a player and give extra turn if dice are the same" in {
      val strategy = RegularTurnStrategy()
      val initialPlayer = Player("Test Player", 1500, 5, isInJail = false, 0)
      val mockDice = () => (3, 3)
      val updatedPlayer = strategy.executeTurn(initialPlayer, mockDice)
      updatedPlayer.position should be(11)
      updatedPlayer.consecutiveDoubles should be(0)
    }

    "move a player past Go and collect money" in {
      val strategy = RegularTurnStrategy()
      val initialPlayer = Player("Test Player", 1500, 38, isInJail = false, 0)
      val mockDice = () => (1, 3)
      val updatedPlayer = strategy.executeTurn(initialPlayer, mockDice)
      updatedPlayer.position should be(2)
      updatedPlayer.balance should be(1700)
    }

    "not move a player if they are in jail" in {
      val strategy = RegularTurnStrategy()
      val initialPlayer = Player("Test Player", 1500, 5, isInJail = true, 0)
      val mockDice = () => (3, 4)
      val updatedPlayer = strategy.executeTurn(initialPlayer, mockDice)
      updatedPlayer.position should be(5)
      updatedPlayer.isInJail should be(true)
    }
  }

  "JailTurnStrategy" should {
    "release a player from jail and move if doubles are rolled on the first try" in {
      val strategy = JailTurnStrategy()
      val initialPlayer = Player("Test Player", 1500, 10, isInJail = true, 1)
      val mockDice = () => (2, 2)
      val updatedPlayer = strategy.executeTurn(initialPlayer, mockDice)
      updatedPlayer.isInJail should be(false)
      updatedPlayer.position should be(14)
      updatedPlayer.consecutiveDoubles should be(0)

    }

    "increment jail turns if no doubles are rolled" in {
      val strategy = JailTurnStrategy()
      val initialPlayer = Player("Test Player", 1500, 10, isInJail = true, 1)
      val mockDice = () => (3, 4)
      val updatedPlayer = strategy.executeTurn(initialPlayer, mockDice)
      updatedPlayer.isInJail should be(true)
      updatedPlayer.consecutiveDoubles should be(2)

      updatedPlayer.position should be(10) // Position sollte sich nicht ändern
    }

    "release a player from jail and deduct money after 3 failed attempts" in {
      val strategy = JailTurnStrategy()
      val initialPlayer = Player("Test Player", 1500, 10, isInJail = true, 2)
      val mockDice = () => (3, 4)
      val updatedPlayer = strategy.executeTurn(initialPlayer, mockDice)
      updatedPlayer.isInJail should be(false)
      updatedPlayer.balance should be(1450)
      updatedPlayer.consecutiveDoubles should be(0)
      updatedPlayer.position should be(10) // Position sollte sich nicht ändern
    }

    "not affect a player who is not in jail" in {
      val strategy = JailTurnStrategy()
      val initialPlayer = Player("Test Player", 1500, 5, isInJail = false, 0)
      val mockDice = () => (1, 2)
      val updatedPlayer = strategy.executeTurn(initialPlayer, mockDice)
      updatedPlayer should be(initialPlayer)
    }
  }
}