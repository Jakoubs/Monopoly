package de.htwg.model

import de.htwg.controller.Controller
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.model.*
import de.htwg.model.PropertyField.Color.*
import de.htwg.{Board, MonopolyGame}
import de.htwg.util.util.Observable
import org.scalatest.matchers.should.Matchers

import scala.collection.immutable.Vector

class ControllerSpec extends AnyWordSpec with Matchers {

  "A Controller" should {
    val dice = new Dice()
    val player1 = Player("Player 1", 1500, 0, isInJail = false, 0)
    val player2 = Player("Player 2", 1500, 0, isInJail = false, 0)
    val fields = Vector(
      GoField,
      PropertyField("brown1", 2, 100, 10, None, color = Brown, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      CommunityChestField(3),
      PropertyField("brown2", 4, 100, 10, None, color = Brown, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      TaxField(100, 5),
      TrainStationField("Marklylebone Station", 6, None),
      PropertyField("lightBlue1", 7, 100, 10, None, color = LightBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      ChanceField(8),
      PropertyField("lightBlue2", 9, 100, 10, None, color = LightBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      PropertyField("lightBlue3", 10, 100, 10, None, color = LightBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      JailField,
      PropertyField("Pink1", 12, 100, 10, None, color = Pink, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      UtilityField("Electric Company", 13, None),
      PropertyField("Pink2", 14, 100, 10, None, color = Pink, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      PropertyField("Pink3", 15, 100, 10, None, color = Pink, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      TrainStationField("Fenchurch ST Station", 16, None),
      PropertyField("Orange1", 17, 100, 10, None, color = Orange, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      CommunityChestField(18),
      PropertyField("Orange2", 19, 100, 10, None, color = Orange, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      PropertyField("Orange3", 20, 100, 10, None, color = Orange, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      FreeParkingField(0),
      PropertyField("Red1", 22, 100, 10, None, color = Red, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      ChanceField(23),
      PropertyField("Red2", 24, 100, 10, None, color = Red, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      PropertyField("Red3", 25, 100, 10, None, color = Red, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      TrainStationField("King's Cross Station", 26, None),
      PropertyField("Yellow1", 27, 100, 10, None, color = Yellow, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      UtilityField("Water Works", 28, None),
      PropertyField("Yellow2", 29, 100, 10, None, color = Yellow, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      PropertyField("Yellow3", 30, 100, 10, None, color = Yellow, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      GoToJailField(),
      PropertyField("Green1", 32, 100, 10, None, color = Green, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      PropertyField("Green2", 33, 100, 10, None, color = Green, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      CommunityChestField(34),
      PropertyField("Green3", 35, 100, 10, None, color = Green, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      TrainStationField("Liverpool ST Station", 36, None),
      ChanceField(37),
      PropertyField("DarkBlue1", 38, 100, 10, None, color = DarkBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      TaxField(200, 39),
      PropertyField("DarkBlue2", 40, 100, 10, None, color = DarkBlue, PropertyField.Mortgage(10, false), PropertyField.House(0))
    )

    val board = Board(fields)
    val initialGame = MonopolyGame(Vector(player1, player2), board, player1, sound = false)
    val controller = new Controller(initialGame, dice)
/*
    "have a current player" in {
      controller.currentPlayer should be(player1)
    }

    "have a board" in {
      controller.board should be(board)
    }

    "have a list of players" in {
      controller.players should be(Vector(player1, player2))
    }

    "have a sound setting" in {
      controller.sound should be(false)
    }
 */

    "handle a regular turn" in {
      val initialPosition = controller.currentPlayer.position
      // Mock the dice roll to get predictable movement
      val mockDice = new Dice {
        override def rollDice(sound: Boolean = false): (Int, Int) = (3, 4)
      }
      val testController = new Controller(controller.game, mockDice)
      testController.handleRegularTurn()
      testController.currentPlayer.position should be((initialPosition + 7) % fields.size)
    }

    "switch to next Player if Player not in Jail in handlePlayerTurn" in {
      val currentPlayer = controller.currentPlayer
      controller.handlePlayerTurn()
      controller.currentPlayer should not be (currentPlayer)
      controller.currentPlayer should be(player2)
    }

    "switch to next Player if Player is in Jail in handlePlayerTurn" in {
      val gameGame = initialGame.copy(players = Vector(player1.copy(balance = 500,isInJail = true), player2.copy(balance = 500)))
      val gameGameController = new Controller(gameGame, dice)
      val currentPlayer = gameGameController.currentPlayer
      controller.handlePlayerTurn()
      gameGameController.currentPlayer should not be currentPlayer
      gameGameController.currentPlayer should be(player2)
    }

    "detect game over when only one player has balance > 0" in {
      val gameOverGame = initialGame.copy(players = Vector(player1.copy(balance = 100), player2.copy(balance = 0)))
      val gameOverController = new Controller(gameOverGame, dice)
      gameOverController.isGameOver should be(true)

      val notGameOverGame = initialGame.copy(players = Vector(player1.copy(balance = 100), player2.copy(balance = 50)))
      val notGameOverController = new Controller(notGameOverGame, dice)
      notGameOverController.isGameOver should be(false)
    }

    "handle landing on a Go To Jail field" in {
      val goToJailIndex = board.fields.indexWhere(_.isInstanceOf[GoToJailField])
      val playerAtGoToJail = player1.copy(position = goToJailIndex)
      val gameAtGoToJail = initialGame.copy(players = Vector(playerAtGoToJail, player2), currentPlayer = playerAtGoToJail)
      val controllerAtGoToJail = new Controller(gameAtGoToJail, dice)
      controllerAtGoToJail.handleGoToJailField()
      controllerAtGoToJail.game.currentPlayer.isInJail should be(true)
      controllerAtGoToJail.game.currentPlayer.position should be(10) // Jail position
    }

    "allow buying a property if it's not owned" in {
      val unownedPropertyIndex = 1
      val unownedProperty = board.fields(unownedPropertyIndex).asInstanceOf[PropertyField]
      val affordablePlayer = player1.copy(balance = unownedProperty.price + 100)
      val gameWithAffordablePlayer = initialGame.copy(players = Vector(affordablePlayer, player2), currentPlayer = affordablePlayer)
      val testController = new Controller(gameWithAffordablePlayer, dice)

      testController.buyProperty(unownedPropertyIndex)
      testController.game.board.fields(unownedPropertyIndex).asInstanceOf[PropertyField].owner should be(Some(affordablePlayer))
      testController.game.players.find(_.name == affordablePlayer.name).get.balance should be(affordablePlayer.balance - unownedProperty.price)
    }

    "not allow buying a property if the player doesn't have enough money" in {
      val unownedPropertyIndex = 1
      val unownedProperty = board.fields(unownedPropertyIndex).asInstanceOf[PropertyField]
      val poorPlayer = player1.copy(balance = unownedProperty.price - 10)
      val gameWithPoorPlayer = initialGame.copy(players = Vector(poorPlayer, player2), currentPlayer = poorPlayer)
      val testController = new Controller(gameWithPoorPlayer, dice)

      testController.buyProperty(unownedPropertyIndex)
      testController.game.board.fields(unownedPropertyIndex).asInstanceOf[PropertyField].owner should be(None)
      testController.game.players.find(_.name == poorPlayer.name).get.balance should be(poorPlayer.balance)
    }

    "not allow buying a property if it's already owned" in {
      val propertyIndex = 1
      val property = board.fields(propertyIndex).asInstanceOf[PropertyField].copy(owner = Some(player2))
      val updatedFields = board.fields.updated(propertyIndex, property)
      val ownedBoard = board.copy(fields = updatedFields)
      val gameWithOwnedProperty = initialGame.copy(board = ownedBoard)
      val testController = new Controller(gameWithOwnedProperty, dice)

      testController.buyProperty(propertyIndex)
      testController.game.board.fields(propertyIndex).asInstanceOf[PropertyField].owner should be(Some(player2))
      testController.game.players.find(_.name == player1.name).get.balance should be(player1.balance)
    }

    "handle landing on an unowned property field" in {
      val unownedPropertyIndex = 1
      val unownedProperty = board.fields(unownedPropertyIndex).asInstanceOf[PropertyField]
      val affordablePlayer = player1.copy(balance = unownedProperty.price + 100, position = unownedPropertyIndex)
      val gameWithPlayerOnProperty = initialGame.copy(players = Vector(affordablePlayer, player2), currentPlayer = affordablePlayer)
      val testController = new Controller(gameWithPlayerOnProperty, dice)

      // Simulate the scenario where the player chooses to buy (by default, handlePropertyField doesn't take input)
      // To properly test this, you'd likely need to mock input or test the internal logic of handlePropertyField
      // Here we'll just call buyProperty directly to check the outcome if bought.
      testController.buyProperty(unownedPropertyIndex)
      testController.game.board.fields(unownedPropertyIndex).asInstanceOf[PropertyField].owner should be(Some(affordablePlayer))
      testController.game.players.find(_.name == affordablePlayer.name).get.balance should be(affordablePlayer.balance - unownedProperty.price)

      val poorPlayerOnProperty = player1.copy(balance = unownedProperty.price - 10, position = unownedPropertyIndex)
      val gameWithPoorPlayerOnProperty = initialGame.copy(players = Vector(poorPlayerOnProperty, player2), currentPlayer = poorPlayerOnProperty)
      val testControllerPoor = new Controller(gameWithPoorPlayerOnProperty, dice)
      // Again, simulating no buy due to insufficient funds
      testControllerPoor.buyProperty(unownedPropertyIndex)
      testControllerPoor.game.board.fields(unownedPropertyIndex).asInstanceOf[PropertyField].owner should be(None)
      testControllerPoor.game.players.find(_.name == poorPlayerOnProperty.name).get.balance should be(poorPlayerOnProperty.balance)
    }

    "handle landing on an owned property field by another player" in {
      val propertyIndex = 1
      val property = board.fields(propertyIndex).asInstanceOf[PropertyField].copy(owner = Some(player2))
      val updatedFields = board.fields.updated(propertyIndex, property)
      val ownedBoard = board.copy(fields = updatedFields)
      val playerOnOwnedProperty = player1.copy(position = propertyIndex)
      val gameWithOwnedPropertyAndPlayer = initialGame.copy(board = ownedBoard, players = Vector(playerOnOwnedProperty, player2), currentPlayer = playerOnOwnedProperty)
      val testController = new Controller(gameWithOwnedPropertyAndPlayer, dice)

      val initialBalancePlayer1 = playerOnOwnedProperty.balance
      val initialBalancePlayer2 = player2.balance

      testController.handleFieldAction() // This should trigger rent payment

      val updatedPlayer1 = testController.game.players.find(_.name == playerOnOwnedProperty.name).get
      val updatedPlayer2 = testController.game.players.find(_.name == player2.name).get
      val rent = PropertyField.calculateRent(property)

      updatedPlayer1.balance should be(initialBalancePlayer1 - rent)
      updatedPlayer2.balance should be(initialBalancePlayer2 + rent)
    }

    "handle landing on their own owned property field" in {
      val propertyIndex = 1
      val property = board.fields(propertyIndex).asInstanceOf[PropertyField].copy(owner = Some(player1))
      val updatedFields = board.fields.updated(propertyIndex, property)
      val ownedBoard = board.copy(fields = updatedFields)
      val playerOnOwnProperty = player1.copy(position = propertyIndex)
      val gameWithOwnPropertyAndPlayer = initialGame.copy(board = ownedBoard, players = Vector(playerOnOwnProperty, player2), currentPlayer = playerOnOwnProperty)
      val testController = new Controller(gameWithOwnPropertyAndPlayer, dice)

      val initialBalance = playerOnOwnProperty.balance
      testController.handleFieldAction() // Should do nothing

      testController.game.players.find(_.name == playerOnOwnProperty.name).get.balance should be(initialBalance)
    }

    "handle a jail turn where player rolls doubles" in {
      val jailedPlayer = player1.copy(isInJail = true, jailTurns = 0, position = 10)
      val gameInJail = initialGame.copy(players = Vector(jailedPlayer, player2), currentPlayer = jailedPlayer)
      val mockDiceDoubles = new Dice {
        override def rollDice(sound: Boolean = false): (Int, Int) = (3, 3)
      }
      val testController = new Controller(gameInJail, mockDiceDoubles)
      val initialPosition = jailedPlayer.position

      testController.handleJailTurn()

      testController.game.currentPlayer.isInJail should be(false)
      testController.game.currentPlayer.jailTurns should be(0)
      testController.game.currentPlayer.position should not be (initialPosition)
    }

    "handle a jail turn where player doesn't roll doubles and has less than 3 turns" in {
      val jailedPlayer = player1.copy(isInJail = true, jailTurns = 1, position = 10)
      val gameInJail = initialGame.copy(players = Vector(jailedPlayer, player2), currentPlayer = jailedPlayer)
      val mockDiceNoDoubles = new Dice {
        override def rollDice(sound: Boolean = false): (Int, Int) = (3, 4)
      }
      val testController = new Controller(gameInJail, mockDiceNoDoubles)
      val initialJailTurns = jailedPlayer.jailTurns

      testController.handleJailTurn()

      testController.game.currentPlayer.isInJail should be(true)
      testController.game.currentPlayer.jailTurns should be(initialJailTurns + 1)
    }

    "handle a jail turn where player doesn't roll doubles and it's their third turn (must pay)" in {
      val jailedPlayer = player1.copy(isInJail = true, jailTurns = 2, balance = 100, position = 10)
      val gameInJail = initialGame.copy(players = Vector(jailedPlayer, player2), currentPlayer = jailedPlayer)
      val mockDiceNoDoubles = new Dice {
        override def rollDice(sound: Boolean = false): (Int, Int) = (3, 4)
      }
      val testController = new Controller(gameInJail, mockDiceNoDoubles)
      val initialBalance = jailedPlayer.balance
      val initialPosition = jailedPlayer.position

      testController.handleJailTurn()

      testController.game.currentPlayer.isInJail should be(false)
      testController.game.currentPlayer.jailTurns should be(0)
      testController.game
    }
  }
}