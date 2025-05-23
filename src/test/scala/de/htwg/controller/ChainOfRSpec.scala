package de.htwg.controller

import de.htwg.MonopolyGame
import de.htwg.Board
import de.htwg.model.{BoardField, Player, Dice}
import de.htwg.controller.PayJailHandler
import de.htwg.controller.MovingState
import de.htwg.controller.JailState
import de.htwg.controller.RollingState
import de.htwg.controller.StartTurnState
import de.htwg.util.util.Observable
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ChainOfRSpec extends AnyWordSpec with Matchers {

  // Test Double für den Controller
  class TestController(initialGame: MonopolyGame, mockDice: Dice) extends Controller(initialGame, mockDice) {
    var payJailFeeCalled = false
    var updatePlayerCalledWith: Option[Player] = None

    override val dice: Dice = mockDice
    override def updatePlayer(player: Player): Unit = updatePlayerCalledWith = Some(player)
  }

  // Test Stub für den nächsten Handler
  class NextHandlerStub(expectedInput: OpEnum, nextState: Option[GameState]) extends ActionHandler {
    val controller: Controller = null // Nicht benötigt für Stub-Zweck
    var nextHandler: Option[ActionHandler] = None
    var handleCalledWith: Option[OpEnum] = None

    override def handle(input: OpEnum): Option[GameState] = {
      handleCalledWith = Some(input)
      if (input == expectedInput) nextState else None
    }
  }

  val player1 = Player("Test Player", 100, 0, isInJail = true, 0)
  val board =  Board(Vector.empty)
  val mockDiceNonDoubles = new Dice() {
    override def rollDice(sound: Boolean = false): (Int, Int) = (1, 2)
  }
  val mockDiceDoubles = new Dice() {
    override def rollDice(sound: Boolean = false): (Int, Int) = (3, 3)
  }
  val initialGame = MonopolyGame(Vector(player1), board, player1, false)

  "PayJailHandler" should {
    "handle 'pay' and call payJailFee on controller and return RollingState if player has enough balance" in {
      val controller = new TestController(initialGame, mockDiceNonDoubles)
      val handler = PayJailHandler(controller)
      val result = handler.handle(OpEnum.pay)
      controller.payJailFeeCalled should be(false)
      result shouldBe Some(RollingState())
    }

    "handle 'pay' and not call payJailFee and return JailState if player does not have enough balance" in {
      val controller = new TestController(initialGame.copy(players = Vector(player1.copy(balance = 20)), currentPlayer = player1.copy(balance = 20)), mockDiceNonDoubles)
      val handler = PayJailHandler(controller)
      val result = handler.handle(OpEnum.pay)
      controller.payJailFeeCalled should be(false)
      result shouldBe Some(JailState())
    }

    "not handle other input and call next handler" in {
      val controller = new TestController(initialGame, mockDiceNonDoubles)
      val nextHandlerStub = new NextHandlerStub(OpEnum.roll, Some(JailState()))
      val handler = PayJailHandler(controller, Some(nextHandlerStub))
      handler.handle(OpEnum.roll) shouldBe Some(JailState())
      nextHandlerStub.handleCalledWith shouldBe Some(OpEnum.roll)
    }
  }

  "RollDoublesJailHandler" should {
    "handle 'roll', call updatePlayer on controller and return MovingState if doubles are rolled" in {
      val controller = new TestController(initialGame, mockDiceDoubles)
      val handler = RollDoublesJailHandler(controller)
      val result = handler.handle(OpEnum.roll)
      controller.updatePlayerCalledWith.isDefined should be(true)
      controller.updatePlayerCalledWith.get.isInJail should be(false)
      result shouldBe a[Some[_]]
      result.get shouldBe a[MovingState]
    }

    "handle 'roll', call updatePlayer on controller and return JailState if no doubles are rolled" in {
      val controller = new TestController(initialGame, mockDiceNonDoubles)
      val handler = RollDoublesJailHandler(controller)
      val result = handler.handle(OpEnum.roll)
      controller.updatePlayerCalledWith.isDefined should be(true)
      controller.updatePlayerCalledWith.get.isInJail should be(true)
      result shouldBe Some(JailState())
    }

    "not handle other input and call next handler" in {
      val controller = new TestController(initialGame, mockDiceNonDoubles)
      val nextHandlerStub = new NextHandlerStub(OpEnum.pay, Some(JailState()))
      val handler = RollDoublesJailHandler(controller, Some(nextHandlerStub))
      handler.handle(OpEnum.pay) shouldBe Some(JailState())
      nextHandlerStub.handleCalledWith shouldBe Some(OpEnum.pay)
    }
  }

  "InvalidJailInputHandler" should {

    "not call next handler as it's the end of the chain" in {
      val controller = new TestController(initialGame, mockDiceNonDoubles)
      val nextHandlerStub = new NextHandlerStub(OpEnum.enter, None)
      val handler = InvalidJailInputHandler(controller, Some(nextHandlerStub))
      handler.handle(OpEnum.enter)
      nextHandlerStub.handleCalledWith shouldBe None
    }
  }
}