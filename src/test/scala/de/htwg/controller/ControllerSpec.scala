package de.htwg.controller

import de.htwg.controller.OpEnum.end
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.model.*
import de.htwg.model.PropertyField.Color.*
import de.htwg.{Board, MonopolyGame}

class ControllerSpec extends AnyWordSpec with Matchers {

  val player1 = Player("Player 1", 1500, 1, isInJail = false, 0)
  val player2 = Player("Player 2", 1500, 1, isInJail = false, 0)

  val fields = Vector(
    GoField,
    PropertyField("brown1", 2, 100, 10, None, color = Brown, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    PropertyField("Brown2",3,200,20,Some(player1),Brown,PropertyField.Mortgage(10,false),PropertyField.House(0)),
    JailField
  )

  val board = Board(fields)
  val dice = new Dice {
    override def rollDice(withSound: Boolean): (Int, Int) = (3, 4)
  }

  val initialGame = MonopolyGame(Vector(player1, player2), board, player1, sound = false)
  val controller = new Controller(initialGame, dice)

  "A Controller" should {

    "update the player correctly" in {
      val updatedPlayer = player1.copy(balance = 1200)
      controller.updatePlayer(updatedPlayer)
      controller.currentPlayer.balance shouldBe 1200
    }

    "return the list of players" in {
      controller.players shouldBe controller.game.players
    }


    "return the current TurnInfo via getTurnInfo" in {
      val initialInfo = controller.getTurnInfo
      initialInfo shouldBe TurnInfo()
    }

    "update state when handleInput is called" in {
      val initialState = controller.state
      controller.handleInput(OpEnum.end)
      controller.state should not be initialState
    }

    "notify observers when handleInput is called" in {
      var notified = false
      controller.add(() => notified = true)
      controller.handleInput(end)
      notified shouldBe true
    }
    "getInventory should return the correct inventory string" in {
      val expectedInventoryString = s"INVENTORY Player: ${player1.name}| idx:3[0]"

      val actualInventoryString = controller.getInventory

      actualInventoryString should be(expectedInventoryString)
    }
    "update board and player correctly" in {
      val updatedField = PropertyField("brown1", 2, 100, 10, Some(player1), color = Brown, PropertyField.Mortgage(10, false), PropertyField.House(0))
      val updatedPlayer = player1.copy(balance = 1300)
      controller.updateBoardAndPlayer(updatedField, updatedPlayer)

      controller.board.fields(1) match {
        case pf: PropertyField => pf.owner shouldBe Some(player1)
        case _ => fail("Field should be a PropertyField")
      }

      controller.currentPlayer.balance shouldBe 1300
    }

    "switch to next player" in {
      controller.switchToNextPlayer()
      controller.currentPlayer.name shouldBe "Player 2"
    }

    "detect game over if only one player has money" in {
      val brokePlayer = player1.copy(balance = 0)
      val richPlayer = player2.copy(balance = 500)
      val gameOverController = new Controller(MonopolyGame(Vector(brokePlayer, richPlayer), board, richPlayer, sound = false), dice)
      gameOverController.isGameOver shouldBe true
    }

    "return correct player status string" in {
      val player = player1.copy(isInJail = false)
      val game = initialGame.copy(players = Vector(player), currentPlayer = player)
      val testController = new Controller(game, dice)
      val status = testController.getCurrentPlayerStatus
      status should include("Player 1")
      status should include("Balance")
      status should include("Position")
      status should include("Jail")
      status should include("In Jail")
    }

    "return correct player status string when in Jail" in {
      val player = player1.copy(isInJail = true)
      val game = initialGame.copy(players = Vector(player), currentPlayer = player)
      val testController = new Controller(game, dice)
      val status = testController.getCurrentPlayerStatus
      status should include("Player 1")
      status should include("Balance")
      status should include("Position")
      status should include("Jail")
      status should include("In Jail")
    }

    "return non-empty string from getBoardString" in {
      val boardString = controller.getBoardString
      boardString shouldBe a[String]
      boardString should not be empty
    }
    "correctly return owned properties grouped by player" in {
      val prop1 = PropertyField("brown1", 2, 100, 10, Some(player1), color = Brown, PropertyField.Mortgage(10, false), PropertyField.House(0))
      val prop2 = PropertyField("brown2", 4, 100, 10, Some(player2), color = Brown, PropertyField.Mortgage(10, false), PropertyField.House(0))
      val prop3 = PropertyField("brown3", 5, 100, 10, Some(player1), color = Brown, PropertyField.Mortgage(10, false), PropertyField.House(0))

      val testFields = Vector(GoField, prop1, prop2, prop3, JailField)
      val testBoard = Board(testFields)
      val testGame = MonopolyGame(Vector(player1, player2), testBoard, player1, sound = false)
      val testController = new Controller(testGame, dice)

      val ownedProps = testController.getOwnedProperties()
      ownedProps(player1) should contain allOf(prop1, prop3)
      ownedProps(player2) should contain(prop2)
    }

    "correctly count owned train stations per player" in {
      val ts1 = TrainStationField("Station 1", 6, 200, Some(player1))
      val ts2 = TrainStationField("Station 2", 7, 200, Some(player1))
      val ts3 = TrainStationField("Station 3", 8, 200, Some(player2))

      val testFields = Vector(GoField, ts1, ts2, ts3, JailField)
      val testBoard = Board(testFields)
      val testGame = MonopolyGame(Vector(player1, player2), testBoard, player1, sound = false)
      val testController = new Controller(testGame, dice)

      val ownedStations = testController.getOwnedTrainStations()
      ownedStations(player1) shouldBe 2
      ownedStations(player2) shouldBe 1
    }

    "correctly count owned utilities per player" in {
      val util1 = UtilityField("Utility 1", 10, 150, UtilityField.UtilityCheck.utility, Some(player1))
      val util2 = UtilityField("Utility 2", 11, 150, UtilityField.UtilityCheck.utility, Some(player2))

      val testFields = Vector(GoField, util1, util2, JailField)
      val testBoard = Board(testFields)
      val testGame = MonopolyGame(Vector(player1, player2), testBoard, player1, sound = false)
      val testController = new Controller(testGame, dice)

      val ownedUtils = testController.getOwnedUtilities()
      ownedUtils(player1) shouldBe 1
      ownedUtils(player2) shouldBe 1
    }
  }
}
