package de.htwg.model

import de.htwg.{Board, MonopolyGame}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import de.htwg.model.PropertyField.Color.{Brown, DarkBlue, Red}
import de.htwg.view.BoardPrinter


class BoardPrinterSpec extends AnyWordSpec {
  val player1 = Player("A", 1500, 12)
  val player2 = Player("B", 1500, 1)
  val brownProperty1 = PropertyField("Brown1", 2, 100, 10, None, Brown)
  val brownProperty1Owned = brownProperty1.copy(owner = Some(player1))
  val brownProperty1WithHouse = brownProperty1Owned.copy(house = PropertyField.House(1))
  val darkBlueProperty1 = PropertyField("DarkBlue1", 38, 200, 20, None, DarkBlue)
  val darkBlueProperty1Owned = darkBlueProperty1.copy(owner = Some(player1))
  val trainStation = TrainStationField("Station1", 6, None)
  val trainStationOwned = trainStation.copy(owner = Some(player1))
  val utility = UtilityField("Utility1", 13, None)
  val utilityOwned = utility.copy(owner = Some(player1))
  val goToJail = GoToJailField()
  val taxField = TaxField(100, 5)
  val freeParkingField = FreeParkingField(50)
  val board = Board(Vector(GoField, brownProperty1, CommunityChestField(3), brownProperty1, taxField, trainStation,
    PropertyField("LightBlue1", 7, 120, 12, None, PropertyField.Color.LightBlue), ChanceField(8),
    PropertyField("LightBlue2", 9, 120, 12, None, PropertyField.Color.LightBlue),
    PropertyField("LightBlue3", 10, 120, 12, None, PropertyField.Color.LightBlue), JailField,
    PropertyField("Pink1", 12, 140, 14, Some(player1), PropertyField.Color.Pink), utility,
    PropertyField("Pink2", 14, 140, 14, Some(player1), PropertyField.Color.Pink),
    PropertyField("Pink3", 15, 140, 14, Some(player1), PropertyField.Color.Pink),
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
    "BoardPrinterSpec" should {
      "return valid String" in {
        val boardString = BoardPrinter.printBoard(game)
        boardString should be("+-----------------+--------+--------+--------+--------+--------+--------+--------+--------+--------+-----------------+\n" +
          "|  ______  _____  |Nr2     |Nr3     |Nr2     |Nr5     |Nr6     |Nr7     |Nr8     |Nr9     |Nr10    |Nr5     |                 |\n" +
          "| |  ____ |     | |100$    |        |100$    |        |200$    |120$    |        |120$    |120$    |        |__________       |\n" +
          "| |_____| |_____| |        |        |        |        |        |        |        |        |        |        |  JAIL    |      |                  ALL FIELDS:\n" +

          "|          Ss.    |--------+--------+--------+--------+--------+--------+--------+--------+--------+          |      |                    Index: 2, GoField\n|  ssssssssSSSS   |  A pos[12], balance[1500], isInJail[false]                                     |          |      |                    Index: 1, GoField, Preis: \n|          ;:`    |  B pos[1], balance[1500], isInJail[false]                                      |          |      |                    Index: 2, Brown1, Preis: 100$\n|B                |                                                                                |          |      |                    Index: 3, communityCard, Preis: \n+--------+--------+                                                                                +--------+-+------+                    Index: 2, Brown1, Preis: 100$\n()\n()\n|38      |                                                                                                  |14 A0   |                    Index: 14, Pink2, Preis: 140$\n|200$    |" +
          "                                                                                                  |140$    |                    Index: 15, Pink3, Preis: 140$\n|        |                                                                                                  |        |                    Index: 16, Station2, Preis: 200$\n+--------+                                                                                                  +--------+                    Index: 17, Orange1, Preis: 160$\n|37      |                                                                                                  |15 A0   |                    Index: 18, communityCard, Preis: \n|        |                                                                                                  |140$    |                    Index: 19, Orange2, Preis: 160$\n|        |                                                                                                  |        |                    Index: 20, Orange3, Preis: 160$\n+--------+                                                                                                  +--------+                    Index: 21, FreeParking, Preis: 50$\n|36      |                                                                                                  |16      |                    Index: 22, Red1, Preis: 180$\n|200$    |                                                                                                  |200$    |                    Index: 23, ChanceField, Preis: \n|        |                                                                                                  |        |                    Index: 24, Red2, Preis: 180$\n+--------+                                                                                                  +--------+                    Index: 25, Red3, Preis: 180$\n|35      |                                                                                                  |17      |                    Index: 26, Station3, Preis: 200$\n|220$    |                                                                                                  |160$    |                    Index: 27, Yellow1, Preis: 200$\n|        |                                                                                                  |        |                    Index: 28, Yellow2, Preis: 200$\n+--------+                                                                                                  +--------+                    Index: 29, Utility2, Preis: \n|34      |                                                                                                  |18      |                    Index: 30, Yellow3, Preis: 200$\n|        |                                                                                                  |        |                    Index: 31, GoToJail, Preis: \n|        |                                                                                                  |        |                    Index: 32, Green1, Preis: 220$\n+--------+                                                                                                  +--------+                    Index: 33, Green2, Preis: 220$\n|33      |                                                                                                  |19      |                    Index: 34, ChanceField, Preis: \n|220$    |                                                                                                  |160$    |                    Index: 35, Green3, Preis: 220$\n|        |                                                                                                  |        |                    Index: 36, Station4, Preis: 200$\n+--------+                                                                                                  +--------+                    Index: 37, ChanceField, Preis: \n|32      |                                                                                                  |20      |                    Index: 38, DarkBlue1, Preis: 200$\n|220$    |                                                                                                  |160$    |                    Index: 5, TaxField, Preis: \n|        |                                                                                                  |        |                    Index: 38, DarkBlue1, Preis: 200$\n+--------+--------+                                                                                +--------+--------+\n|   GO TO JAIL    |                                                                                |  FREE PARIKING  |\n|     ---->       |                                                                                |   ______        |\n|                 |                                                                                |  /|_||_`.__     |\n|                 +--------+--------+--------+--------+--------+--------+--------+--------+--------+ (   _    _ _\\   |\n|                 |30      |29      |28      |27      |26      |25      |24      |23      |22      | =`-(_)--(_)-`   |\n|                 |200$    |        |200$    |200$    |200$    |180$    |180$    |        |180$    |   Money [50$]    |\n|                 |        |        |        |        |        |        |        |        |        |                 |\n+-----------------+--------+--------+--------+--------+--------+--------+--------+--------+--------+-----------------+\n")
      }
      "return a Inventory of a Player" in {
        val inv = BoardPrinter.getInventory(game)
        println(inv)
        inv should be("INVENTORY Player: A| ")
      }
      }
}
