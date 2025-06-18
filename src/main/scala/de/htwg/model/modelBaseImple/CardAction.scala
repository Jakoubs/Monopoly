package de.htwg.model.modelBaseImple

import de.htwg.model.IPlayer
import de.htwg.model.modelBaseImple.{CardAction, Player}

sealed trait CardAction

case class GainMoney(amount: Int) extends CardAction {
  def apply(player: IPlayer): IPlayer = {
    player.copyPlayer(balance = player.balance + amount)
  }
}

case class LoseMoney(amount: Int) extends CardAction {
  def apply(player: IPlayer, freeParkingField: FreeParkingField): (IPlayer,FreeParkingField) = {
    val updatedPlayer = player.copyPlayer(balance = player.balance - amount)
    val updatedField = freeParkingField.copy(amount = freeParkingField.amount + amount)
    (updatedPlayer, updatedField)
  }
}

case object CardToJail extends CardAction {
  def apply(player: IPlayer): IPlayer = {
    player.goToJail
  }
}

case class CardMoveTo(index: Int, collectMoney: Boolean) extends CardAction {
  def apply(player: IPlayer): IPlayer = {
    val passedGo = player.position > index
    val movedPlayer = player.moveToIndex(index)
    if (collectMoney && passedGo)
      movedPlayer.copyPlayer(balance = movedPlayer.balance + 200)
    else
      movedPlayer
  }
}
