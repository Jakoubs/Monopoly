package de.htwg.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.model.*
import de.htwg.model.PropertyField.Color.*
import de.htwg.{Board, MonopolyGame}

class ControllerSpec extends AnyWordSpec with Matchers {

  val player1 = Player("Player 1", 1500, 1, isInJail = false, 0)
  val player2 = Player("Player 2", 1500, 1, isInJail = false, 0)

  val fields = Vector(
    GoField,
    PropertyField("brown1", 2, 100, 10, None, color = Brown, PropertyField.Mortgage(10, false), PropertyField.House(0)),
    JailField
  )

  val board = Board(fields)
  val dice = new Dice {
    override def rollDice(withSound: Boolean): (Int, Int) = (3, 4)
  }

  val initialGame = MonopolyGame(Vector(player1, player2), board, player1, sound = false)
  val controller = new Controller(initialGame, dice)

  "A Controller" should {

    "update the player correctly" in {
      val updatedPlayer = player1.copy(balance = 1200)
      controller.updatePlayer(updatedPlayer)
      controller.currentPlayer.balance shouldBe 1200
    }

    "return the list of players" in {
      controller.players shouldBe controller.game.players
    }


    "return the current TurnInfo via getTurnInfo" in {
      val initialInfo = controller.getTurnInfo
      initialInfo shouldBe TurnInfo()
    }

    "update state when handleInput is called" in {
      val initialState = controller.state
      controller.handleInput("some input")
      controller.state should not be initialState
    }

    "notify observers when handleInput is called" in {
      var notified = false
      controller.add(() => notified = true)
      controller.handleInput("any")
      notified shouldBe true
    }

    "update board and player correctly" in {
      val updatedField = PropertyField("brown1", 2, 100, 10, Some(player1), color = Brown, PropertyField.Mortgage(10, false), PropertyField.House(0))
      val updatedPlayer = player1.copy(balance = 1300)
      controller.updateBoardAndPlayer(updatedField, updatedPlayer)

      controller.board.fields(1) match {
        case pf: PropertyField => pf.owner shouldBe Some(player1)
        case _ => fail("Field should be a PropertyField")
      }

      controller.currentPlayer.balance shouldBe 1300
    }

    "switch to next player" in {
      controller.switchToNextPlayer()
      controller.currentPlayer.name shouldBe "Player 2"
    }

    "detect game over if only one player has money" in {
      val brokePlayer = player1.copy(balance = 0)
      val richPlayer = player2.copy(balance = 500)
      val gameOverController = new Controller(MonopolyGame(Vector(brokePlayer, richPlayer), board, richPlayer, sound = false), dice)
      gameOverController.isGameOver shouldBe true
    }

    "return correct player status string" in {
      val player = player1.copy(isInJail = false)
      val game = initialGame.copy(players = Vector(player), currentPlayer = player)
      val testController = new Controller(game, dice)
      val status = testController.getCurrentPlayerStatus
      status should include("Player 1")
      status should include("Balance")
      status should include("Position")
      status should include("Jail")
      status should include("In Jail")
    }

    "return correct player status string when in Jail" in {
      val player = player1.copy(isInJail = true)
      val game = initialGame.copy(players = Vector(player), currentPlayer = player)
      val testController = new Controller(game, dice)
      val status = testController.getCurrentPlayerStatus
      status should include("Player 1")
      status should include("Balance")
      status should include("Position")
      status should include("Jail")
      status should include("In Jail")
    }

    "return non-empty string from getBoardString" in {
      val boardString = controller.getBoardString
      boardString shouldBe a[String]
      boardString should not be empty
    }

  }
}
