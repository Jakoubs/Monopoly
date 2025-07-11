package de.htwg.controller

import de.htwg.Board
import de.htwg.controller.controllerBaseImpl.OpEnum.*
import de.htwg.controller.controllerBaseImpl.{ActionHandler, Controller, GameState, InvalidJailInputHandler, JailState, MovingState, OpEnum, PayJailHandler, RollDoublesJailHandler, RollingState, StartTurnState}
import de.htwg.model.IPlayer
import de.htwg.model.modelBaseImple.{BoardField, Dice, MonopolyGame, Player}
import de.htwg.util.util.Observable
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.model.FileIOComponent.JSONFileIO.FileIO as JSONFileIO

class ChainOfRSpec extends AnyWordSpec with Matchers {

  val fileIO = new JSONFileIO

  // Test Double für den Controller
  class TestController(initialGame: MonopolyGame) extends Controller(initialGame)(using fileIO) {
    var payJailFeeCalled = false
    var updatePlayerCalledWith: Option[IPlayer] = None


    override def updatePlayer(player: IPlayer): Unit = updatePlayerCalledWith = Some(player)
  }

  // Test Stub für den nächsten Handler
  class NextHandlerStub(expectedInput: OpEnum, nextState: Option[GameState]) extends ActionHandler {
    val controller: Controller = null
    //var nextHandler: Option[ActionHandler] = None
    var handleCalledWith: Option[OpEnum] = None

    override def handle(input: OpEnum)(using Controller): Option[GameState] = {
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
    "not handle other input and call next handler" in {
      val controller = new TestController(initialGame)
      val nextHandlerStub = new NextHandlerStub(OpEnum.roll, Some(JailState()))
      val handler = PayJailHandler()
      handler.setNext(nextHandlerStub) // Handler verketten
      handler.handle(OpEnum.roll)(using controller) shouldBe Some(JailState())
      nextHandlerStub.handleCalledWith shouldBe Some(OpEnum.roll)
    }
  }

  "RollDoublesJailHandler" should {
    "not handle other input and call next handler" in {
      val controller = new TestController(initialGame)
      val nextHandlerStub = new NextHandlerStub(pay, Some(JailState()))
      val handler = RollDoublesJailHandler()
      handler.setNext(nextHandlerStub) // Handler verketten
      handler.handle(pay)(using controller) shouldBe Some(JailState())
      nextHandlerStub.handleCalledWith shouldBe Some(pay)
    }
  }

  "InvalidJailInputHandler" should {
    "not call next handler as it's the end of the chain" in {
      val controller = new TestController(initialGame)
      val nextHandlerStub = new NextHandlerStub(enter, None)
      val handler = InvalidJailInputHandler()
      handler.setNext(nextHandlerStub) // Handler verketten
      handler.handle(n)(using controller)
      nextHandlerStub.handleCalledWith shouldBe None
    }
  }

  "RollDoublesJailHandler" should {
    "handle 'roll', call updatePlayer on controller and return MovingState if doubles are rolled" in {
      val controller = new TestController(initialGame)
      val handler = RollDoublesJailHandler()
      val result = handler.handle(roll)(using controller)
      controller.updatePlayerCalledWith.isDefined should be(true)
      controller.updatePlayerCalledWith.get.isInJail should be(true)
      result shouldBe a[Some[_]]
    }

    "handle 'roll', call updatePlayer on controller and return JailState if no doubles are rolled" in {
      val controller = new TestController(initialGame)
      val handler = RollDoublesJailHandler()
      val result = handler.handle(roll)(using controller)
      controller.updatePlayerCalledWith.isDefined should be(true)
      controller.updatePlayerCalledWith.get.isInJail should be(true)
      result shouldBe Some(JailState())
    }

    "not handle an other input and call next handler" in {
      val controller = new TestController(initialGame)
      val nextHandlerStub = new NextHandlerStub(pay, Some(JailState()))
      val handler = RollDoublesJailHandler()
      handler.setNext(nextHandlerStub) // Handler verketten
      handler.handle(pay)(using controller) shouldBe Some(JailState())
      nextHandlerStub.handleCalledWith shouldBe Some(pay)
    }
  }

  "InvalidJailInputHandler" should {
    "handle any input and return JailState" in {
      val controller = new TestController(initialGame)
      val handler = InvalidJailInputHandler()
      handler.handle(n)(using controller) shouldBe Some(JailState())
      handler.handle(n)(using controller) shouldBe Some(JailState())
      handler.handle(enter)(using controller) shouldBe Some(JailState())
    }

    "not call an next handler as it's the end of the chain" in {
      val controller = new TestController(initialGame)
      val nextHandlerStub = new NextHandlerStub(enter, None)
      val handler = InvalidJailInputHandler()
      handler.handle(n)(using controller)
      nextHandlerStub.handleCalledWith shouldBe None // Da der InvalidHandler immer einen Zustand zurückgibt
    }
  }
}