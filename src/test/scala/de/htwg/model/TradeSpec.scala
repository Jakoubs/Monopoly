package de.htwg.model

import de.htwg.Board
import de.htwg.model.modelBaseImple.{BuyableField, GoField, Player, Trade, PropertyField, CommunityChestField, TaxField, TrainStationField, ChanceField, JailField, UtilityField, FreeParkingField, GoToJailField}
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class TradeSpec extends AnyWordSpec {
  "Trade" should {
    val tradeList = List.empty[BuyableField] // Assuming tradeList is not used in this test
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

    "be able to trade when only p1 gets Money" in {
      val p1 = Player("TestPlayer1", 1500, 5, isInJail = false, 0)
      val p2 = Player("TestPlayer2", 200, 5, isInJail = false, 0)

      val tradeAmountP1ToP2 = 0
      val tradeAmountP2ToP1 = 200
      val result = Trade().tradeCall(p1, p2, tradeAmountP1ToP2, tradeAmountP2ToP1, tradeList, tradeList, board)

      result should not be empty
      val (newP1: Player, newP2: Player, boardnew: Board) = result.get
      newP1.balance should be(1700)
      newP2.balance should be(0)
    }

    "be able to trade when only p2 gets Money" in {
      val p1 = Player("TestPlayer1", 1500, 5, isInJail = false, 0)
      val p2 = Player("TestPlayer2", 200, 5, isInJail = false, 0)

      val tradeAmountP1ToP2 = 1500
      val tradeAmountP2ToP1 = 0

      val result = Trade().tradeCall(p1, p2, tradeAmountP1ToP2, tradeAmountP2ToP1, tradeList, tradeList, board)

      result should not be empty
      val (newP1: Player, newP2: Player, boardnew: Board) = result.get
      newP1.balance should be(0)
      newP2.balance should be(1700)
    }

    "deliver false, if the balance of p2 is lower than the trade offer" in {
      val p1 = Player("TestPlayer1", 1500, 5, isInJail = false, 0)
      val p2 = Player("TestPlayer2", 200, 5, isInJail = false, 0)

      val tradeAmountP1ToP2 = 0
      val tradeAmountP2ToP1 = 3000

      Trade().tradeCall(p1, p2, tradeAmountP1ToP2, tradeAmountP2ToP1, tradeList, tradeList, board) should be(None)
    }

    "deliver false, if the balance of p1 is lower than the trade offer" in {
      val p1 = Player("TestPlayer1", 1500, 5, isInJail = false, 0)
      val p2 = Player("TestPlayer2", 200, 5, isInJail = false, 0)

      val tradeAmountP1ToP2 = 2000
      val tradeAmountP2ToP1 = 0

      Trade().tradeCall(p1, p2, tradeAmountP1ToP2, tradeAmountP2ToP1, tradeList, tradeList, board) should be(None)
    }
/*
    "deliver false if every trade item is 0" in {
      val p1 = Player("TestPlayer1", 1500, 5, isInJail = false, 0)
      val p2 = Player("TestPlayer2", 200, 5, isInJail = false, 0)

      val tradeAmountP1ToP2 = 0
      val tradeAmountP2ToP1 = 0

      Trade().tradeCall(p1, p2, tradeAmountP1ToP2, tradeAmountP2ToP1, tradeList, tradeList, board) should be(None)
    }*/
  }
}
