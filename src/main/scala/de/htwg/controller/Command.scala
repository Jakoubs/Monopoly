package de.htwg.controller

import de.htwg.model.{Player, PropertyField, TrainStationField, UtilityField}

trait Command {
  def execute(): Unit
  def undo(): Unit
}
  case class BuyTrainStationCommand(controller: Controller, field: TrainStationField, player: Player) extends Command {
    private var previousState: Option[(TrainStationField, Player)] = None

    def execute(): Unit = {
      previousState = Some((field, player))
      val (updatedField, updatedPlayer) = field.buyTrainstation(field, player)
      controller.updateBoardAndPlayer(updatedField, updatedPlayer)
    }

    def undo(): Unit = {
      previousState.foreach { case (f, p) =>
        controller.updateBoardAndPlayer(f, p)
      }
    }
  }

  case class BuyUtilityCommand(controller: Controller, field: UtilityField, player: Player) extends Command {
    private var previousState: Option[(UtilityField, Player)] = None

    def execute(): Unit = {
      previousState = Some((field, player))
      val (updatedField, updatedPlayer) = UtilityField.buyUtilityField(field, player)
      controller.updateBoardAndPlayer(updatedField, updatedPlayer)
    }

    def undo(): Unit = {
      previousState.foreach { case (f, p) =>
        controller.updateBoardAndPlayer(f, p)
      }
    }
  }

  case class BuyPropertyCommand(controller: Controller, field: PropertyField, player: Player) extends Command {
    private var previousState: Option[(PropertyField, Player)] = None

    def execute(): Unit = {
      previousState = Some((field, player))
      val (updatedField, updatedPlayer) = PropertyField.buyProperty(field, player)
      controller.updateBoardAndPlayer(updatedField, updatedPlayer)
    }

    def undo(): Unit = {
      previousState.foreach { case (f, p) =>
        controller.updateBoardAndPlayer(f, p)
      }
    }
  }

  case class BuyHouseCommand(controller: Controller, field: PropertyField, player: Player) extends Command {
    private var previousState: Option[(PropertyField, Player)] = None

    def execute(): Unit = {
      previousState = Some((field, player))
      val (updatedField, updatedPlayer) = PropertyField.House().buyHouse(player, field, controller.game)
      controller.updateBoardAndPlayer(updatedField, updatedPlayer)
    }

    def undo(): Unit = {
      previousState.foreach { case (f, p) =>
        controller.updateBoardAndPlayer(f, p)
      }
    }
  }

  case class RollDiceCommand(controller: Controller) extends Command {
    private var previousPlayerState: Option[Player] = None
    private var rollResult: (Int, Int) = (0, 0)

    def execute(): Unit = {
      previousPlayerState = Some(controller.currentPlayer)
      rollResult = controller.dice.rollDice(controller.sound)
    }

    def undo(): Unit = {
      previousPlayerState.foreach(controller.updatePlayer)
    }

    def getResult: (Int, Int) = rollResult
  }

  case class PayJailFeeCommand(controller: Controller, player: Player) extends Command {
    private var previousState: Option[Player] = None

    def execute(): Unit = {
      previousState = Some(player)
      val updatedPlayer = player.copy(
        isInJail = false,
        balance = player.balance - 50,
      )
        controller.updatePlayer(updatedPlayer)
    }

    def undo(): Unit = {
      previousState.foreach(controller.updatePlayer)
    }
  }

