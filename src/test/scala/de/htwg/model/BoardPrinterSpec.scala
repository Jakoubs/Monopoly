package de.htwg.model

import de.htwg.{Board, MonopolyGame}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import de.htwg.model.PropertyField.Color.{Brown, DarkBlue, Red}
import de.htwg.model.BoardPrinter


class BoardPrinterSpec extends AnyWordSpec {
  val player1 = Player("A", 1500, 12)
  val player2 = Player("B", 1500, 1)
  val brownProperty1 = PropertyField("Brown1", 2, 100, 10, None, Brown)
  val brownProperty1Owned = brownProperty1.copy(owner = Some(player1))
  val brownProperty1WithHouse = brownProperty1Owned.copy(house = PropertyField.House(1))
  val darkBlueProperty1 = PropertyField("DarkBlue1", 38, 200, 20, None, DarkBlue)
  val darkBlueProperty1Owned = darkBlueProperty1.copy(owner = Some(player1))
  val trainStation = TrainStationField("Station1", 6,200, None)
  val trainStationOwned = trainStation.copy(owner = Some(player1))
  val utility = UtilityField("Utility1", 13, 150, UtilityField.UtilityCheck.utility, None)
  val utilityOwned = utility.copy(owner = Some(player1))
  val goToJail = GoToJailField()
  val taxField = TaxField(100, 5)
  val freeParkingField = FreeParkingField(50)
  val board = Board(
    Vector(
      GoField,
      PropertyField("brown1", 2, 60, 2, None, color = PropertyField.Color.Brown, PropertyField.Mortgage(30, false), PropertyField.House(0)),
      CommunityChestField(3),
      PropertyField("brown2", 4, 60, 4, None, color = PropertyField.Color.Brown, PropertyField.Mortgage(30, false), PropertyField.House(0)),
      TaxField(100, 5),
      TrainStationField("Marklylebone Station", 6,200, None),
      PropertyField("lightBlue1", 7, 100, 6, None, color = PropertyField.Color.LightBlue, PropertyField.Mortgage(50, false), PropertyField.House(0)),
      ChanceField(8),
      PropertyField("lightBlue2", 9, 100, 6, None, color = PropertyField.Color.LightBlue, PropertyField.Mortgage(50, false), PropertyField.House(0)),
      PropertyField("lightBlue3", 10, 120, 8, None, color = PropertyField.Color.LightBlue, PropertyField.Mortgage(60, false), PropertyField.House(0)),
      JailField,
      PropertyField("Pink1", 12, 140, 10, None, color = PropertyField.Color.Pink, PropertyField.Mortgage(70, false), PropertyField.House(0)),
      UtilityField("Electric Company", 13,150, UtilityField.UtilityCheck.utility, None),
      PropertyField("Pink2", 14, 140, 10, None, color = PropertyField.Color.Pink, PropertyField.Mortgage(70, false), PropertyField.House(0)),
      PropertyField("Pink3", 15, 160, 12, None, color = PropertyField.Color.Pink, PropertyField.Mortgage(80, false), PropertyField.House(0)),
      TrainStationField("Fenchurch ST Station", 16,200, None),
      PropertyField("Orange1", 17, 180, 14, None, color = PropertyField.Color.Orange, PropertyField.Mortgage(90, false), PropertyField.House(0)),
      CommunityChestField(18),
      PropertyField("Orange2", 19, 180, 14, None, color = PropertyField.Color.Orange, PropertyField.Mortgage(90, false), PropertyField.House(0)),
      PropertyField("Orange3", 20, 200, 16, None, color = PropertyField.Color.Orange, PropertyField.Mortgage(100, false), PropertyField.House(0)),
      FreeParkingField(0),
      PropertyField("Red1", 22, 220, 18, None, color = PropertyField.Color.Red, PropertyField.Mortgage(110, false), PropertyField.House(0)),
      ChanceField(23),
      PropertyField("Red2", 24, 220, 18, None, color = PropertyField.Color.Red, PropertyField.Mortgage(110, false), PropertyField.House(0)),
      PropertyField("Red3", 25, 240, 20, None, color = PropertyField.Color.Red, PropertyField.Mortgage(120, false), PropertyField.House(0)),
      TrainStationField("King's Cross Station", 26,200, None),
      PropertyField("Yellow1", 27, 260, 22, None, color = PropertyField.Color.Yellow, PropertyField.Mortgage(130, false), PropertyField.House(0)),
      UtilityField("Water Works", 28,150, UtilityField.UtilityCheck.utility, None),
      PropertyField("Yellow2", 29, 260, 22, None, color = PropertyField.Color.Yellow, PropertyField.Mortgage(130, false), PropertyField.House(0)),
      PropertyField("Yellow3", 30, 280, 24, None, color = PropertyField.Color.Yellow, PropertyField.Mortgage(140, false), PropertyField.House(0)),
      GoToJailField(),
      PropertyField("Green1", 32, 300, 26, None, color = PropertyField.Color.Green, PropertyField.Mortgage(150, false), PropertyField.House(0)),
      PropertyField("Green2", 33, 300, 26, None, color = PropertyField.Color.Green, PropertyField.Mortgage(150, false), PropertyField.House(0)),
      CommunityChestField(34),
      PropertyField("Green3", 35, 320, 28, None, color = PropertyField.Color.Green, PropertyField.Mortgage(160, false), PropertyField.House(0)),
      TrainStationField("Liverpool ST Station", 36,200, None),
      ChanceField(37),
      PropertyField("DarkBlue1", 38, 350, 35, None, color = PropertyField.Color.DarkBlue, PropertyField.Mortgage(175, false), PropertyField.House(0)),
      TaxField(200, 39),
      PropertyField("DarkBlue2", 40, 400, 50, None, color = PropertyField.Color.DarkBlue, PropertyField.Mortgage(200, false), PropertyField.House(0))
    )
  )
  val game = MonopolyGame(Vector(player1, player2), board, player1, false)
    "BoardPrinterSpec" should {
      "return valid String" in {
        val boardString = BoardPrinter.getBoardAsString(game)
        boardString should be(
          "\n+-----------------+--------+--------+--------+--------+--------+--------+--------+--------+--------+-----------------+\n|  ______  _____  |Nr2     |Nr3     |Nr4     |Nr5     |Nr6     |Nr7     |Nr8     |Nr9     |Nr10    |                 |\n| |  ____ |     | |60$     |        |60$     |        |200$    |100$    |        |100$    |120$    |__________       |\n| |_____| |_____| |        |        |        |        |        |        |        |        |        |  JAIL    |      |                  ALL FIELDS:\n|          Ss.    |--------------------------------------------------------------------------------+          |      |                    Index: 1, GoField\n|  ssssssssSSSS   |  A pos[12], balance[1500], isInJail[false]                                     |          |      |                    Index: 2, brown1, Preis: 60$\n|          ;:`    |  B pos[1], balance[1500], isInJail[false]                                      |          |      |                    Index: 3, communityCard, Preis: \n|B                |                                                                                |          |      |                    Index: 4, brown2, Preis: 60$\n+--------+--------+                                                                                +--------+-+------+                    Index: 5, TaxField, Preis: \n|40      |                                                                                                  |12      |                    Index: 6, Marklylebone Station, Preis: 200$\n|400$    |                                                                                                  |140$    |                    Index: 7, lightBlue1, Preis: 100$\n|        |                                                                                                  |A       |                    Index: 8, ChanceField, Preis: \n+--------+                                                                                                  +--------+                    Index: 9, lightBlue2, Preis: 100$\n|39      |                                                                                                  |13      |                    Index: 10, lightBlue3, Preis: 120$\n|        |                                                                                                  |150$    |                    Index: 11, Jail, Preis: \n|        |                                                                                                  |        |                    Index: 12, Pink1, Preis: 140$\n+--------+                                                                                                  +--------+                    Index: 13, Electric Company, Preis: 150$\n|38      |                                                                                                  |14      |                    Index: 14, Pink2, Preis: 140$\n|350$    |                                                                                                  |140$    |                    Index: 15, Pink3, Preis: 160$\n|        |                                                                                                  |        |                    Index: 16, Fenchurch ST Station, Preis: 200$\n+--------+                                                                                                  +--------+                    Index: 17, Orange1, Preis: 180$\n|37      |                                                                                                  |15      |                    Index: 18, communityCard, Preis: \n|        |                                                                                                  |160$    |                    Index: 19, Orange2, Preis: 180$\n|        |                                                                                                  |        |                    Index: 20, Orange3, Preis: 200$\n+--------+                                                                                                  +--------+                    Index: 21, FreeParking, Preis: 0$\n|36      |                                                                                                  |16      |                    Index: 22, Red1, Preis: 220$\n|200$    |                                                                                                  |200$    |                    Index: 23, ChanceField, Preis: \n|        |                                                                                                  |        |                    Index: 24, Red2, Preis: 220$\n+--------+                                                                                                  +--------+                    Index: 25, Red3, Preis: 240$\n|35      |                                                                                                  |17      |                    Index: 26, King's Cross Station, Preis: 200$\n|320$    |                                                                                                  |180$    |                    Index: 27, Yellow1, Preis: 260$\n|        |                                                                                                  |        |                    Index: 28, Water Works, Preis: 150$\n+--------+                                                                                                  +--------+                    Index: 29, Yellow2, Preis: 260$\n|34      |                                                                                                  |18      |                    Index: 30, Yellow3, Preis: 280$\n|        |                                                                                                  |        |                    Index: 31, GoToJail, Preis: \n|        |                                                                                                  |        |                    Index: 32, Green1, Preis: 300$\n+--------+                                                                                                  +--------+                    Index: 33, Green2, Preis: 300$\n|33      |                                                                                                  |19      |                    Index: 34, communityCard, Preis: \n|300$    |                                                                                                  |180$    |                    Index: 35, Green3, Preis: 320$\n|        |                                                                                                  |        |                    Index: 36, Liverpool ST Station, Preis: 200$\n+--------+                                                                                                  +--------+                    Index: 37, ChanceField, Preis: \n|32      |                                                                                                  |20      |                    Index: 38, DarkBlue1, Preis: 350$\n|300$    |                                                                                                  |200$    |                    Index: 39, TaxField, Preis: \n|        |                                                                                                  |        |                    Index: 40, DarkBlue2, Preis: 400$\n+--------+--------+                                                                                +--------+--------+\n|   GO TO JAIL    |                                                                                |  FREE PARIKING  |\n|     ---->       |                                                                                |   ______        |\n|                 |                                                                                |  /|_||_`.__     |\n|                 +--------+--------+--------+--------+--------+--------+--------+--------+--------+ (   _    _ _\\   |\n|                 |30      |29      |28      |27      |26      |25      |24      |23      |22      | =`-(_)--(_)-`   |\n|                 |280$    |260$    |150$    |260$    |200$    |240$    |220$    |        |220$    |   Money [0$]    |\n|                 |        |        |        |        |        |        |        |        |        |                 |\n+-----------------+--------+--------+--------+--------+--------+--------+--------+--------+--------+-----------------+")
      }
      "return a Inventory of a Player" in {
        val inv = BoardPrinter.getInventoryString(game)
        inv should be("INVENTORY Player: A| ")
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

        "return an empty string for other BoardField types" in {
          BoardPrinter.getName(GoField) should be("")
          BoardPrinter.getName(JailField) should be("")
          BoardPrinter.getName(GoToJailField()) should be("")
          BoardPrinter.getName(taxField) should be("")
          BoardPrinter.getName(ChanceField(1)) should be("")
        }
      }
    }
}
