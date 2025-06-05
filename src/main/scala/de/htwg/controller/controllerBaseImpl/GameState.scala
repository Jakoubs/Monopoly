package de.htwg.controller

import de.htwg.model.*
import de.htwg.controller.TurnInfo
import de.htwg.util.util.Observable
import de.htwg.controller.*
import de.htwg.controller.OpEnum
import de.htwg.controller.StartTurnState // Import des GameState-Traits
import de.htwg.controller.RollingState // Import des RollingState (falls verwendet)
import de.htwg.controller.JailState // Import des JailState (falls verwendet)
import de.htwg.controller.MovingState // Import des MovingState (falls verwendet)
import de.htwg.controller.PayJailHandler
import de.htwg.controller.RollDoublesJailHandler
import de.htwg.controller.InvalidJailInputHandler


sealed trait GameState {
  def handle(input: OpEnum, controller: Controller): GameState
}

case class StartTurnState() extends GameState {
  def handle(input: OpEnum, controller: Controller): GameState = {
    val player = controller.currentPlayer.resetDoubles()
    controller.updatePlayer(player)

    if (controller.currentPlayer.isInJail) {
      JailState()
    } else {
      RollingState()
    }
  }
}

case class JailState() extends GameState {
  override def handle(input: OpEnum, controller: Controller): GameState = {
    val payHandler = PayJailHandler(controller)
    val rollHandler = RollDoublesJailHandler(controller)
    val invalidHandler = InvalidJailInputHandler(controller)

    payHandler.setNext(rollHandler).setNext(invalidHandler)

    payHandler.handle(input).getOrElse(this)
  }
}

case class RollingState(isDouble: Boolean = false) extends GameState {
  def handle(input: OpEnum, controller: Controller): GameState = {
    val command = RollDiceCommand(controller)
    controller.executeCommand(command)
    val (d1, d2) = command.getResult
    val isDouble = d1 == d2
    

    // Update turn info
    controller.updateTurnInfo(
      TurnInfo(
        diceRoll1 = d1,
        diceRoll2 = d2
      )
    )

    if (isDouble) {
      val player = controller.currentPlayer
      val updatedPlayer = player.incrementDoubles()
      controller.updatePlayer(updatedPlayer)

      if (updatedPlayer.consecutiveDoubles >= 3) {
        val jailedPlayer = updatedPlayer.goToJail()
        controller.updatePlayer(jailedPlayer)
        EndTurnState()
      }
    } else {

      val updatedPlayer = controller.currentPlayer.resetDoubles()
      controller.updatePlayer(updatedPlayer)
    }

    MovingState(() => (d1, d2))
  }
}

case class MovingState(dice: () => (Int, Int)) extends GameState {
  def handle(input: OpEnum, controller: Controller): GameState = {
    val strategy = if (controller.currentPlayer.isInJail) {
      JailTurnStrategy()
    } else {
      RegularTurnStrategy()
    }

    val (d1, d2) = dice()
    val isDouble = d1 == d2

    val updatedPlayer = strategy.executeTurn(controller.currentPlayer,() => (d1, d2))
    controller.updatePlayer(updatedPlayer)


    val currentField = controller.board.fields(updatedPlayer.position - 1)
    controller.updateTurnInfo(controller.getTurnInfo.copy(landedField = Some(currentField)))
    val (die1, die2) = dice()
    val diceResult = die1 + die2
    val ownedProperties = controller.getOwnedProperties()
    val ownedTrainStations = controller.getOwnedTrainStations()
    val ownedUtilities = controller.getOwnedUtilities()

    currentField match {
      case buyableField: BuyableField =>
        buyableField.owner match {
          case Some(owner) if owner != updatedPlayer =>
            val rentVisitor = new RentVisitor(updatedPlayer, controller.players, controller.board, diceResult, ownedProperties, ownedTrainStations, ownedUtilities)
            val rent = currentField.accept(rentVisitor)

            if (updatedPlayer.balance >= rent) {
              val payingPlayer = updatedPlayer.copy(balance = updatedPlayer.balance - rent)
              val receivingPlayer = owner.copy(balance = owner.balance + rent)
              controller.updatePlayer(receivingPlayer)
              controller.updatePlayer(payingPlayer)
              controller.updateTurnInfo(controller.getTurnInfo.copy(paidRent = Some(rent), rentPaidTo = Some(owner)))
            } else {
              controller.isGameOver
            }
            AdditionalActionsState(isDouble)
          case _ => PropertyDecisionState()
        }
      case _: GoToJailField =>
        val jailedPlayer = updatedPlayer.goToJail()
        controller.updatePlayer(jailedPlayer)
        EndTurnState()
      case _: TaxField =>
        val rentVisitorTax = new RentVisitor(updatedPlayer, controller.players, controller.board, diceResult, ownedProperties, ownedTrainStations, ownedUtilities)
        val rent = currentField.accept(rentVisitorTax)
        if(updatedPlayer.balance >= rent) {
          val payingPlayer = updatedPlayer.copy(balance = updatedPlayer.balance - rent)
          val freeParking = controller.board.fields(20).asInstanceOf[FreeParkingField]
          val updatedFreeParking = freeParking.copy(amount = freeParking.amount + rent)
          controller.updateBoardAndPlayer(updatedFreeParking, payingPlayer)
        }else {
          controller.isGameOver
        }
        AdditionalActionsState(isDouble)
      case fp: FreeParkingField =>
        val freeParkingPlayer = fp.apply(updatedPlayer)
        val updatedFreeParkingField = fp.resetAmount()
        controller.updateBoardAndPlayer(updatedFreeParkingField, freeParkingPlayer)
        AdditionalActionsState(isDouble)
      case _ =>
        AdditionalActionsState(isDouble)
    }
  }
}


case class PropertyDecisionState(isDouble: Boolean = false) extends GameState {
  def handle(input: OpEnum, controller: Controller): GameState = {
    input match {
      case OpEnum.y => // ✅ Nur bei 'y' kaufen
        BuyPropertyState(isDouble)
      case OpEnum.n => // ✅ Bei 'n' nicht kaufen
        AdditionalActionsState(isDouble)
      case _ =>
        AdditionalActionsState(isDouble)
    }
  }
}


case class BuyPropertyState(isDouble: Boolean = false) extends GameState {
  def handle(input: OpEnum, controller: Controller): GameState = {
    val field = controller.board.fields(controller.currentPlayer.position-1)
    field match {
      case buyableField: BuyableField =>
        val command = BuyCommand(controller, buyableField, controller.currentPlayer)
        controller.executeCommand(command)
        AdditionalActionsState(isDouble)
      case _ =>
        AdditionalActionsState(isDouble)
    }
  }
}


case class AdditionalActionsState(isDouble: Boolean = false) extends GameState {
  def handle(input: OpEnum, controller: Controller): GameState = {
    input match {
      case OpEnum.buy =>
        BuyHouseState(isDouble)
      case _ =>
        if (isDouble) {
          RollingState()
        } else {
          EndTurnState()
        }
    }
  }
}

// State when buying a house
case class BuyHouseState(isDouble: Boolean = false) extends GameState {
  def handle(input: OpEnum, controller: Controller): GameState = {
    input match {
      case OpEnum.fieldSelected(fieldId) =>
          controller.game.board.fields(fieldId - 1) match {
            case field: PropertyField =>
              val command = BuyHouseCommand(controller, field, controller.currentPlayer)
              controller.executeCommand(command)
              ConfirmBuyHouseState(isDouble, command)
            case _ =>
              if (isDouble){
                RollingState()
              }else{
                EndTurnState()
              }
        }
      case _ =>
        this
    }
  }
}

case class ConfirmBuyHouseState(isDouble: Boolean = false, command: Command) extends GameState {
  def handle(input: OpEnum, controller: Controller): GameState = {
    input match {
      case  OpEnum.y =>
        command.undo()
        AdditionalActionsState(isDouble)
      case _ =>
        if (isDouble) {
          RollingState()
        } else {
          EndTurnState()
        }
    }
  }
}

case class EndTurnState() extends GameState {
  def handle(input: OpEnum, controller: Controller): GameState = {
    controller.switchToNextPlayer()
    StartTurnState()
  }
}


