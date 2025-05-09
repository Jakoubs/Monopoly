import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.{Board, Monopoly, MonopolyGame}
import de.htwg.model._

class MonopolySpec extends AnyWordSpec {

  "Monopoly" should{
    "initialize a game with correct number of players" in {
      val game = MonopolyGame(
        Vector(Player("🐶", 1500, 1), Player("🐱", 1500, 1)),
        Board(Vector(GoField)),
        Player("🐶", 1500, 1),
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
      val existingPlayers = Vector(Player("🐶", 1500, 1))
      val emoji = Monopoly.randomEmoji(existingPlayers)
      emoji should not be "🐶"
    }

    "initialize board with correct fields" in {
      val game = MonopolyGame(
        Vector(Player("🐶", 1500, 1)),
        Board(Vector(GoField)),
        Player("🐶", 1500, 1),
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
