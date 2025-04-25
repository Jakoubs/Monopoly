error id: scala/None.
file://<WORKSPACE>/src/test/scala/de/htwg/model/MonopolySpec.scala
empty definition using pc, found symbol in pc: scala/None.
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 863
uri: file://<WORKSPACE>/src/test/scala/de/htwg/model/MonopolySpec.scala
text:
```scala
package de.htwg.model

import de.htwg.model.PropertyField.Color.{Brown, DarkBlue}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import java.io.StringReader

class MonopolySpec extends AnyWordSpec with Matchers {
  "Monopoly main" should {
    "run without throwing an exception" in {
      noException should be thrownBy Monopoly.main(Array.empty)
    }
  }
  val player1 = Player("Player1", 1500, 1)
  val player2 = Player("Player2", 1500, 1)
  val brownProperty1 = PropertyField("Brown1", 2, 100, 10, None, Brown)
  val brownProperty1Owned = brownProperty1.copy(owner = Some(player1.name))
  val brownProperty1WithHouse = brownProperty1Owned.copy(house = PropertyField.House(1))
  val darkBlueProperty1 = PropertyField("DarkBlue1", 38, 200, 20, None, DarkBlue)
  val darkBlueProperty1Owned = darkBlueProperty1.copy(owne@@r = Some(player1.name))
  val trainStation = TrainStationField("Station1", 6, None)
  val trainStationOwned = trainStation.copy(owner = Some(player1.name))
  val utility = UtilityField("Utility1", 13, None)
  val utilityOwned = utility.copy(owner = Some(player1.name))
  val goToJail = GoToJailField()
  val taxField = TaxField(100, 5)
  val freeParkingField = FreeParkingField(50)
  val board = Board(Vector(GoField, brownProperty1, CommunityChestField(3), brownProperty1, taxField, trainStation,
    PropertyField("LightBlue1", 7, 120, 12, None, PropertyField.Color.LightBlue), ChanceField(8),
    PropertyField("LightBlue2", 9, 120, 12, None, PropertyField.Color.LightBlue),
    PropertyField("LightBlue3", 10, 120, 12, None, PropertyField.Color.LightBlue), JailField,
    PropertyField("Pink1", 12, 140, 14, None, PropertyField.Color.Pink), utility,
    PropertyField("Pink2", 14, 140, 14, None, PropertyField.Color.Pink),
    PropertyField("Pink3", 15, 140, 14, None, PropertyField.Color.Pink),
    TrainStationField("Station2", 16, None),
    PropertyField("Orange1", 17, 160, 16, None, PropertyField.Color.Orange), CommunityChestField(18),
    PropertyField("Orange2", 19, 160, 16, None, PropertyField.Color.Orange),
    PropertyField("Orange3", 20, 160, 16, None, PropertyField.Color.Orange), freeParkingField,
    PropertyField("Red1", 22, 180, 18, None, PropertyField.Color.Red), ChanceField(23),
    PropertyField("Red2", 24, 180, 18, None, PropertyField.Color.Red),
    PropertyField("Red3", 25, 180, 18, None, PropertyField.Color.Red),
    TrainStationField("Station3", 26, None),
    PropertyField("Yellow1", 27, 200, 20, None, PropertyField.Color.Yellow),
    PropertyField("Yellow2", 28, 200, 20, None, PropertyField.Color.Yellow), UtilityField("Utility2", 29, None),
    PropertyField("Yellow3", 30, 200, 20, None, PropertyField.Color.Yellow), goToJail,
    PropertyField("Green1", 32, 220, 22, None, PropertyField.Color.Green),
    PropertyField("Green2", 33, 220, 22, None, PropertyField.Color.Green), ChanceField(34),
    PropertyField("Green3", 35, 220, 22, None, PropertyField.Color.Green),
    TrainStationField("Station4", 36, None), ChanceField(37), darkBlueProperty1, taxField,
    darkBlueProperty1
  ))
  val game = MonopolyGame(Vector(player1, player2), board, player1, false)
  val gameWithSound = MonopolyGame(Vector(player1, player2), board, player1, true)

  "handlePlayerTurn" should {
    "call handleJailTurn if player is in jail" in {
      val jailedPlayer = player1.copy(isInJail = true)
      val updatedGame = game.copy(players = Vector(jailedPlayer, player2), currentPlayer = jailedPlayer)
      Monopoly.handlePlayerTurn(updatedGame).currentPlayer.isInJail should be(true) // Assuming handleJailTurn doesn't immediately release
    }
    "call handleRegularTurn if player is not in jail" in {
      Monopoly.handlePlayerTurn(game).currentPlayer.position should be >= 1
    }
  }

  "handleRegularTurn" should {
    "roll dice, move player, and handle field action" in {
      val initialPosition = game.currentPlayer.position
      val updatedGame = Monopoly.handleRegularTurn(game.copy(board = Board(Vector.fill(40)(GoField)))) // Ensure no immediate special field
      updatedGame.currentPlayer.position should not be initialPosition
    }
  }

  "handleOptionalActions" should {
    "handle buying a house" in {
      val gameWithProperty = game.copy(board = Board(Vector(GoField, brownProperty1Owned)), currentPlayer = player1)
      val input = List("1", "2").mkString("\n")
      Console.withIn(new StringReader(input)) {
        val updatedGame = Monopoly.handleOptionalActions(gameWithProperty)
        updatedGame.board.fields.head match {
          case p: PropertyField => p.house.amount should be(1)
          case _ => fail("Expected PropertyField")
        }
      }
    }
    "handle trade option (not implemented)" in {
      val input = List("2", "x").mkString("\n")
      Console.withIn(new StringReader(input)) {
        Monopoly.handleOptionalActions(game) should be(game)
      }
    }
    "handle mortgage option (not implemented)" in {
      val input = List("3", "x").mkString("\n")
      Console.withIn(new StringReader(input)) {
        Monopoly.handleOptionalActions(game) should be(game)
      }
    }
    "do nothing on 'x'" in {
      val input = List("x").mkString("\n")
      Console.withIn(new StringReader(input)) {
        Monopoly.handleOptionalActions(game) should be(game)
      }
    }
    "handle invalid input" in {
      val input = List("invalid", "x").mkString("\n")
      Console.withIn(new StringReader(input)) {
        Monopoly.handleOptionalActions(game) should be(game)
      }
    }
  }

  "caseDiceJail" should {
    "release player on doubles" in {
      val jailedPlayer = player1.copy(isInJail = true)
      val updatedGame = game.copy(players = Vector(jailedPlayer, player2), currentPlayer = jailedPlayer)
      val input = List("1", "1").mkString("\n") // Simulate rolling doubles
      Console.withIn(new StringReader(input)) {
        val resultGame = Monopoly.caseDiceJail(updatedGame.copy(sound = false))
        resultGame.currentPlayer.isInJail should be(false)
        resultGame.currentPlayer.jailTurns should be(0)
      }
    }
    "increment jail turns on non-doubles" in {
      val jailedPlayer = player1.copy(isInJail = true, jailTurns = 0)
      val updatedGame = game.copy(players = Vector(jailedPlayer, player2), currentPlayer = jailedPlayer)
      val input = List("1", "2").mkString("\n") // Simulate not rolling doubles
      Console.withIn(new StringReader(input)) {
        val resultGame = Monopoly.caseDiceJail(updatedGame.copy(sound = false))
        resultGame.currentPlayer.isInJail should be(true)
        resultGame.currentPlayer.jailTurns should be(1)
      }
    }
    "force pay and release after 3 failed attempts" in {
      val jailedPlayer = player1.copy(isInJail = true, jailTurns = 2, balance = 100)
      val updatedGame = game.copy(players = Vector(jailedPlayer, player2), currentPlayer = jailedPlayer)
      val input = List("1", "2").mkString("\n") // Simulate not rolling doubles
      Console.withIn(new StringReader(input)) {
        val resultGame = Monopoly.caseDiceJail(updatedGame.copy(sound = false))
        resultGame.currentPlayer.isInJail should be(false)
        resultGame.currentPlayer.jailTurns should be(0)
        resultGame.currentPlayer.balance should be(50)
      }
    }
    "handle insufficient funds after 3 failed attempts" in {
      val jailedPlayer = player1.copy(isInJail = true, jailTurns = 2, balance = 40)
      val updatedGame = game.copy(players = Vector(jailedPlayer, player2), currentPlayer = jailedPlayer)
      val input = List("1", "2").mkString("\n") // Simulate not rolling doubles
      Console.withIn(new StringReader(input)) {
        val resultGame = Monopoly.caseDiceJail(updatedGame.copy(sound = false))
        resultGame.currentPlayer.isInJail should be(true)
        resultGame.currentPlayer.jailTurns should be(3)
        resultGame.currentPlayer.balance should be(40)
      }
    }
  }

  "handleJailTurn" should {
    "handle paying to get out" in {
      val jailedPlayer = player1.copy(isInJail = true, balance = 100)
      val updatedGame = game.copy(players = Vector(jailedPlayer, player2), currentPlayer = jailedPlayer)
      val input = List("1", "1", "1").mkString("\n") // Choose option 1
      Console.withIn(new StringReader(input)) {
        val resultGame = Monopoly.handleJailTurn(updatedGame.copy(sound = false))
        resultGame.currentPlayer.isInJail should be(false)
        resultGame.currentPlayer.balance should be < 100
        resultGame.currentPlayer.jailTurns should be(0)
      }
    }
    "handle insufficient funds to pay" in {
      val jailedPlayer = player1.copy(isInJail = true, balance = 40)
      val updatedGame = game.copy(players = Vector(jailedPlayer, player2), currentPlayer = jailedPlayer)
      val input = List("1").mkString("\n") // Choose option 1
      Console.withIn(new StringReader(input)) {
        val resultGame = Monopoly.handleJailTurn(updatedGame.copy(sound = false))
        resultGame.currentPlayer.isInJail should be(true)
        resultGame.currentPlayer.balance should be(40)
      }
    }
    "handle using a 'Get Out of Jail Free' card (not implemented)" in {
      val jailedPlayer = player1.copy(isInJail = true) // Assuming player has a card (not tracked yet)
      val updatedGame = game.copy(players = Vector(jailedPlayer, player2), currentPlayer = jailedPlayer)
      val input = List("2").mkString("\n") // Choose option 2
      Console.withIn(new StringReader(input)) {
        val resultGame = Monopoly.handleJailTurn(updatedGame.copy(sound = false))
        resultGame.currentPlayer.isInJail should be(true) // Not implemented yet
      }
    }
    "handle trying to roll doubles" in {
      val jailedPlayer = player1.copy(isInJail = true)
      val updatedGame = game.copy(players = Vector(jailedPlayer, player2), currentPlayer = jailedPlayer)
      val input = List("3", "1", "1").mkString("\n") // Choose option 3, then roll doubles
      Console.withIn(new StringReader(input)) {
        val resultGame = Monopoly.handleJailTurn(updatedGame.copy(sound = false))
        resultGame.currentPlayer.isInJail should be(false)
      }
    }
    "handle invalid choice" in {
      val jailedPlayer = player1.copy(isInJail = true)
      val updatedGame = game.copy(players = Vector(jailedPlayer, player2), currentPlayer = jailedPlayer)
      val input = List("4", "1", "1").mkString("\n") // Choose invalid option, then roll doubles
      Console.withIn(new StringReader(input)) {
        val resultGame = Monopoly.handleJailTurn(updatedGame.copy(sound = false))
        resultGame.currentPlayer.isInJail should be(false) // Defaults to rolling doubles
      }
    }
  }

  "handleFieldAction" should {
    "handle GoToJailField" in {
      val updatedGame = Monopoly.handleFieldAction(game, 31)
      updatedGame.currentPlayer.isInJail should be(true)
      updatedGame.currentPlayer.position should be(11)
    }
    "handle TaxField" in {
      val initialBalance = game.currentPlayer.balance
      val updatedGame = Monopoly.handleFieldAction(game, 5)
      updatedGame.currentPlayer.balance should be(initialBalance - 100)
      updatedGame.board.fields.find(_.isInstanceOf[FreeParkingField]).get.asInstanceOf[FreeParkingField].amount should be(100)
    }
    "handle FreeParkingField" in {
      val initialBalance = game.currentPlayer.balance
      val updatedGame = Monopoly.handleFieldAction(game.copy(board = Board(Vector.fill(21)(GoField) ++ Vector(freeParkingField))), 21)
      updatedGame.currentPlayer.balance should be(initialBalance + 50)
      updatedGame.board.fields.find(_.isInstanceOf[FreeParkingField]).get.asInstanceOf[FreeParkingField].amount should be(0)
    }
    "handle PropertyField (unowned)" in {
      val input = List("y").mkString("\n")
      Console.withIn(new StringReader(input)) {
        val (updatedGame, _) = Monopoly.buyProperty(game, 2, game.currentPlayer)
        Monopoly.handleFieldAction(updatedGame, 2).board.fields(1).asInstanceOf[PropertyField].owner should be(Some("Player1"))
      }
      val inputN = List("n").mkString("\n")
      Console.withIn(new StringReader(inputN)) {
        Monopoly.handleFieldAction(game, 2).board.fields(1).asInstanceOf[PropertyField].owner should be(None)
      }
    }
    "handle PropertyField (owned by other)" in {
      val gameWithOwnedProperty = game.copy(board = Board(Vector(GoField, brownProperty1Owned)), currentPlayer = player2)
      val initialBalancePlayer2 = gameWithOwnedProperty.currentPlayer.balance
      val initialBalancePlayer1 = gameWithOwnedProperty.players.find(_.name == "Player1").get.balance
      val updatedGame = Monopoly.handleFieldAction(gameWithOwnedProperty, 2)
      updatedGame.currentPlayer.balance should be(initialBalancePlayer2 - 10)
      updatedGame.players.find(_.name == "Player1").get.balance should be(initialBalancePlayer1 + 10)
    }
    "handle PropertyField (owned by current player)" in {
      val gameWithOwnedProperty = game.copy(board = Board(Vector(GoField, brownProperty1Owned)), currentPlayer = player1)
      Monopoly.handleFieldAction(gameWithOwnedProperty, 2) should be(gameWithOwnedProperty)
    }
    "handle other fields" in {
      Monopoly.handleFieldAction(game, 1) should be(game) // GoField
      Monopoly.handleFieldAction(game, 3) should be(game) // CommunityChestField
      Monopoly.handleFieldAction(game, 8) should be(game) // ChanceField
    }
    "throw exception for non-existent field" in {
      intercept[Exception] {
        Monopoly.handleFieldAction(game, 99)
      }
    }
  }

  "handlePropertyField" should {
    "allow current player to buy unowned property" in {
      val input = List("y").mkString("\n")
      Console.withIn(new StringReader(input)) {
        val updatedGame = Monopoly.handlePropertyField(game
```


#### Short summary: 

empty definition using pc, found symbol in pc: scala/None.