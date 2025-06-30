package de.htwg.model

import de.htwg.Board
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import de.htwg.model.modelBaseImple.PropertyField.Color.{Brown, DarkBlue, Green, LightBlue, Orange, Pink, Red, Yellow}
import de.htwg.model.modelBaseImple.{ChanceField, CommunityChestField, FreeParkingField, GoField, GoToJailField, JailField, MonopolyGame, Player, PropertyField, TaxField, TrainStationField, UtilityField}
import de.htwg.view.BoardPrinter

class BoardPrinterSpec extends AnyWordSpec {
  val player1 = Player("A", 1500, 12, isInJail = false, 0)
  val player2 = Player("B", 1500, 1, isInJail = false, 0)
  val brownProperty1 = PropertyField("Brown1", 2, 100, 10, None, Brown)
  val brownProperty1Owned = brownProperty1.copy(owner = Some(player1))
  val brownProperty1WithHouse = brownProperty1Owned.copy(house = PropertyField.House(1))
  val darkBlueProperty1 = PropertyField("DarkBlue1", 38, 200, 20, None, DarkBlue)
  val darkBlueProperty1Owned = darkBlueProperty1.copy(owner = Some(player1))
  val redProperty1 = PropertyField("Red1", 22, 220, 18, None, Red,
    PropertyField.Mortgage(110, false), PropertyField.House(0))
  val redProperty1Owned = redProperty1.copy(owner = Some(player1))
  val trainStation = TrainStationField("Station1", 6, 200, None)
  val trainStationOwned = trainStation.copy(owner = Some(player1))
  val utility = UtilityField("Utility1", 13, 150, UtilityField.UtilityCheck.utility, None)
  val utilityOwned = utility.copy(owner = Some(player1))
  val goToJail = GoToJailField()
  val taxField = TaxField(100, 5)
  val freeParkingField = FreeParkingField(50)
  val chanceField = ChanceField(8)

  val board = Board(
    Vector(
      GoField,
      PropertyField("brown1", 2, 60, 2, None, color = PropertyField.Color.Brown, PropertyField.Mortgage(30, false), PropertyField.House(0)),
      CommunityChestField(3),
      PropertyField("brown2", 4, 60, 4, None, color = PropertyField.Color.Brown, PropertyField.Mortgage(30, false), PropertyField.House(0)),
      TaxField(100, 5),
      TrainStationField("Marklylebone Station", 6, 200, None),
      PropertyField("lightBlue1", 7, 100, 6, None, color = PropertyField.Color.LightBlue, PropertyField.Mortgage(50, false), PropertyField.House(0)),
      ChanceField(8),
      PropertyField("lightBlue2", 9, 100, 6, None, color = PropertyField.Color.LightBlue, PropertyField.Mortgage(50, false), PropertyField.House(0)),
      PropertyField("lightBlue3", 10, 120, 8, None, color = PropertyField.Color.LightBlue, PropertyField.Mortgage(60, false), PropertyField.House(0)),
      JailField,
      PropertyField("Pink1", 12, 140, 10, None, color = PropertyField.Color.Pink, PropertyField.Mortgage(70, false), PropertyField.House(0)),
      UtilityField("Electric Company", 13, 150, UtilityField.UtilityCheck.utility, None),
      PropertyField("Pink2", 14, 140, 10, None, color = PropertyField.Color.Pink, PropertyField.Mortgage(70, false), PropertyField.House(0)),
      PropertyField("Pink3", 15, 160, 12, None, color = PropertyField.Color.Pink, PropertyField.Mortgage(80, false), PropertyField.House(0)),
      TrainStationField("Fenchurch ST Station", 16, 200, None),
      PropertyField("Orange1", 17, 180, 14, None, color = PropertyField.Color.Orange, PropertyField.Mortgage(90, false), PropertyField.House(0)),
      CommunityChestField(18),
      PropertyField("Orange2", 19, 180, 14, None, color = PropertyField.Color.Orange, PropertyField.Mortgage(90, false), PropertyField.House(0)),
      PropertyField("Orange3", 20, 200, 16, None, color = PropertyField.Color.Orange, PropertyField.Mortgage(100, false), PropertyField.House(0)),
      FreeParkingField(0),
      PropertyField("Red1", 22, 220, 18, None, color = PropertyField.Color.Red, PropertyField.Mortgage(110, false), PropertyField.House(0)),
      ChanceField(23),
      PropertyField("Red2", 24, 220, 18, None, color = PropertyField.Color.Red, PropertyField.Mortgage(110, false), PropertyField.House(0)),
      PropertyField("Red3", 25, 240, 20, None, color = PropertyField.Color.Red, PropertyField.Mortgage(120, false), PropertyField.House(0)),
      TrainStationField("King's Cross Station", 26, 200, None),
      PropertyField("Yellow1", 27, 260, 22, None, color = PropertyField.Color.Yellow, PropertyField.Mortgage(130, false), PropertyField.House(0)),
      UtilityField("Water Works", 28, 150, UtilityField.UtilityCheck.utility, None),
      PropertyField("Yellow2", 29, 260, 22, None, color = PropertyField.Color.Yellow, PropertyField.Mortgage(130, false), PropertyField.House(0)),
      PropertyField("Yellow3", 30, 280, 24, None, color = PropertyField.Color.Yellow, PropertyField.Mortgage(140, false), PropertyField.House(0)),
      GoToJailField(),
      PropertyField("Green1", 32, 300, 26, None, color = PropertyField.Color.Green, PropertyField.Mortgage(150, false), PropertyField.House(0)),
      PropertyField("Green2", 33, 300, 26, None, color = PropertyField.Color.Green, PropertyField.Mortgage(150, false), PropertyField.House(0)),
      CommunityChestField(34),
      PropertyField("Green3", 35, 320, 28, None, color = PropertyField.Color.Green, PropertyField.Mortgage(160, false), PropertyField.House(0)),
      TrainStationField("Liverpool ST Station", 36, 200, None),
      ChanceField(37),
      PropertyField("DarkBlue1", 38, 350, 35, None, color = PropertyField.Color.DarkBlue, PropertyField.Mortgage(175, false), PropertyField.House(0)),
      TaxField(200, 39),
      PropertyField("DarkBlue2", 40, 400, 50, None, color = PropertyField.Color.DarkBlue, PropertyField.Mortgage(200, false), PropertyField.House(0))
    )
  )

  // Create a board with owned properties
  val boardWithOwnedProperties = Board(
    Vector(
      GoField,
      brownProperty1Owned,
      CommunityChestField(3),
      PropertyField("brown2", 4, 60, 4, Some(player1), color = PropertyField.Color.Brown, PropertyField.Mortgage(30, false), PropertyField.House(1)),
      TaxField(100, 5),
      trainStationOwned,
      PropertyField("lightBlue1", 7, 100, 6, None, color = PropertyField.Color.LightBlue, PropertyField.Mortgage(50, false), PropertyField.House(0)),
      ChanceField(8),
      PropertyField("lightBlue2", 9, 100, 6, None, color = PropertyField.Color.LightBlue, PropertyField.Mortgage(50, false), PropertyField.House(0)),
      PropertyField("lightBlue3", 10, 120, 8, None, color = PropertyField.Color.LightBlue, PropertyField.Mortgage(60, false), PropertyField.House(0)),
      JailField,
      PropertyField("Pink1", 12, 140, 10, None, color = PropertyField.Color.Pink, PropertyField.Mortgage(70, false), PropertyField.House(0)),
      utilityOwned,
      PropertyField("Pink2", 14, 140, 10, None, color = PropertyField.Color.Pink, PropertyField.Mortgage(70, false), PropertyField.House(0)),
      PropertyField("Pink3", 15, 160, 12, None, color = PropertyField.Color.Pink, PropertyField.Mortgage(80, false), PropertyField.House(0)),
      TrainStationField("Fenchurch ST Station", 16, 200, None),
      PropertyField("Orange1", 17, 180, 14, None, color = PropertyField.Color.Orange, PropertyField.Mortgage(90, false), PropertyField.House(0)),
      CommunityChestField(18),
      PropertyField("Orange2", 19, 180, 14, None, color = PropertyField.Color.Orange, PropertyField.Mortgage(90, false), PropertyField.House(0)),
      PropertyField("Orange3", 20, 200, 16, None, color = PropertyField.Color.Orange, PropertyField.Mortgage(100, false), PropertyField.House(0)),
      FreeParkingField(50),
      redProperty1Owned,
      ChanceField(23),
      PropertyField("Red2", 24, 220, 18, None, color = PropertyField.Color.Red, PropertyField.Mortgage(110, false), PropertyField.House(0)),
      PropertyField("Red3", 25, 240, 20, None, color = PropertyField.Color.Red, PropertyField.Mortgage(120, false), PropertyField.House(0)),
      TrainStationField("King's Cross Station", 26, 200, None),
      PropertyField("Yellow1", 27, 260, 22, None, color = PropertyField.Color.Yellow, PropertyField.Mortgage(130, false), PropertyField.House(0)),
      UtilityField("Water Works", 28, 150, UtilityField.UtilityCheck.utility, None),
      PropertyField("Yellow2", 29, 260, 22, None, color = PropertyField.Color.Yellow, PropertyField.Mortgage(130, false), PropertyField.House(0)),
      PropertyField("Yellow3", 30, 280, 24, None, color = PropertyField.Color.Yellow, PropertyField.Mortgage(140, false), PropertyField.House(0)),
      GoToJailField(),
      PropertyField("Green1", 32, 300, 26, None, color = PropertyField.Color.Green, PropertyField.Mortgage(150, false), PropertyField.House(0)),
      PropertyField("Green2", 33, 300, 26, None, color = PropertyField.Color.Green, PropertyField.Mortgage(150, false), PropertyField.House(0)),
      CommunityChestField(34),
      PropertyField("Green3", 35, 320, 28, None, color = PropertyField.Color.Green, PropertyField.Mortgage(160, false), PropertyField.House(0)),
      TrainStationField("Liverpool ST Station", 36, 200, None),
      ChanceField(37),
      darkBlueProperty1Owned,
      TaxField(200, 39),
      PropertyField("DarkBlue2", 40, 400, 50, None, color = PropertyField.Color.DarkBlue, PropertyField.Mortgage(200, false), PropertyField.House(0))
    )
  )

  val game = MonopolyGame(Vector(player1, player2), board, player1, false)
  val gameWithOwnedProperties = MonopolyGame(Vector(player1, player2), boardWithOwnedProperties, player1, false)

  "BoardPrinterSpec" should {
    "return valid String" in {
      val boardString = BoardPrinter.getBoardAsString(game)
      boardString should not be ("")
      boardString should include("ALL FIELDS:")
      boardString should include("A pos[12], balance[1500], isInJail[false]")
      boardString should include("B pos[1], balance[1500], isInJail[false]")
    }

    "return a Inventory of a Player without properties" in {
      val inv = BoardPrinter.getInventoryString(game)
      inv should be("INVENTORY Player: A| ")
    }

    "return a Inventory of a Player with properties" in {
      val inv = BoardPrinter.getInventoryString(gameWithOwnedProperties)
      inv should include("INVENTORY Player: A|")
    }

    "getName method" should {
      "return the name of a PropertyField" in {
        BoardPrinter.getName(brownProperty1) should be("Brown1")
      }

      "return the name of a TrainStationField" in {
        BoardPrinter.getName(trainStation) should be("Station1")
      }

      "return 'FreeParking' for a FreeParkingField" in {
        BoardPrinter.getName(freeParkingField) should be("FreeParking")
      }

      "return 'CommunityChest' for a CommunityChestField" in {
        BoardPrinter.getName(CommunityChestField(1)) should be("CommunityChest")
      }

      "return 'Chance' for a ChanceField" in {
        BoardPrinter.getName(ChanceField(1)) should be("")
      }

      "return an empty string for other BoardField types" in {
        BoardPrinter.getName(GoField) should be("")
        BoardPrinter.getName(JailField) should be("")
        BoardPrinter.getName(GoToJailField()) should be("")
        BoardPrinter.getName(taxField) should be("")
      }
    }

    "getStats method" should {
      "return player stats string" in {
        val statsStrings = BoardPrinter.getStats(game)
        statsStrings(0) should include("A pos[12], balance[1500], isInJail[false]")
        statsStrings(1) should include("B pos[1], balance[1500], isInJail[false]")
      }

      "handle stats string with different lengths" in {
        val longNamePlayer = player1.copy(name = "Very Long Player Name That Exceeds 20 Characters")
        val gameWithLongName = MonopolyGame(Vector(longNamePlayer, player2), board, longNamePlayer, false)
        val statsStrings = BoardPrinter.getStats(gameWithLongName)
        statsStrings(0) should include("Very Long Player Name")
      }
    }

    "getExtra method" should {
      "return owner name and house amount for owned property" in {
        val extra = BoardPrinter.getExtra(brownProperty1WithHouse)
        extra should be(" A1")
      }

      "return empty string for unowned property" in {
        val extra = BoardPrinter.getExtra(brownProperty1)
        extra should be("")
      }

      "return owner name for owned train station" in {
        val extra = BoardPrinter.getExtra(trainStationOwned)
        extra should be(" A")
      }

      "return empty string for unowned train station" in {
        val extra = BoardPrinter.getExtra(trainStation)
        extra should be("")
      }

      "return owner name for owned utility" in {
        val extra = BoardPrinter.getExtra(utilityOwned)
        extra should be(" A")
      }

      "return empty string for unowned utility" in {
        val extra = BoardPrinter.getExtra(utility)
        extra should be("")
      }

      "return empty string for non-ownable fields" in {
        val extra = BoardPrinter.getExtra(GoField)
        extra should be("")
      }
    }

    "formatField method" should {
      "format a field with index, name and price" in {
        val fieldString = BoardPrinter.formatField(Some(brownProperty1))
        fieldString should be("Index: 2, Brown1, Preis: 100$")
      }

      "return empty string for None" in {
        val fieldString = BoardPrinter.formatField(None)
        fieldString should be("")
      }
    }

    "fillSpace method" should {
      "pad string to specified length" in {
        val paddedString = BoardPrinter.fillSpace("test", 10)
        paddedString should be("test      ")
        paddedString.length should be(10)
      }

      "not change string if already at max length" in {
        val paddedString = BoardPrinter.fillSpace("1234", 4)
        paddedString should be("1234")
        paddedString.length should be(4)
      }
    }

    "getPrice method" should {
      "return price string for PropertyField" in {
        val priceString = BoardPrinter.getPrice(brownProperty1)
        priceString should be("100$")
      }

      "return price string for TrainStationField" in {
        val priceString = BoardPrinter.getPrice(trainStation)
        priceString should be("200$")
      }

      "return amount string for FreeParkingField" in {
        val priceString = BoardPrinter.getPrice(freeParkingField)
        priceString should be("50$")
      }

      "return price string for UtilityField" in {
        val priceString = BoardPrinter.getPrice(utility)
        priceString should be("150$")
      }

      "return empty string for other fields" in {
        val priceString = BoardPrinter.getPrice(GoField)
        priceString should be("")
      }
    }

    "playersOnIndex method" should {
      "return player marker for index with player" in {
        val playerString = BoardPrinter.playersOnIndex(game, 12, false)
        playerString should include("A")
        playerString should not include("B")
      }

      "return empty string for index without player" in {
        val playerString = BoardPrinter.playersOnIndex(game, 3, false)
        playerString should be("")
      }

      "handle multiple players on same index" in {
        val player3 = Player("C", 1500, 12, isInJail = false, 0)
        val gameWithMultiplePlayersOnSameIndex =
          MonopolyGame(Vector(player1, player2, player3), board, player1, false)
        val playerString = BoardPrinter.playersOnIndex(gameWithMultiplePlayersOnSameIndex, 12, false)
        playerString should include("A")
        playerString should include("C")
      }
    }
  }
}