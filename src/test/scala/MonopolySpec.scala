import de.htwg.controller.controllerBaseImpl.Controller
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.{Board, Monopoly}

import de.htwg.model.{IPlayer, IMonopolyGame}
import de.htwg.model.*
import de.htwg.model.modelBaseImple.PropertyField.Color.*
import de.htwg.model.modelBaseImple.{MonopolyGame,ChanceField, CommunityChestField, Dice, FreeParkingField, GoField, GoToJailField, JailField, Player, PropertyField, TaxField, TrainStationField, UtilityField}
import de.htwg.view.Tui

class MonopolySpec extends AnyWordSpec {

  val dice = new Dice()
  val player1 = Player("Player 1", 1500, 1, isInJail = false, 0)
  val player2 = Player("Player 2", 1500, 1, isInJail = false, 0)
  val fields = Vector(
    GoField,
    PropertyField("brown1", 2, 100, 10, None, color = Brown, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    CommunityChestField(3),
    PropertyField("brown2", 4, 100, 10, None, color = Brown, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    TaxField(100, 5),
    TrainStationField("Marklylebone Station", 6, 200, Some(player1)),
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
    FreeParkingField(0),
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

  val board = Board(fields)
  val initialGame = MonopolyGame(Vector(player1, player2), board, player1, sound = false)
  val controller = new Controller(initialGame)

  "Monopoly" should{
    "initialize a game with correct number of players" in {
      val game = MonopolyGame(
        Vector(Player("🐶", 1500, 1,false,0), Player("🐱", 1500, 1,false,0)),
        Board(Vector(GoField)),
        Player("🐶", 1500, 1,false,0),
        false
      )
      game.players.size should be(2)
      game.players.head.balance should be(1500)
    }
    "generate unique emojis for players" in {
      val existingPlayers = Vector[Player]()
      val emoji = Monopoly.randomEmoji(existingPlayers)
      emoji should not be empty
      emoji.length should be(2) // Emoji characters are 2 units long
    }

    "not generate duplicate emojis" in {
      val existingPlayers = Vector(Player("🐶", 1500, 1, false, 0))
      val emoji = Monopoly.randomEmoji(existingPlayers)
      emoji should not be "🐶"
    }

    "initialize board with correct fields" in {
      val game = MonopolyGame(
        Vector(Player("🐶", 1500, 1, false, 0)),
        Board(Vector(GoField)),
        Player("🐶", 1500, 1, false, 0),
        false
      )
      game.board.fields.size should be >= 1
      game.board.fields.head should be(GoField)
    }
    "define well" in {
        val game = Monopoly.defineGame()
        game.board.fields.size should be(40)
    }



  }
}
