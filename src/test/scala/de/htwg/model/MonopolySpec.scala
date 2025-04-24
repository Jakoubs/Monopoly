package de.htwg.model
import de.htwg.model.Monopoly

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.model.PropertyField.Color.{Brown, DarkBlue, Green, LightBlue, Orange, Pink, Red, Yellow}

class MonopolySpec extends AnyWordSpec with Matchers {
  val player1 = Player("Player1", 1500, 1)
  val player2 = Player("Player2", 1500, 1)
  val brownProperty1 = PropertyField("Brown1", 2, 100, 10, None, Brown)
  val brownProperty1Owned = brownProperty1.copy(owner = Some(player1.name))
  val brownProperty1WithHouse = brownProperty1Owned.copy(house = PropertyField.House(1))
  val darkBlueProperty1 = PropertyField("DarkBlue1", 38, 200, 20, None, DarkBlue)
  val darkBlueProperty1Owned = darkBlueProperty1.copy(owner = Some(player1.name))
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
  "handlePlayerTurn" should {
    "call handleRegularTurn if player is not in jail" in {
      val resultGame = Monopoly.handlePlayerTurn(game)
      // Assert that handleRegularTurn was called (similarly, check for a side effect)
      resultGame.currentPlayer.isInJail should be(false)
    }
    "call handleJailTurn if player is in jail" in {
      val jailedPlayer = player1.copy(isInJail = true)
      val updatedGame = game.copy(players = Vector(jailedPlayer, player2), currentPlayer = jailedPlayer)
      val resultGame = Monopoly.handlePlayerTurn(updatedGame)
      // Assert that handleJailTurn was called (we can't directly test the call,
      // but we can check a side effect or the return type if it's distinct)
      resultGame.currentPlayer.isInJail should be(true)
    }
  }

  "buyProperty" should {
    "allow a player to buy an unowned property if they have enough money" in {
      val player = Player("Alice", 500, position = 38)
      val property = PropertyField("Blue1", index = 38, price = 300, rent = 30, owner = None, color = DarkBlue)
      val board = Board(Vector(property))
      val game = MonopolyGame(players = Vector(player), board = board, currentPlayer = player, sound = false)

      val updatedGame = buyProperty(game, propertyIndex = 38, player)

      val updatedProperty = updatedGame.board.fields.collectFirst {
        case p: PropertyField if p.index == 38 => p
      }.get

      val updatedPlayer = updatedGame.players.find(_.name == "Alice").get

      updatedProperty.owner shouldBe Some("Alice")
      updatedPlayer.balance shouldBe 200
      updatedPlayer.position shouldBe 38
    }

    "not allow a player to buy an unowned property if they have enough money" in {
      val player = Player("Alice", 250, position = 38)
      val property = PropertyField("Blue1", index = 38, price = 300, rent = 30, owner = None, color = DarkBlue)
      val board = Board(Vector(property))
      val game = MonopolyGame(players = Vector(player), board = board, currentPlayer = player, sound = false)

      val updatedGame = buyProperty(game, propertyIndex = 38, player)

      val updatedProperty = updatedGame.board.fields.collectFirst {
        case p: PropertyField if p.index == 38 => p
      }.get

      val updatedPlayer = updatedGame.players.find(_.name == "Alice").get

      updatedProperty.owner shouldBe None
      updatedPlayer.balance shouldBe 250
    }

    "not allow a Player to buy an owned PropertyField" in {
      val player = Player("Alice", 500, position = 38)
      val player2 = Player("Tim", 500, position = 22)
      val property = PropertyField("Blue1", index = 38, price = 300, rent = 30, owner = Some("Tim"), color = DarkBlue)
      val board = Board(Vector(property))
      val game = MonopolyGame(players = Vector(player), board = board, currentPlayer = player, sound = false)

      val updatedGame = buyProperty(game, propertyIndex = 38, player)

      val updatedProperty = updatedGame.board.fields.collectFirst {
        case p: PropertyField if p.index == 38 => p
      }.get

      val updatedPlayer = updatedGame.players.find(_.name == "Alice").get

      updatedProperty.owner shouldBe Some("Tim")
      updatedPlayer.balance shouldBe 500
    }

    "allow the player to buy an unowned train station if they have enough money" in {
      val player = Player("Alice", balance = 500, position = 6)
      val station = TrainStationField("Station1", idx = 6, owner = None)
      val board = Board(Vector(station))
      val game = MonopolyGame(players = Vector(player), board = board, currentPlayer = player, sound = false)

      val updatedGame = buyProperty(game, propertyIndex = 6, player)

      val updatedStation = updatedGame.board.fields.collectFirst {
        case s: TrainStationField if s.index == 6 => s
      }.get

      val updatedPlayer = updatedGame.players.find(_.name == "Alice").get

      updatedStation.owner shouldBe Some("Alice")
      updatedPlayer.balance shouldBe 300 // 500 - 200
      updatedPlayer.position shouldBe 6
    }

    "not allow the player to buy a train station if they don't have enough money" in {
      val player = Player("Bob", balance = 150, position = 6)
      val station = TrainStationField("Station1", idx = 6, owner = None)
      val board = Board(Vector(station))
      val game = MonopolyGame(players = Vector(player), board = board, currentPlayer = player, sound = false)

      val updatedGame = buyProperty(game, propertyIndex = 6, player)

      val unchangedStation = updatedGame.board.fields.collectFirst {
        case s: TrainStationField if s.index == 6 => s
      }.get

      val unchangedPlayer = updatedGame.players.find(_.name == "Bob").get

      unchangedStation.owner shouldBe None
      unchangedPlayer.balance shouldBe 150
      unchangedPlayer.position shouldBe 6
    }

    "not allow the player to buy a train station that is already owned" in {
      val player = Player("Charlie", balance = 800, position = 6)
      val station = TrainStationField("Station1", idx = 6, owner = Some("SomeoneElse"))
      val board = Board(Vector(station))
      val game = MonopolyGame(players = Vector(player), board = board, currentPlayer = player, sound = false)

      val updatedGame = buyProperty(game, propertyIndex = 6, player)

      val unchangedStation = updatedGame.board.fields.collectFirst {
        case s: TrainStationField if s.index == 6 => s
      }.get

      val unchangedPlayer = updatedGame.players.find(_.name == "Charlie").get

      unchangedStation.owner shouldBe Some("SomeoneElse")
      unchangedPlayer.balance shouldBe 800
      unchangedPlayer.position shouldBe 6
    }

    "allow the player to buy an unowned UtilityField if they have enough money" in {
      val player = Player("Alice", balance = 500, position = 6)
      val station = UtilityField("Wasserwerk", idx = 6, owner = None)
      val board = Board(Vector(station))
      val game = MonopolyGame(players = Vector(player), board = board, currentPlayer = player, sound = false)

      val updatedGame = buyProperty(game, propertyIndex = 6, player)

      val updatedUtility = updatedGame.board.fields.collectFirst {
        case s: UtilityField if s.index == 6 => s
      }.get

      val updatedPlayer = updatedGame.players.find(_.name == "Alice").get

      updatedUtility.owner shouldBe Some("Alice")
      updatedPlayer.balance shouldBe 350
      updatedPlayer.position shouldBe 6
    }

    "not allow the player to buy a UtilityField if they don't have enough money" in {
      val player = Player("Bob", balance = 100, position = 6)
      val station = UtilityField("Wasserwerk", idx = 6, owner = None)
      val board = Board(Vector(station))
      val game = MonopolyGame(players = Vector(player), board = board, currentPlayer = player, sound = false)

      val updatedGame = buyProperty(game, propertyIndex = 6, player)

      val updatedUtility = updatedGame.board.fields.collectFirst {
        case s: UtilityField if s.index == 6 => s
      }.get

      val unchangedPlayer = updatedGame.players.find(_.name == "Bob").get

      updatedUtility.owner shouldBe None
      unchangedPlayer.balance shouldBe 100
      unchangedPlayer.position shouldBe 6
    }

    "not allow the player to buy a UtilityField that is already owned" in {
      val player = Player("Charlie", balance = 800, position = 6)
      val station = UtilityField("Wasserwerk", idx = 6, owner = Some("SomeoneElse"))
      val board = Board(Vector(station))
      val game = MonopolyGame(players = Vector(player), board = board, currentPlayer = player, sound = false)

      val updatedGame = buyProperty(game, propertyIndex = 6, player)

      val updatedUtility = updatedGame.board.fields.collectFirst {
        case s: UtilityField if s.index == 6 => s
      }.get

      val unchangedPlayer = updatedGame.players.find(_.name == "Charlie").get

      updatedUtility.owner shouldBe Some("SomeoneElse")
      unchangedPlayer.balance shouldBe 800
      unchangedPlayer.position shouldBe 6
    }

    "not allow buying a non-purchasable field (e.g. GoField)" in {
      val player = Player("Eve", balance = 500, position = 1)
      val goField = GoField
      val board = Board(Vector(goField)) // GoField hat index = 1
      val game = MonopolyGame(players = Vector(player), board = board, currentPlayer = player, sound = false)

      val updatedGame = buyProperty(game, propertyIndex = 1, player)

      updatedGame shouldBe game // Es darf sich nichts ändern
    }

    "not allow buying if the field index does not exist" in {
      val player = Player("Frank", balance = 500, position = 1)
      val property = PropertyField("Blue1", index = 2, price = 300, rent = 30, owner = None, color = DarkBlue)
      val board = Board(Vector(property)) // Es gibt kein Feld mit index = 99
      val game = MonopolyGame(players = Vector(player), board = board, currentPlayer = player, sound = false)

      val updatedGame = buyProperty(game, propertyIndex = 99, player)

      updatedGame shouldBe game
    }
  }



  "buyHouse" should {

    "allow the owner to buy a house if they have enough money" in {
      val player = Player("Alice", balance = 500, position = 2)
      val property = PropertyField("Blue1", index = 2, price = 300, rent = 30, owner = Some("Alice"),
        color = DarkBlue, mortgage = PropertyField.Mortgage(10, false), house = PropertyField.House(0))
      val board = Board(Vector(property))
      val game = MonopolyGame(players = Vector(player), board = board, currentPlayer = player, sound = false)

      val updatedGame = buyHouse(game, propertyIndex = 2, player)

      val updatedProperty = updatedGame.board.fields.collectFirst {
        case p: PropertyField if p.index == 2 => p
      }.get

      val updatedPlayer = updatedGame.players.find(_.name == "Alice").get

      updatedProperty.house.amount shouldBe 1
      updatedPlayer.balance shouldBe 450 // Haus kostet 50
    }

    "not allow a house purchase if the player does not own the property" in {
      val player = Player("Bob", balance = 500, position = 2)
      val property = PropertyField("Blue1", index = 2, price = 300, rent = 30, owner = Some("SomeoneElse"),
        color = DarkBlue, mortgage = PropertyField.Mortgage(10, false), house = PropertyField.House(0))
      val board = Board(Vector(property))
      val game = MonopolyGame(players = Vector(player), board = board, currentPlayer = player, sound = false)

      val (updatedGame) = buyHouse(game, propertyIndex = 2, player)

      val unchangedProperty = updatedGame.board.fields.collectFirst {
        case p: PropertyField if p.index == 2 => p
      }.get

      val unchangedPlayer = updatedGame.players.find(_.name == "Bob").get

      unchangedProperty.house.amount shouldBe 0
      unchangedPlayer.balance shouldBe 500
    }

    "not allow a house purchase if the player has insufficient funds" in {
      val player = Player("Carol", balance = 30, position = 2)
      val property = PropertyField("Blue1", index = 2, price = 300, rent = 30, owner = Some("Carol"),
        color = DarkBlue, mortgage = PropertyField.Mortgage(10, false), house = PropertyField.House(0))
      val board = Board(Vector(property))
      val game = MonopolyGame(players = Vector(player), board = board, currentPlayer = player, sound = false)

      val updatedGame = buyHouse(game, propertyIndex = 2, player)

      val unchangedProperty = updatedGame.board.fields.collectFirst {
        case p: PropertyField if p.index == 2 => p
      }.get

      val unchangedPlayer = updatedGame.players.find(_.name == "Carol").get

      unchangedProperty.house.amount shouldBe 0
      unchangedPlayer.balance shouldBe 30
    }

    "not allow building a house on a non-PropertyField" in {
      val player = Player("Dave", balance = 500, position = 6)
      val station = TrainStationField("Station", idx = 6, owner = Some("Dave"))
      val board = Board(Vector(station))
      val game = MonopolyGame(players = Vector(player), board = board, currentPlayer = player, sound = false)

      val (updatedGame) = buyHouse(game, propertyIndex = 6, player)

      updatedGame shouldBe game
    }

    "not allow building a house on a Property not existing" in {
      val player = Player("Carol", balance = 30, position = 2)
      val property = PropertyField("Blue1", index = 2, price = 300, rent = 30, owner = Some("Carol"),
        color = DarkBlue, mortgage = PropertyField.Mortgage(10, false), house = PropertyField.House(0))
      val board = Board(Vector(property))
      val game = MonopolyGame(players = Vector(player), board = board, currentPlayer = player, sound = false)

      val updatedGame = buyHouse(game, propertyIndex = 45, player)

      updatedGame shouldBe game
    }
  }

  "getInventory" should {
    "only adds fields in inventory for current player" in {
      val player = Player("Carol", balance = 30, position = 2)
      val player2 = Player("Alice", balance = 140, position = 4)
      val property = PropertyField("Blue1", index = 2, price = 300, rent = 30, owner = Some("Carol"),
        color = DarkBlue, mortgage = PropertyField.Mortgage(10, false), house = PropertyField.House(0))
      val board = Board(Vector(property))
      val game = MonopolyGame(players = Vector(player), board = board, currentPlayer = player, sound = false)

      val GameOutput = getInventory(game)

      GameOutput shouldEqual("INVENTORY Player: Carol| idx:2[0]")
    }

    "correctly display Property fields with house amounts" in {
      val player = Player("Carol", balance = 30, position = 2)

      val property = TrainStationField("Blue1", idx = 2, Some("Carol"))

      val board = Board(Vector(property))
      val game = MonopolyGame(players = Vector(player), board = board, currentPlayer = player, sound = false)

      val updatedGame = getInventory(game)

      updatedGame shouldEqual "INVENTORY Player: Carol| idx:2"
    }

    "correctly remove the trailing comma" in {
      val player = Player("Carol", balance = 30, position = 2)

      val property1 = PropertyField("Blue1", index = 2, price = 300, rent = 30, owner = Some("Carol"),
        color = DarkBlue, mortgage = PropertyField.Mortgage(10, false), house = PropertyField.House(1))
      val property2 = UtilityField("Green1", idx = 4, owner = Some("Carol"))

      val board = Board(Vector(property1, property2))
      val game = MonopolyGame(players = Vector(player), board = board, currentPlayer = player, sound = false)

      val updatedGame = getInventory(game)

      updatedGame shouldEqual("INVENTORY Player: Carol| idx:2[1], idx:4")
    }

    "return an empty inventory if the player has no fields" in {
      val player = Player("Carol", balance = 30, position = 2)

      val board = Board(Vector())
      val game = MonopolyGame(players = Vector(player), board = board, currentPlayer = player, sound = false)

      val updatedGame = getInventory(game)

      updatedGame shouldEqual "INVENTORY Player: Carol| "
    }
  }

  "getStats" should {
    "correctly split player info into four strings" in {
      val player1 = Player("Carol", balance = 200, position = 5, isInJail = false)
      val player2 = Player("Bob", balance = 150, position = 8, isInJail = true)
      val player3 = Player("Jane", balance = 300, position = 12, isInJail = false)
      val player4 = Player("Steve", balance = 100, position = 4, isInJail = false)

      val game = MonopolyGame(players = Vector(player1, player2, player3, player4), board = Board(Vector()), currentPlayer = player4, sound = false)

      val (s1, s2, s3, s4) = getStats(game)

      // Sicherstellen, dass jeder String weniger als oder gleich 20 Zeichen enthält
      s1.length should be <= 20
      s2.length should be <= 20
      s3.length should be <= 20
      s4.length should be <= 20
    }

    "correctly handle less than 4 players" in {
      val player1 = Player("Carol", balance = 200, position = 5, isInJail = false)
      val player2 = Player("Bob", balance = 150, position = 8, isInJail = true)

      val game = MonopolyGame(players = Vector(player1, player2), board = Board(Vector()), currentPlayer = player2, sound = false)

      val (s1, s2, s3, s4) = getStats(game)

      s1 should not be empty
      s2 should not be empty
      s3 shouldBe empty
      s4 shouldBe empty
    }

    "handle long player info properly" in {
      val player1 = Player("Carol", balance = 200, position = 5, isInJail = false)
      val player2 = Player("VincentDerTee", balance = 150, position = 8, isInJail = true)
      val player3 = Player("JakobderDritteMann", balance = 300, position = 12, isInJail = false)
      val player4 = Player("Steve", balance = 100, position = 4, isInJail = false)

      val game = MonopolyGame(players = Vector(player1, player2, player3, player4), board = Board(Vector()), currentPlayer = player4, sound = false)

      val (s1, s2, s3, s4) = getStats(game)

      s1.length should be <= 20
      s2.length should be <= 20
      s3.length should be <= 20
      s4.length should be <= 20
    }
  }
}