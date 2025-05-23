package de.htwg.controller

import de.htwg.{Board, MonopolyGame}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.controller.*
import de.htwg.model.*
import de.htwg.model.PropertyField.Color.*
import de.htwg.util.util.Observable
import org.scalatest.matchers.should.Matchers.shouldBe

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
    FreeParkingField(100),
    PropertyField("Red1", 22, 100, 10, Some(player1), color = Red, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    ChanceField(23),
    PropertyField("Red2", 24, 100, 10, Some(player1), color = Red, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    PropertyField("Red3", 25, 100, 10, Some(player1), color = Red, PropertyField.Mortgage(10, false), PropertyField.House(0)),
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
      val state = StartTurnState().handle(OpEnum.enter, controller)
      state shouldBe a[JailState]
    }

    "go to RollingState if player is not in jail" in {
      controller.updatePlayer(player1.copy(isInJail = false))
      val state = StartTurnState().handle(OpEnum.enter, controller)
      state shouldBe a[RollingState]
    }
  }

  "RollingState" should {
    "roll the dice and go to MovingState" in {
      val state = RollingState().handle(OpEnum.enter, controller)
      state shouldBe a[MovingState]
    }
  }

  "EndTurnState" should {
    "switch to next player and return StartTurnState" in {
      val state = EndTurnState().handle(OpEnum.end, controller)
      state shouldBe a[StartTurnState]
    }
  }

  "MovingState" should {
    "go to PropertyDecisionState when landing on buyable property" in {
      controller.updatePlayer(player1.copy(position = 1)) // Property "brown1"
      val state = MovingState(() => (1, 0)).handle(OpEnum.enter, controller)
      state shouldBe a[PropertyDecisionState]
    }

    "go to AdditionalActionsState when landing on empty field" in {
      controller.updatePlayer(player1.copy(position = 2)) // CommunityChestField
      val state = MovingState(() => (1, 0)).handle(OpEnum.enter, controller)
      state shouldBe a[AdditionalActionsState]
    }

    "go to EndTurnState after GoToJailField" in {
      controller.updatePlayer(player1.copy(position = 30)) // GoToJail
      val state = MovingState(() => (1, 0)).handle(OpEnum.enter, controller)
      state shouldBe a[EndTurnState]
      controller.currentPlayer.isInJail shouldBe true
    }

    "use JailTurnStrategy when player is in jail" in {
      val jailedPlayer = player1.copy(isInJail = true)
      controller.updatePlayer(jailedPlayer)

      val state = MovingState(() => (3, 3)).handle(OpEnum.enter, controller)

      controller.currentPlayer.isInJail shouldBe false
      state should not be a[JailState]
    }

    "handle a buyableField with an owner and end in additionalState" in {
      val player9 = player1.copy(position = 20, balance = 1500)
      val initialGame2 = MonopolyGame(Vector(player9,player1), board, player9, sound = false)
      val controllerTest2 = new Controller(initialGame2, dice)
      val state = MovingState(() => (1, 1)).handle(OpEnum.enter, controllerTest2)
      controllerTest2.currentPlayer.balance shouldBe 1480
      //controllerTest2.switchToNextPlayer()
      //controllerTest2.currentPlayer.balance shouldBe 1520
      state shouldBe a[AdditionalActionsState]
    }

    "end the game if the player has not enough money for paying rent" in {
      val player9 = player1.copy(position = 20, balance = 10)
      val initialGame2 = MonopolyGame(Vector(player9), board, player9, sound = false)
      val controllerTest2 = new Controller(initialGame2, dice)
      val state = MovingState(() => (1,1)).handle(OpEnum.enter, controllerTest2)
      controllerTest2.isGameOver shouldBe true
    }

    "Update Players balance und reset Freeparking value " in {
      val player9 = player1.copy(position = 15)
      val initialGame2 = MonopolyGame(Vector(player9), board, player9, sound = false)
      val controllerTest2 = new Controller(initialGame2, dice)
      val state = MovingState(() => (3, 3)).handle(OpEnum.enter, controllerTest2)
      controllerTest2.currentPlayer.balance shouldBe 1600
      controllerTest2.board.fields(20).asInstanceOf[FreeParkingField].amount shouldBe 0
    }

    "update players balance and FreeParking amount if player lands on Taxfield" in {
      val player9 = player1.copy(position = 37)
      val initialGame2 = MonopolyGame(Vector(player9), board, player9, sound = false)
      val controllerTest2 = new Controller(initialGame2, dice)
      val state = MovingState(() => (1, 1)).handle(OpEnum.enter, controllerTest2)
      controllerTest2.currentPlayer.balance shouldBe 1300
      controllerTest2.board.fields(20).asInstanceOf[FreeParkingField].amount shouldBe 300
    }

    "end the game if the player has not enough money" in {
      val player9 = player1.copy(position = 37, balance = 100)
      val initialGame2 = MonopolyGame(Vector(player9), board, player9, sound = false)
      val controllerTest2 = new Controller(initialGame2, dice)
      val state = MovingState(() => (1,1)).handle(OpEnum.enter, controllerTest2)
      controllerTest2.isGameOver shouldBe true
    }
  }

  "BuyHouseState" should {

    "buy a house on a property field and return ConfirmState" in {
      val fieldIndexToBuyHouseOn = 22 // Index in 'fields' is 21 for "Red1"
      val state = BuyHouseState().handle(OpEnum.fieldSelected(fieldIndexToBuyHouseOn), controller)
      state shouldBe a[ConfirmBuyHouseState]

      val updatedField = controller.board.fields(fieldIndexToBuyHouseOn - 1).asInstanceOf[PropertyField]
      updatedField.house.amount should be > 0
    }

    "return EndTurnState if field is not a property field" in {
      val jailFieldIndex = 11 // JailField at index 10
      val state = BuyHouseState(isDouble = false).handle(OpEnum.fieldSelected(jailFieldIndex), controller)
      state shouldBe a[EndTurnState]
    }
    "return RollingState if field is not a property field and player rolls doubles" in {
      val state = BuyHouseState(isDouble = true).handle(OpEnum.fieldSelected(5), controller)
      state shouldBe a[RollingState]
    }
    "return EndState if field is not a property field and player does not roll doubles" in {
      val state = BuyHouseState().handle(OpEnum.fieldSelected(5), controller)
      state shouldBe a[EndTurnState]
    }
    "return BuyHouseState if OpEnum is not fieldSelected" in {
      val state = BuyHouseState().handle(OpEnum.roll, controller)
      state shouldBe a[BuyHouseState]
    }
  }

  "AdditionalActionsState" should {

    "return BuyHouseState when input is 'buy'" in {
      val state = AdditionalActionsState().handle(OpEnum.buy, controller)
      state shouldBe a[BuyHouseState]
    }
    "return EndTurnState when input is 'end'" in {
      val state = AdditionalActionsState().handle(OpEnum.end, controller)
      state shouldBe a[EndTurnState]
    }
    "return RollingState when input is 'end' and isDouble is true" in {
      val state = AdditionalActionsState(isDouble = true).handle(OpEnum.end, controller)
      state shouldBe a[RollingState]
    }
  }

  "BuyPropertyState" should {

    "execute BuyPropertyCommand and return AdditionalActionsState for PropertyField" in {
      controller.updatePlayer(player1.copy(position = 2))
      val state = BuyPropertyState().handle(OpEnum.enter, controller)
      state shouldBe a[AdditionalActionsState]
    }

    "execute BuyTrainStationCommand and return AdditionalActionsState for TrainStationField" in {
      controller.updatePlayer(player1.copy(position = 6))
      val state = BuyPropertyState().handle(OpEnum.enter, controller)
      state shouldBe a[AdditionalActionsState]
    }

    "execute BuyUtilityCommand and return AdditionalActionsState for UtilityField" in {
      controller.updatePlayer(player1.copy(position = 13))
      val state = BuyPropertyState().handle(OpEnum.enter, controller)
      state shouldBe a[AdditionalActionsState]
    }

    "return AdditionalActionsState for non-buyable field" in {
      controller.updatePlayer(player1.copy(position = 1))
      val state = BuyPropertyState().handle(OpEnum.enter, controller)
      state shouldBe a[AdditionalActionsState]
    }
  }

  "PropertyDecisionState" should {

    "return BuyPropertyState when input is 'y'" in {
      val stateY = PropertyDecisionState().handle(OpEnum.y, controller)
      stateY shouldBe a[BuyPropertyState]
    }
    "return AdditionalActionsState when input is 'n'" in {
      val stateN = PropertyDecisionState().handle(OpEnum.n, controller)
      stateN shouldBe a[AdditionalActionsState]
    }
    "return AdditionalActionsState when input is not 'y' or 'n'" in {
      val state = PropertyDecisionState().handle(OpEnum.roll, controller)
      state shouldBe a[AdditionalActionsState]
    }
  }

  "JailState" should {

    "return RollingState if player pays to leave jail and has enough money" in {
      val richPlayer = player1.copy(balance = 100, isInJail = true)
      controller.updatePlayer(richPlayer)
      val state = JailState().handle(OpEnum.pay, controller)
      state shouldBe a[RollingState]
    }

    "stay in JailState if player tries to pay with insufficient balance" in {
      val poorPlayer = player1.copy(balance = 10, isInJail = true)
      controller.updatePlayer(poorPlayer)
      val state = JailState().handle(OpEnum.pay, controller)
      state shouldBe a[JailState]
    }

    "return MovingState if player rolls doubles" in {
      val doubler = () => (3, 3)
      controller.updatePlayer(player1.copy(isInJail = true))
      val strategy = JailTurnStrategy()
      val updated = strategy.executeTurn(controller.currentPlayer, doubler)
      controller.updatePlayer(updated)

      val state = JailState().handle(OpEnum.roll, controller)
      if (!controller.currentPlayer.isInJail)
        state shouldBe a[MovingState]
      else
        fail("Expected player to leave jail after rolling doubles")
    }

    "stay in JailState if player rolls and does not roll doubles" in {
      val nonDoubler = () => (2, 3)
      controller.updatePlayer(player1.copy(isInJail = true))

      val state = JailState().handle(OpEnum.roll, controller)
      if (controller.currentPlayer.isInJail)
        state shouldBe a[JailState]
      else
        fail("Expected player to stay in jail after not rolling doubles")
    }

    "handle if controller is not right initialized" in {
      val fakeDice = new Dice() {
        override def rollDice(withSound: Boolean): (Int, Int) = (6, 6)
      }

      val controller = new Controller(initialGame, fakeDice)
      controller.updatePlayer(player1.copy(isInJail = true))

      val state = JailState().handle(OpEnum.roll, controller)
      state shouldBe a[MovingState]
    }
  }

  "ConfirmBuyHouseState" should {

    "undo the command and return AdditionalActionsState when input is 'y'" in {
      val field = controller.board.fields(21).asInstanceOf[PropertyField] // Red1
      val command = BuyHouseCommand(controller, field, controller.currentPlayer)

      // Erst ausführen, um einen Zustand zu haben
      command.execute()

      val state = ConfirmBuyHouseState(isDouble = false, command)
      val nextState = state.handle(OpEnum.y, controller)

      nextState shouldBe a[AdditionalActionsState]
    }

    "return RollingState if isDouble is true and input is not 'y'" in {
      val field = controller.board.fields(21).asInstanceOf[PropertyField] // Red1
      val command = BuyHouseCommand(controller, field, controller.currentPlayer)
      command.execute()

      val state = ConfirmBuyHouseState(isDouble = true, command)
      val nextState = state.handle(OpEnum.n, controller)

      nextState shouldBe a[RollingState]
    }

    "return EndTurnState if isDouble is false and input is not 'y'" in {
      val field = controller.board.fields(21).asInstanceOf[PropertyField] // Red1
      val command = BuyHouseCommand(controller, field, controller.currentPlayer)
      command.execute()

      val state = ConfirmBuyHouseState(isDouble = false, command)
      val nextState = state.handle(OpEnum.n, controller)

      nextState shouldBe a[EndTurnState]
    }
  }
}