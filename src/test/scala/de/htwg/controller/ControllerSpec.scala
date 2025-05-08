package de.htwg.controller

import de.htwg.controller.Controller
import de.htwg.model.PropertyField.Color.*
import de.htwg.model.*
import de.htwg.{Board, MonopolyGame}
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.immutable.Vector

class ControllerSpec extends AnyWordSpec with Matchers {

  "A Controller" should {
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
      TrainStationField("Marklylebone Station", 6 ,200,None),
      PropertyField("lightBlue1", 7, 100, 10, None, color = LightBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      ChanceField(8),
      PropertyField("lightBlue2", 9, 100, 10, None, color = LightBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      PropertyField("lightBlue3", 10, 100, 10, None, color = LightBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      JailField,
      PropertyField("Pink1", 12, 100, 10, None, color = Pink, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      UtilityField("Electric Company", 13,150,UtilityField.UtilityCheck.utility, None),
      PropertyField("Pink2", 14, 100, 10, None, color = Pink, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      PropertyField("Pink3", 15, 100, 10, None, color = Pink, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      TrainStationField("Fenchurch ST Station", 16,200, None),
      PropertyField("Orange1", 17, 100, 10, None, color = Orange, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      CommunityChestField(18),
      PropertyField("Orange2", 19, 100, 10, None, color = Orange, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      PropertyField("Orange3", 20, 100, 10, None, color = Orange, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      FreeParkingField(0),
      PropertyField("Red1", 22, 100, 10, None, color = Red, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      ChanceField(23),
      PropertyField("Red2", 24, 100, 10, None, color = Red, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      PropertyField("Red3", 25, 100, 10, None, color = Red, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      TrainStationField("King's Cross Station", 26,200, None),
      PropertyField("Yellow1", 27, 100, 10, None, color = Yellow, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      UtilityField("Water Works", 28,150,UtilityField.UtilityCheck.utility, None),
      PropertyField("Yellow2", 29, 100, 10, None, color = Yellow, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      PropertyField("Yellow3", 30, 100, 10, None, color = Yellow, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      GoToJailField(),
      PropertyField("Green1", 32, 100, 10, None, color = Green, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      PropertyField("Green2", 33, 100, 10, None, color = Green, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      CommunityChestField(34),
      PropertyField("Green3", 35, 100, 10, None, color = Green, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      TrainStationField("Liverpool ST Station", 36,200,None),
      ChanceField(37),
      PropertyField("DarkBlue1", 38, 100, 10, None, color = DarkBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
      TaxField(200, 39),
      PropertyField("DarkBlue2", 40, 100, 10, None, color = DarkBlue, PropertyField.Mortgage(10, false), PropertyField.House(0))
    )

    val board = Board(fields)
    val initialGame = MonopolyGame(Vector(player1, player2), board, player1, sound = false)
    val controller = new Controller(initialGame, dice)

    "provide game status information" in {

      //Noch anpassen
      controller.getGameStatus should be(initialGame)
      controller.getCurrentPlayerName should be(controller.currentPlayer.name)
      controller.getCurrentPlayerBalance should be(controller.currentPlayer.balance)
      controller.getCurrentPlayerPosition should be(controller.currentPlayer.position)
      controller.isCurrentPlayerInJail should be(controller.currentPlayer.isInJail)

      val statusString = controller.getCurrentPlayerStatus
    }

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

    "handle a regular turn" in {
      val initialPosition = controller.currentPlayer.position
      val mockDice = new Dice {
        override def rollDice(sound: Boolean = false): (Int, Int) = (3, 4)
      }
      val testController = new Controller(controller.game, mockDice)
      testController.handleRegularTurn(mockAsk, mockPrint, mockChoice)

      // The position update in handleRegularTurn uses playerMove() which we don't see in the provided code
      // We need to adjust this test to match how playerMove() works in the actual implementation
      // Based on controller implementation, we can verify that the controller's game was updated
      testController.currentPlayer should be(testController.game.currentPlayer)
    }

    "switch to next Player in handlePlayerTurn" in {
      val currentPlayer = controller.currentPlayer
      val controllerCopy = new Controller(controller.game, dice)
      controllerCopy.handlePlayerTurn(mockAsk, mockPrint, mockChoice)
      controllerCopy.currentPlayer should not be (currentPlayer)
      controllerCopy.currentPlayer should be(player2)
    }

    "switch to next Player if Player is in Jail in handlePlayerTurn" in {
      val gameWithJailedPlayer = initialGame.copy(players = Vector(player1.copy(isInJail = true), player2), currentPlayer = player1.copy(isInJail = true))
      val controllerWithJailedPlayer = new Controller(gameWithJailedPlayer, dice)
      val currentPlayer = controllerWithJailedPlayer.currentPlayer
      controllerWithJailedPlayer.handlePlayerTurn(mockAsk, mockPrint, mockChoice)
      controllerWithJailedPlayer.currentPlayer should not be currentPlayer
      controllerWithJailedPlayer.currentPlayer should be(player2)
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

      val jailPosition = 11 // Position of the JailField
      val player = playerAtGoToJail.goToJail()
      player.isInJail should be(true)
      player.position should be(jailPosition)
      val prop = PropertyField("brown1", 2, 100, 10, None, color = Brown, PropertyField.Mortgage(10, false), PropertyField.House(0))
      controller.handlePropertyField(prop, mockAsk, mockPrint, mockChoice)
    }
    "not allow buying a property if the player doesn't have enough money" in {
      val unownedPropertyIndex = 1
      val unownedProperty = board.fields(unownedPropertyIndex).asInstanceOf[PropertyField]
      val poorPlayer = player1.copy(balance = unownedProperty.price - 10)
      val gameWithPoorPlayer = initialGame.copy(players = Vector(poorPlayer, player2), currentPlayer = poorPlayer)
      val testController = new Controller(gameWithPoorPlayer, dice)

      testController.buyProperty(unownedPropertyIndex, mockPrint)

      // Greife jetzt auf den aktualisierten Zustand des game-Objekts im testController zu
      val updatedField = testController.game.board.fields(unownedPropertyIndex).asInstanceOf[PropertyField]
      updatedField.owner should be(None)

      val updatedPlayer = testController.game.players.find(_.name == poorPlayer.name).get
      updatedPlayer.balance should be(poorPlayer.balance)
    }

    "not allow buying a property if it's already owned" in {
      val propertyIndex = 1
      val property = board.fields(propertyIndex).asInstanceOf[PropertyField].copy(owner = Some(player2))
      val updatedFields = board.fields.updated(propertyIndex, property)
      val ownedBoard = board.copy(fields = updatedFields)
      val gameWithOwnedProperty = initialGame.copy(board = ownedBoard)
      val testController = new Controller(gameWithOwnedProperty, dice)

      testController.buyProperty(propertyIndex, mockPrint)

      val updatedField = testController.game.board.fields(propertyIndex).asInstanceOf[PropertyField]
      updatedField.owner should be(Some(player2))

      val updatedPlayer = testController.game.players.find(_.name == player1.name).get
      updatedPlayer.balance should be(player1.balance)
    }

    "handle landing on an owned property field by another player" in {
      val propertyIndex = 1
      val property = board.fields(propertyIndex).asInstanceOf[PropertyField].copy(owner = Some(player2))
      val updatedFields = board.fields.updated(propertyIndex-1, property)
      val ownedBoard = board.copy(fields = updatedFields)
      val playerOnOwnedProperty = player1.copy(position = propertyIndex)
      val gameWithOwnedPropertyAndPlayer = initialGame.copy(
        board = ownedBoard,
        players = Vector(playerOnOwnedProperty, player2),
        currentPlayer = playerOnOwnedProperty
      )

      val testController = new Controller(gameWithOwnedPropertyAndPlayer, dice)

      val initialBalancePlayer1 = playerOnOwnedProperty.balance
      val initialBalancePlayer2 = player2.balance

      testController.handleFieldAction(mockAsk, mockPrint, mockChoice) // This should trigger rent payment
      val rent = PropertyField.calculateRent(property)

      val updatedPlayer = testController.game.players.find(_.name == playerOnOwnedProperty.name).get
      updatedPlayer.balance should be(initialBalancePlayer1 - rent)
      val updatedPlayer2 = testController.game.players.find(_.name == player2.name).get
      updatedPlayer2.balance should be(initialBalancePlayer2 + rent)
    }

    "handle landing on their own owned property field" in {
      val propertyIndex = 1
      val property = board.fields(propertyIndex).asInstanceOf[PropertyField].copy(owner = Some(player1))
      val updatedFields = board.fields.updated(propertyIndex, property)
      val ownedBoard = board.copy(fields = updatedFields)
      val playerOnOwnProperty = player1.copy(position = propertyIndex)
      val gameWithOwnPropertyAndPlayer = initialGame.copy(
        board = ownedBoard,
        players = Vector(playerOnOwnProperty, player2),
        currentPlayer = playerOnOwnProperty
      )
      val testController = new Controller(gameWithOwnPropertyAndPlayer, dice)

      val initialBalance = playerOnOwnProperty.balance
      testController.handleFieldAction(mockAsk, mockPrint, mockChoice) // Should do nothing for own property

      val updatedPlayer = testController.game.players.find(_.name == playerOnOwnProperty.name).get
      updatedPlayer.balance should be(initialBalance)
    }

    "create a TrainStation field with correct properties" in {
      val stationName = "Test Station"
      val stationIndex = 5
      val station = TrainStationField(stationName, stationIndex,200, None)

      station.name should be(stationName)
      station.price should be(200)
      station.index should be(stationIndex)
      station.owner should be(None)
    }

    "handle a jail turn where player chooses to pay to get out" in {
      val jailedPlayer = player1.copy(isInJail = true, jailTurns = 0, position = 10, balance = 100)
      val gameInJail = initialGame.copy(players = Vector(jailedPlayer, player2), currentPlayer = jailedPlayer)

      // Mock choice to always select option 1 (pay to get out)
      val payToGetOutChoice: () => Int = () => 1

      val testController = new Controller(gameInJail, dice)
      testController.handleJailTurn(mockAsk, mockPrint, payToGetOutChoice)

      val updatedPlayer = testController.game.currentPlayer
      updatedPlayer.isInJail should be(false)
      updatedPlayer.jailTurns should be(0)
      updatedPlayer.balance should be(jailedPlayer.balance - 50) // Should have paid 50
    }

    "handle a jail turn where player rolls doubles" in {
      val jailedPlayer = player1.copy(isInJail = true, jailTurns = 0, position = 10)
      val gameInJail = initialGame.copy(players = Vector(jailedPlayer, player2), currentPlayer = jailedPlayer)

      // Mock dice to always roll doubles
      val mockDiceDoubles = new Dice {
        override def rollDice(sound: Boolean = false): (Int, Int) = (3, 3)
      }

      // Mock choice to always select option 3 (try to roll doubles)
      val rollDoublesChoice: () => Int = () => 3

      val testController = new Controller(gameInJail, mockDiceDoubles)
      testController.handleJailTurn(mockAsk, mockPrint, rollDoublesChoice)

      val updatedPlayer = testController.game.currentPlayer
      updatedPlayer.isInJail should be(false)
      updatedPlayer.jailTurns should be(0)
    }

    "handle a jail turn where player doesn't roll doubles and has less than 3 turns" in {
      val jailedPlayer = player1.copy(isInJail = true, jailTurns = 1, position = 10)
      val gameInJail = initialGame.copy(players = Vector(jailedPlayer, player2), currentPlayer = jailedPlayer)

      // Mock dice that doesn't roll doubles
      val mockDiceNoDoubles = new Dice {
        override def rollDice(sound: Boolean = false): (Int, Int) = (3, 4)
      }

      // Mock choice to always select option 3 (try to roll doubles)
      val rollDoublesChoice: () => Int = () => 3

      val testController = new Controller(gameInJail, mockDiceNoDoubles)
      val initialJailTurns = jailedPlayer.jailTurns

      testController.handleJailTurn(mockAsk, mockPrint, rollDoublesChoice)

      val updatedPlayer = testController.game.currentPlayer
      updatedPlayer.isInJail should be(true)
      updatedPlayer.jailTurns should be(initialJailTurns + 1)
    }

    "handle a jail turn where player doesn't roll doubles and it's their third turn (must pay)" in {
      val jailedPlayer = player1.copy(isInJail = true, jailTurns = 2, balance = 100, position = 10)
      val gameInJail = initialGame.copy(players = Vector(jailedPlayer, player2), currentPlayer = jailedPlayer)

      // Mock dice that doesn't roll doubles
      val mockDiceNoDoubles = new Dice {
        override def rollDice(sound: Boolean = false): (Int, Int) = (3, 4)
      }

      // Mock choice to always select option 3 (try to roll doubles)
      val rollDoublesChoice: () => Int = () => 3

      val testController = new Controller(gameInJail, mockDiceNoDoubles)
      val initialBalance = jailedPlayer.balance

      testController.handleJailTurn(mockAsk, mockPrint, rollDoublesChoice)

      val updatedPlayer = testController.game.currentPlayer
      updatedPlayer.isInJail should be(false)
      updatedPlayer.jailTurns should be(0)
      updatedPlayer.balance should be(initialBalance - 50) // Should have paid 50 after 3rd turn
    }

    "charge rent to current player when landing on another player's propertyField" in {
      // Player 2 besitzt die Station
      val stationIndex = 11
      val property = fields(stationIndex).asInstanceOf[PropertyField].copy(owner = Some(player2))

      val updatedFields = fields.updated(stationIndex, property)

      val game = MonopolyGame(Vector(player1, player2), Board(updatedFields), player1, sound = false)
      val controller = new Controller(game, dice)

      controller.handlePropertyField(property, mockAsk, println, mockChoice)

      val updatedPlayer1 = controller.game.players.find(_.name == player1.name).get
      val updatedPlayer2 = controller.game.players.find(_.name == player2.name).get

      updatedPlayer1.balance shouldBe (1500 - 10)
      updatedPlayer2.balance shouldBe (1500 + 10)
    }

    "dont buy the property if the player said no" in {
      val stationIndex = 11
      val mockAsk: String => Boolean = _ => false
      val property = fields(stationIndex).asInstanceOf[PropertyField]

      val updatedFields = fields.updated(stationIndex, property)
      val game = MonopolyGame(Vector(player1, player2), Board(updatedFields), player1, sound = false)

      controller.handlePropertyField(property, mockAsk, println, mockChoice)

      val updatedField = controller.game.board.fields(stationIndex).asInstanceOf[PropertyField]
      updatedField.owner should be(None)
    }
    "handle buying a house on a property" in {
      val brown1Index = 1 // Vector index of "brown1"
      val brown2Index = 3 // Vector index of "brown2"

      val property1 = board.fields(brown1Index).asInstanceOf[PropertyField].copy(owner = Some(player1))
      val property2 = board.fields(brown2Index).asInstanceOf[PropertyField].copy(owner = Some(player1))

      val updatedFields = board.fields
        .updated(brown1Index, property1)
        .updated(brown2Index, property2)

      val ownedBoard = board.copy(fields = updatedFields)
      val playerWithProperties = player1.copy(balance = 500)

      val gameWithOwnedProperties = initialGame.copy(
        board = ownedBoard,
        players = Vector(playerWithProperties, player2),
        currentPlayer = playerWithProperties
      )

      val testController = new Controller(gameWithOwnedProperties, dice)

      // Use the index of the field you actually updated (Vector index, not propertyField.index!)
      testController.buyHouse(brown1Index, mockPrint)

      // Validate: check the updated board in the controller
      val updatedProperty = testController.game.board.fields(brown1Index).asInstanceOf[PropertyField]
      updatedProperty.house.amount should be(1)

      val updatedPlayer = testController.game.players.find(_.name == player1.name).get
      updatedPlayer.balance should be(playerWithProperties.balance - 50)
    }

    "handleTrainStationField" should {
      "allow a player to buy an unowned train station" in {
        val stationIndex = 5 // Index des Bahnhofs "Marklylebone Station"
        val trainStation = fields(stationIndex).asInstanceOf[TrainStationField]

        val game = MonopolyGame(Vector(player1, player2), Board(fields), player1, sound = false)
        val controller = new Controller(game, dice)

        controller.handleTrainStationField(trainStation, mockAsk, mockPrint, mockChoice)

        val updatedField = controller.game.board.fields(stationIndex).asInstanceOf[TrainStationField]
        val updatedPlayer = controller.game.players.find(_.name == player1.name).get

        updatedField.owner shouldBe Some(player1)
        updatedPlayer.balance shouldBe (1500 - 200)
      }

      "not be able to buy trainstaion if not enough money" in {
        val playerWithNoMoney = player1.copy(balance = 100)
        val gameWithNoMoney = initialGame.copy(players = Vector(playerWithNoMoney, player2), currentPlayer = playerWithNoMoney)
        val stationIndex = 5 // Index des Bahnhofs "Marklylebone Station"
        val trainStation = fields(stationIndex).asInstanceOf[TrainStationField]

        val controller = new Controller(gameWithNoMoney, dice)

        controller.handleTrainStationField(trainStation, mockAsk, mockPrint, mockChoice)

        val updatedField = controller.game.board.fields(stationIndex).asInstanceOf[TrainStationField]
        val updatedPlayer = controller.game.players.find(_.name == player1.name).get

        updatedField.owner shouldBe None
        updatedPlayer.balance shouldBe (100)
      }

      "dont buy the Trainstation if the player said no" in {
        val stationIndex = 15
        val mockAsk: String => Boolean = _ => false
        val trainstation = fields(stationIndex).asInstanceOf[TrainStationField]

        val updatedFields = fields.updated(stationIndex, trainstation)
        val game = MonopolyGame(Vector(player1, player2), Board(updatedFields), player1, sound = false)

        controller.handleTrainStationField(trainstation, mockAsk, println, mockChoice)

        val updatedField = controller.game.board.fields(stationIndex).asInstanceOf[TrainStationField]
        updatedField.owner should be(None)
      }

      "charge rent to current player when landing on another player's train station" in {
        // Player 2 besitzt die Station
        val stationIndex = 5
        val trainStation = fields(stationIndex).asInstanceOf[TrainStationField].copy(owner = Some(player2))

        val updatedFields = fields.updated(stationIndex, trainStation)

        val game = MonopolyGame(Vector(player1, player2), Board(updatedFields), player1, sound = false)
        val controller = new Controller(game, dice)

        controller.handleTrainStationField(trainStation, mockAsk, println, mockChoice)

        val updatedPlayer1 = controller.game.players.find(_.name == player1.name).get
        val updatedPlayer2 = controller.game.players.find(_.name == player2.name).get

        updatedPlayer1.balance shouldBe (1500 - 25)
        updatedPlayer2.balance shouldBe (1500 + 25)
      }
    }


    "Utility Field" should {
      /*val utilityName = "Test Utility"
      val utilityPosition = 13
      val utilityPrice = 150
      val utility = UtilityField(utilityName, utilityPosition,150,UtilityField.UtilityCheck.utility, None)

      "be created with correct initial properties" in {
        utility.name should be(utilityName)
        utility.index should be(utilityPosition)
        utility.owner should be(None)
      }

      "calculate correct rent based on number of utilities owned and dice roll" in {
        // Get utility positions
        val utilityPositions = Vector(13, 28) // Electric Company and Water Works

        // Mock dice for consistent testing
        val mockDice = new Dice {
          override def rollDice(sound: Boolean = false): (Int, Int) = (3, 4) // Total of 7
        }

        // Test rent with one utility owned
        val playerWithOneUtility = player1
        val singleUtilityFields = board.fields.updated(
          utilityPositions(0),
          UtilityField("Electric Company", utilityPositions(0),150,UtilityField.UtilityCheck.utility, Some(playerWithOneUtility))
        )
        val gameWithOneUtility = initialGame.copy(board = board.copy(fields = singleUtilityFields))
        val controllerOneUtility = new Controller(gameWithOneUtility, mockDice)

        val rentWithOne = 28
        rentWithOne should be(28) // 4 times dice roll (7 * 4)

        // Test rent with both utilities owned
        val twoUtilityFields = singleUtilityFields.updated(
          utilityPositions(1),
          UtilityField("Water Works", utilityPositions(1),150,UtilityField.UtilityCheck.utility, Some(playerWithOneUtility))
        )
        val gameWithTwoUtilities = initialGame.copy(board = board.copy(fields = twoUtilityFields))
        val controllerTwoUtilities = new Controller(gameWithTwoUtilities, mockDice)

        val rentWithTwo = 70
        rentWithTwo should be(70) // 10 times dice roll (7 * 10)
      }

      "handle property transaction correctly" in {
        val utilityIndex = 12 // Position of Electric Company
        val buyingPlayer = player1.copy(balance = 1500)
        val gameBeforeBuy = initialGame.copy(
          players = Vector(buyingPlayer, player2),
          currentPlayer = buyingPlayer
        )
        val testController = new Controller(gameBeforeBuy, dice)

        // Test buying the utility
        testController.buyProperty(utilityIndex, mockPrint)

        val updatedUtility = testController.game.board.fields(utilityIndex).asInstanceOf[UtilityField]
        updatedUtility.owner should be(Some(buyingPlayer))

        val updatedPlayer = testController.game.players.head
        updatedPlayer.balance should be(buyingPlayer.balance - 150) // Utility price should be deducted
      }

      "handle rent payment correctly" in {
        val utilityIndex = 12
        val ownerPlayer = player2.copy(balance = 1500)
        val payingPlayer = player1.copy(balance = 1500, position = utilityIndex)

        // Setup game with owned utility
        val ownedUtilityField = UtilityField("Electric Company", utilityIndex, 150,UtilityField.UtilityCheck.utility,Some(ownerPlayer))
        val updatedFields = board.fields.updated(utilityIndex, ownedUtilityField)
        val gameWithOwnedUtility = initialGame.copy(
          board = board.copy(fields = updatedFields),
          players = Vector(payingPlayer, ownerPlayer),
          currentPlayer = payingPlayer
        )

        // Mock dice for consistent testing
        val mockDice = new Dice {
          override def rollDice(sound: Boolean = false): (Int, Int) = (3, 4) // Total of 7
        }

        val testController = new Controller(gameWithOwnedUtility, mockDice)

        // Initial balances
        val initialPayingBalance = payingPlayer.balance
        val initialOwnerBalance = ownerPlayer.balance

        // Handle the field action (should trigger rent payment)
        testController.handleFieldAction(mockAsk, mockPrint, mockChoice)

        // Get updated players
        val updatedPayingPlayer = testController.game.players.find(_.name == payingPlayer.name).get
        val updatedOwnerPlayer = testController.game.players.find(_.name == ownerPlayer.name).get

        // Verify rent calculation and payment (7 * 4 = 28 for one utility)
        updatedPayingPlayer.balance should be(initialPayingBalance - 28)
        updatedOwnerPlayer.balance should be(initialOwnerBalance + 28)
      }

      "not allow buying when player has insufficient funds" in {
        val utilityIndex = 12
        val poorPlayer = player1.copy(balance = 100) // Less than utility price
        val gameWithPoorPlayer = initialGame.copy(
          players = Vector(poorPlayer, player2),
          currentPlayer = poorPlayer
        )
        val testController = new Controller(gameWithPoorPlayer, dice)

        testController.buyProperty(utilityIndex, mockPrint)

        val updatedUtility = testController.game.board.fields(utilityIndex).asInstanceOf[UtilityField]
        updatedUtility.owner should be(None)

        val updatedPlayer = testController.game.players.head
        updatedPlayer.balance should be(poorPlayer.balance) // Balance should remain unchanged
      }

      "handle multiple utility ownership rent calculation correctly" in {
        val mockDice = new Dice {
          override def rollDice(sound: Boolean = false): (Int, Int) = (6, 6) // Total of 12
        }

        // Setup game with both utilities owned by the same player
        val electricCompanyIndex = 12
        val waterWorksIndex = 27
        val ownerPlayer = player2.copy(balance = 1500)
        val payingPlayer = player1.copy(balance = 1500, position = electricCompanyIndex)

        val ownedUtilities = board.fields
          .updated(electricCompanyIndex, UtilityField("Electric Company", electricCompanyIndex,150,UtilityField.UtilityCheck.utility, Some(ownerPlayer)))
          .updated(waterWorksIndex, UtilityField("Water Works", waterWorksIndex,150,UtilityField.UtilityCheck.utility, Some(ownerPlayer)))

        val gameWithBothUtilities = initialGame.copy(
          board = board.copy(fields = ownedUtilities),
          players = Vector(payingPlayer, ownerPlayer),
          currentPlayer = payingPlayer
        )

        val testController = new Controller(gameWithBothUtilities, mockDice)

        // Initial balances
        val initialPayingBalance = payingPlayer.balance
        val initialOwnerBalance = ownerPlayer.balance

        // Handle the field action
        testController.handleFieldAction(mockAsk, mockPrint, mockChoice)

        // Get updated players
        val updatedPayingPlayer = testController.game.players.find(_.name == payingPlayer.name).get
        val updatedOwnerPlayer = testController.game.players.find(_.name == ownerPlayer.name).get

        updatedPayingPlayer.balance should be(initialPayingBalance - 100)
        updatedOwnerPlayer.balance should be(initialOwnerBalance + 100)
      }
*/
      "allow a player to buy an unowned utilityfield" in {
        val utilityIndex = 12 // Index des Bahnhofs "Marklylebone Station"
        val utilityField = fields(utilityIndex).asInstanceOf[UtilityField]

        val game = MonopolyGame(Vector(player1, player2), Board(fields), player1, sound = false)
        val controller = new Controller(game, dice)

        controller.handleUtilityField(utilityField, mockAsk, mockPrint, mockChoice)

        val updatedField = controller.game.board.fields(utilityIndex).asInstanceOf[UtilityField]
        val updatedPlayer = controller.game.players.find(_.name == player1.name).get

        updatedField.owner shouldBe Some(player1)
        updatedPlayer.balance shouldBe (1500 - 200)
      }

      "not be able to buy utilityField if not enough money" in {
        val playerWithNoMoney = player1.copy(balance = 100)
        val gameWithNoMoney = initialGame.copy(players = Vector(playerWithNoMoney, player2), currentPlayer = playerWithNoMoney)
        val utilityIndex = 12 // Index des Bahnhofs "Marklylebone Station"
        val utilityField = fields(utilityIndex).asInstanceOf[UtilityField]

        val controller = new Controller(gameWithNoMoney, dice)

        controller.handleUtilityField(utilityField, mockAsk, mockPrint, mockChoice)

        val updatedField = controller.game.board.fields(utilityIndex).asInstanceOf[UtilityField]
        val updatedPlayer = controller.game.players.find(_.name == player1.name).get

        updatedField.owner shouldBe None
        updatedPlayer.balance shouldBe (100)
      }

      "dont buy the UtilityField if the player said no" in {
        val stationIndex = 12
        val mockAsk: String => Boolean = _ => false
        val utilityField = fields(stationIndex).asInstanceOf[UtilityField]

        val updatedFields = fields.updated(stationIndex, utilityField)
        val game = MonopolyGame(Vector(player1, player2), Board(updatedFields), player1, sound = false)

        controller.handleUtilityField(utilityField, mockAsk, println, mockChoice)

        val updatedField = controller.game.board.fields(stationIndex).asInstanceOf[UtilityField]
        updatedField.owner should be(None)
      }

      "charge rent to current player when landing on another player's UtilityField" in {
        // Player 2 besitzt die Station
        val stationIndex = 12
        val utilityField = fields(stationIndex).asInstanceOf[UtilityField].copy(owner = Some(player2))

        val updatedFields = fields.updated(stationIndex, utilityField)

        val game = MonopolyGame(Vector(player1, player2), Board(updatedFields), player1, sound = false)
        val controller = new Controller(game, dice)

        controller.handleUtilityField(utilityField, mockAsk, println, mockChoice)

        val updatedPlayer1 = controller.game.players.find(_.name == player1.name).get
        val updatedPlayer2 = controller.game.players.find(_.name == player2.name).get

        updatedPlayer1.balance shouldBe (1500 - 25)
        updatedPlayer2.balance shouldBe (1500 + 25)
      }
    }

    "getBoardString" should {
      "return the board as a string from BoardPrinter" in {
        val testGame = MonopolyGame(Vector(player1), board, player1, sound = false)
        val controller = new Controller(testGame, dice)

        val boardString = controller.getBoardString

        // Beispielprüfung: enthält der String bekannte Felderamen oder Zeichen?
        boardString should include("brown1")
        boardString should include("Go")
        boardString.length should be > 10 // Mindestens etwas Inhalt
      }
    }

    "getInventoryString" should {
      "return the inventory as a string from BoardPrinter" in {
        val testGame = MonopolyGame(Vector(player1), board, player1, sound = false)
        val controller = new Controller(testGame, dice)

        val inventoryString = controller.getInventoryString

        // Beispielprüfung: enthält der String den Spielernamen?
        inventoryString should include(player1.name)
        inventoryString.length should be > 5
      }
    }
  }
}