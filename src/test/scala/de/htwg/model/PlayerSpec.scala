package de.htwg.model

import de.htwg.Board
import de.htwg.controller.controllerBaseImpl.Controller
import de.htwg.model.FileIOComponent.JSONFileIO.FileIO as JSONFileIO
import de.htwg.model.modelBaseImple.{ChanceField, CommunityChestField, FreeParkingField, GoField, GoToJailField, JailField, MonopolyGame, Player, PropertyField, TaxField, TrainStationField, UtilityField}
import org.scalatest.matchers.should.Matchers.{shouldEqual, *}
import org.scalatest.wordspec.AnyWordSpec

class PlayerSpec extends AnyWordSpec {

  val player3: Player = Player("TestPlayer", 100, 11, true, 0)
  val board: Board = Board(
    Vector(
      GoField,
      PropertyField("brown1", 2, 60, 2, Some(player3), color = PropertyField.Color.Brown, PropertyField.Mortgage(30, false), PropertyField.House(0)),
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
  val game = MonopolyGame(Vector(player3), board, player3, false)
  val fileIO = new JSONFileIO
  val controller = new Controller(game)(using fileIO)


  "Player" should {
    "have a name, a balance, a position and a Jail status" in {
      val player = Player("TestPlayer", 1000, 5, false, 0)
      player.name should be("TestPlayer")
      player.balance should be(1000)
      player.position should be(5)
      player.isInJail should be(false)
      player.consecutiveDoubles should be(0)
    }

    "be able to change position index when not in jail" in {
      val player = Player("TestPlayer", 100, 5, false, 0)
      val updatedPlayer = player.moveToIndex(4)
      updatedPlayer.position shouldEqual 4
      updatedPlayer.isInJail should be(false)
    }

    "increase consecutiveDoubles when incrementDoubles" in {
      val player = Player("TestPlayer", 100, 5)
      val updatedPlayer = player.incrementDoubles
      updatedPlayer.consecutiveDoubles shouldEqual 1
    }

    "not be able to change index when in jail" in {
      val player = Player("TestPlayer", 100 ,5 ,true, 0)
      val updatedPlayer = player.moveToIndex(4)
      updatedPlayer.position shouldEqual 5
      updatedPlayer.isInJail should be(true)
    }

    "be released from Jail" in {
      val player = Player("TestPlayer", 100, 5, true, 0)
      val updatedPlayer = player.releaseFromJail
      updatedPlayer.isInJail should be(false)
    }

    "go to Jail" in {
      val player = Player("TestPlayer", 100, 11, true, 0)
      val updatedPlayer = player.goToJail
      updatedPlayer.position shouldEqual 11
      updatedPlayer.isInJail should be(true)
    }

    "use getProperties to return his Properties" in {
      val FieldList = player3.getProperties(controller.board.fields)
      FieldList shouldEqual Vector(PropertyField("brown1", 2, 60, 2, Some(player3), color = PropertyField.Color.Brown, PropertyField.Mortgage(30, false), PropertyField.House(0)))
    }
  }
}
