package de.htwg.controller.controllerBaseImpl

import de.htwg.controller.controllerBaseImpl.*
import de.htwg.model.modelBaseImple.*
import de.htwg.model.IPlayer
import scala.util.{Failure, Success, Try}


trait Command {
  def execute(): Unit
  def undo(): Unit
  def redo(): Unit
}

case class BuyCommand[T <: BuyableField](
                                          field: T,
                                          player: IPlayer
                                        )(using controller: Controller) extends Command{

    private var previousState: Option[(T, IPlayer)] = None
    private var executedState: Option[(BoardField, IPlayer)] = None

    def execute(): Unit = {
      previousState = Some((field, player))
      val (updatedField, updatedPlayer) = field.buy(player)
      executedState = Some((updatedField, updatedPlayer))
      controller.updateBoardAndPlayer(updatedField, updatedPlayer)
    }

    def undo(): Unit = {
      previousState.foreach { case (f, p) =>
        controller.updateBoardAndPlayer(f, p)
      }
    }

    def redo(): Unit = {
      executedState.foreach { case (f, p) =>
        controller.updateBoardAndPlayer(f, p)
      }
    }
}

  case class BuyHouseCommand()(using controller: Controller, field: PropertyField, player: IPlayer) extends Command {
    private var previousState: Option[(PropertyField, IPlayer)] = None
    private var executedState: Option[(PropertyField, IPlayer)] = None

    def execute(): Unit = {
      previousState = Some((field, player))

      PropertyField.House().buyHouse(player, field, controller.game) match {
        case Success((updatedField: PropertyField, updatedPlayer: IPlayer)) =>
          controller.updateBoardAndPlayer(updatedField, updatedPlayer)
        case Failure(exception) =>
      }
    }

    def undo(): Unit = {
      previousState.foreach { case (f, p) =>
        controller.updateBoardAndPlayer(f, p)
      }
    }

    override def redo(): Unit = {
      executedState.foreach { case (f, p) =>
        controller.updateBoardAndPlayer(f, p)
      }
    }
  }

case class RollDiceCommand()(using controller: Controller) extends Command {
    private var previousPlayerState: Option[IPlayer] = None
    private var rollResult: (Int, Int) = (0, 0)
    private var executedPlayerState: Option[IPlayer] = None

    def execute(): Unit = {
      previousPlayerState = Some(controller.currentPlayer)
      rollResult = controller.game.rollDice(controller.sound)
    }

    def undo(): Unit = {
      previousPlayerState.foreach(controller.updatePlayer)
    }

    def getResult: (Int, Int) = rollResult

    def redo(): Unit = { executedPlayerState.foreach(controller.updatePlayer) }

}

case class PayJailFeeCommand()(using controller: Controller, player: IPlayer) extends Command {
  private var previousState: Option[IPlayer] = None
  private var executedState: Option[IPlayer] = None

  def execute(): Unit = {
      previousState = Some(player)
      val updatedPlayer = player.copyPlayer(
        isInJail = false,
        balance = player.balance - 50,
      )
        controller.updatePlayer(updatedPlayer)
    }

    def undo(): Unit = {
      previousState.foreach(controller.updatePlayer)
    }

  override def redo(): Unit = {
    executedState.foreach(controller.updatePlayer)
  }
}

