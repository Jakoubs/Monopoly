package de.htwg.controller

import de.htwg.{Board, MonopolyGame}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.controller.*
import de.htwg.model.*
import de.htwg.model.PropertyField.Color.*
import de.htwg.util.util.Observable

class GameStateSpec extends AnyWordSpec with Matchers {
  val dice = new Dice()
  val mockAsk: String => Boolean = _ => true // Simuliert immer "ja" als Antwort
  val mockPrint: String => Unit = _ => () // Tut nichts beim Drucken
  val mockChoice: () => Int = () => 1 // Gibt immer 1 zurück
  val player1 = Player("Player 1", 1500, 1, isInJail = false, 0)
  val player2 = Player("Player 2", 1500, 1, isInJail = false, 0)
  val fields = Vector(
    GoField,
    PropertyField("brown1", 2, 100, 10, None, color = Brown, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    CommunityChestField(3),
    PropertyField("brown2", 4, 100, 10, None, color = Brown, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    TaxField(100, 5),
    TrainStationField("Marklylebone Station", 6, 200, None),
    PropertyField("lightBlue1", 7, 100, 10, None, color = LightBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    ChanceField(8),
    PropertyField("lightBlue2", 9, 100, 10, None, color = LightBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    PropertyField("lightBlue3", 10, 100, 10, None, color = LightBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    JailField,
    PropertyField("Pink1", 12, 100, 10, None, color = Pink, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    UtilityField("Electric Company", 13, 150, UtilityField.UtilityCheck.utility, None),
    PropertyField("Pink2", 14, 100, 10, None, color = Pink, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    PropertyField("Pink3", 15, 100, 10, None, color = Pink, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    TrainStationField("Fenchurch ST Station", 16, 200, None),
    PropertyField("Orange1", 17, 100, 10, None, color = Orange, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    CommunityChestField(18),
    PropertyField("Orange2", 19, 100, 10, None, color = Orange, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    PropertyField("Orange3", 20, 100, 10, None, color = Orange, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    FreeParkingField(0),
    PropertyField("Red1", 22, 100, 10, None, color = Red, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    ChanceField(23),
    PropertyField("Red2", 24, 100, 10, None, color = Red, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    PropertyField("Red3", 25, 100, 10, None, color = Red, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    TrainStationField("King's Cross Station", 26, 200, None),
    PropertyField("Yellow1", 27, 100, 10, None, color = Yellow, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    UtilityField("Water Works", 28, 150, UtilityField.UtilityCheck.utility, None),
    PropertyField("Yellow2", 29, 100, 10, None, color = Yellow, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    PropertyField("Yellow3", 30, 100, 10, None, color = Yellow, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    GoToJailField(),
    PropertyField("Green1", 32, 100, 10, None, color = Green, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    PropertyField("Green2", 33, 100, 10, None, color = Green, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    CommunityChestField(34),
    PropertyField("Green3", 35, 100, 10, None, color = Green, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    TrainStationField("Liverpool ST Station", 36, 200, None),
    ChanceField(37),
    PropertyField("DarkBlue1", 38, 100, 10, None, color = DarkBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    TaxField(200, 39),
    PropertyField("DarkBlue2", 40, 100, 10, None, color = DarkBlue, PropertyField.Mortgage(10, false), PropertyField.House(0))
  )

  val board = Board(fields)
  val initialGame = MonopolyGame(Vector(player1, player2), board, player1, sound = false)
  val controller = new Controller(initialGame, dice)

  "StartTurnState" should {
    "go to JailState if player is in jail" in {
      controller.updatePlayer(player1.copy(isInJail = true))
      val state = StartTurnState().handle("", controller)
      state shouldBe a[JailState]
    }

    "go to RollingState if player is not in jail" in {
      controller.updatePlayer(player1.copy(isInJail = false))
      val state = StartTurnState().handle("", controller)
      state shouldBe a[RollingState]
    }
  }

  "RollingState" should {
    "roll the dice and go to MovingState" in {
      val state = RollingState().handle("", controller)
      state shouldBe a[MovingState]
    }
  }

  "EndTurnState" should {
    "switch to next player and return StartTurnState" in {
      val state = EndTurnState().handle("", controller)
      state shouldBe a[StartTurnState]
    }
  }

  "MovingState" should {
    "go to PropertyDecisionState when landing on buyable property" in {
      controller.updatePlayer(player1.copy(position = 1)) // Property "brown1"
      val state = MovingState(() => (1, 0)).handle("", controller)
      state shouldBe a[PropertyDecisionState]
    }

    "go to AdditionalActionsState when landing on empty field" in {
      controller.updatePlayer(player1.copy(position = 2)) // CommunityChestField
      val state = MovingState(() => (1, 0)).handle("", controller)
      state shouldBe a[AdditionalActionsState]
    }

    "go to AdditionalActionsState after GoToJailField" in {
      controller.updatePlayer(player1.copy(position = 30)) // GoToJail
      val state = MovingState(() => (1, 0)).handle("", controller)
      state shouldBe a[AdditionalActionsState]
      controller.currentPlayer.isInJail shouldBe true
    }
  }
}