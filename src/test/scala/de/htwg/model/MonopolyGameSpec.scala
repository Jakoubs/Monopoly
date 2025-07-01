package de.htwg.model

import de.htwg.model.FileIOComponent.JSONFileIO.FileIO as JSONFileIO
import de.htwg.Board
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.controller.*
import de.htwg.controller.controllerBaseImpl.OpEnum.{buy, enter, fieldSelected, n, pay, roll, y}
import de.htwg.controller.controllerBaseImpl.{AdditionalActionsState, BuyHouseCommand, BuyHouseState, BuyPropertyState, ConfirmBuyHouseState, Controller, EndTurnState, JailState, JailTurnStrategy, MovingState, OpEnum, PropertyDecisionState, RollingState, StartTurnState}
import de.htwg.model.*
import de.htwg.model.modelBaseImple.PropertyField.Color.*
import de.htwg.model.modelBaseImple.{BoardField, ChanceField, CommunityChestField, Dice, FreeParkingField, GoField, GoToJailField, JailField, MonopolyGame, Player, PropertyField, TaxField, TrainStationField, UtilityField}
import de.htwg.util.util.Observable
import org.scalatest.matchers.should.Matchers.shouldBe

import scala.util.Try

class MonopolyGameSpec extends AnyWordSpec with Matchers {
  val dice = new Dice()
  val mockAsk: String => Boolean = _ => true // Simuliert immer "ja" als Antwort
  val mockPrint: String => Unit = _ => () // Tut nichts beim Drucken
  val mockChoice: () => Int = () => 1 // Gibt immer 1 zurück
  val player1: Player = Player("Player 1", 1500, 1, isInJail = false, 0)
  val player2: Player = Player("Player 2", 1500, 1, isInJail = false, 0)
  val fields: Vector[BoardField] = Vector(
    GoField,
    PropertyField("brown1", 2, 100, 10, None, color = Brown, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    CommunityChestField(3),
    PropertyField("brown2", 4, 100, 10, None, color = Brown, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    TaxField(100, 5),
    TrainStationField("Marklylebone Station", 6, 200, None),
    PropertyField("lightBlue1", 7, 100, 10, None, color = LightBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    ChanceField(8),
    PropertyField("lightBlue2", 9, 100, 10, None, color = LightBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    PropertyField("lightBlue3", 10, 100, 10, None, color = LightBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    JailField,
    PropertyField("Pink1", 12, 100, 10, None, color = Pink, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    UtilityField("Electric Company", 13, 150, UtilityField.UtilityCheck.utility, None),
    PropertyField("Pink2", 14, 100, 10, None, color = Pink, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    PropertyField("Pink3", 15, 100, 10, None, color = Pink, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    TrainStationField("Fenchurch ST Station", 16, 200, None),
    PropertyField("Orange1", 17, 100, 10, None, color = Orange, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    CommunityChestField(18),
    PropertyField("Orange2", 19, 100, 10, None, color = Orange, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    PropertyField("Orange3", 20, 100, 10, None, color = Orange, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    FreeParkingField(100),
    PropertyField("Red1", 22, 100, 10, Some(player1), color = Red, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    ChanceField(23),
    PropertyField("Red2", 24, 100, 10, Some(player1), color = Red, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    PropertyField("Red3", 25, 100, 10, Some(player1), color = Red, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    TrainStationField("King's Cross Station", 26, 200, None),
    PropertyField("Yellow1", 27, 100, 10, None, color = Yellow, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    UtilityField("Water Works", 28, 150, UtilityField.UtilityCheck.utility, None),
    PropertyField("Yellow2", 29, 100, 10, None, color = Yellow, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    PropertyField("Yellow3", 30, 100, 10, None, color = Yellow, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    GoToJailField(),
    PropertyField("Green1", 32, 100, 10, None, color = Green, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    PropertyField("Green2", 33, 100, 10, None, color = Green, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    CommunityChestField(34),
    PropertyField("Green3", 35, 100, 10, None, color = Green, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    TrainStationField("Liverpool ST Station", 36, 200, None),
    ChanceField(37),
    PropertyField("DarkBlue1", 38, 100, 10, None, color = DarkBlue, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    TaxField(200, 39),
    PropertyField("DarkBlue2", 40, 100, 10, None, color = DarkBlue, PropertyField.Mortgage(10, false), PropertyField.House(0))
  )
  val board: Board = Board(fields)
  val initialGame: MonopolyGame = MonopolyGame(Vector(player1, player2), board, player1, sound = false)
  val fileIO = new JSONFileIO
  val controller = new Controller(initialGame)(using fileIO)

  "MonopolyGame" should {

    "create a new game with the first player as currentPlayer" in {
      val g2 = initialGame.createGame
      g2.currentPlayer shouldBe player1
      g2.players shouldBe initialGame.players
      g2.board.fields shouldBe initialGame.board.fields
    }

    "update multiple players" in {
      val cindy = Player("Cindy", 1500, 1, isInJail = false, consecutiveDoubles = 0)
      val g2 = controller.game.withUpdatedPlayers(Vector(cindy))
      g2.players shouldBe Vector(cindy)
    }

    "end the turn of an player" in {
      val g2 = controller.game.endTurn()
      g2.currentPlayer shouldBe(player2)
    }

    "move players" in {
      val g2 = controller.game.movePlayer(10)
      g2.currentPlayer.position shouldBe(11)
    }
  }

}
