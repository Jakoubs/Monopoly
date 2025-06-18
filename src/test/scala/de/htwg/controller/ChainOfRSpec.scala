package de.htwg.controller

import de.htwg.MonopolyGame
import de.htwg.Board
import de.htwg.controller.controllerBaseImpl.OpEnum.*
import de.htwg.controller.controllerBaseImpl.{ActionHandler, Controller, GameState, InvalidJailInputHandler, JailState, MovingState, OpEnum, PayJailHandler, RollDoublesJailHandler, RollingState, StartTurnState}
import de.htwg.model.modelBaseImple.{BoardField, Dice, Player}
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
      if (input.equals(expectedInput)) nextState else None
    }
  }

  val player1 = Player("Test Player", 100, 0, isInJail = true, 0)
  val board = Board(Vector.empty)
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
      val result = handler.handle(pay)
      controller.payJailFeeCalled should be(false)
      result shouldBe Some(RollingState())
    }

    "handle 'pay' and not call payJailFee and return JailState if player does not have enough balance" in {
      val controller = new TestController(initialGame.copy(players = Vector(player1.copy(balance = 20)), currentPlayer = player1.copy(balance = 20)), mockDiceNonDoubles)
      val handler = PayJailHandler(controller)
      val result = handler.handle(pay)
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
      val result = handler.handle(roll)
      controller.updatePlayerCalledWith.isDefined should be(true)
      controller.updatePlayerCalledWith.get.isInJail should be(false)
      result shouldBe a[Some[_]]
      result.get shouldBe a[MovingState]
    }

    "handle 'roll', call updatePlayer on controller and return JailState if no doubles are rolled" in {
      val controller = new TestController(initialGame, mockDiceNonDoubles)
      val handler = RollDoublesJailHandler(controller)
      val result = handler.handle(roll)
      controller.updatePlayerCalledWith.isDefined should be(true)
      controller.updatePlayerCalledWith.get.isInJail should be(true)
      result shouldBe Some(JailState())
    }

    "not handle other input and call next handler" in {
      val controller = new TestController(initialGame, mockDiceNonDoubles)
      val nextHandlerStub = new NextHandlerStub(pay, Some(JailState()))
      val handler = RollDoublesJailHandler(controller, Some(nextHandlerStub))
      handler.handle(pay) shouldBe Some(JailState())
      nextHandlerStub.handleCalledWith shouldBe Some(pay)
    }
  }

  "InvalidJailInputHandler" should {
    "handle any input and return JailState" in {
      val controller = new TestController(initialGame, mockDiceNonDoubles)
      val handler = InvalidJailInputHandler(controller)
      handler.handle(n) shouldBe Some(JailState())
      handler.handle(n) shouldBe Some(JailState())
      handler.handle(enter) shouldBe Some(JailState())
    }

    "not call next handler as it's the end of the chain" in {
      val controller = new TestController(initialGame, mockDiceNonDoubles)
      val nextHandlerStub = new NextHandlerStub(enter, None)
      val handler = InvalidJailInputHandler(controller, Some(nextHandlerStub))
      handler.handle(n)
      nextHandlerStub.handleCalledWith shouldBe None // Da der InvalidHandler immer einen Zustand zurückgibt
    }
  }
}