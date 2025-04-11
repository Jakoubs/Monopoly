package de.htwg.model

sealed trait CardAction

case class GainMoney(amount: Int) extends CardAction {
  def apply(player: Player): Player = {
    player.copy(balance = player.balance + amount)
  }
}

case class LoseMoney(amount: Int) extends CardAction {
  def apply(player: Player, freeParkingField: FreeParkingField): Player = {
    val updatedPlayer = player.copy(balance = player.balance - amount)
    val updatedField = freeParkingField.copy(amount = freeParkingField.amount + amount)
    updatedPlayer
  }
}

case object CardToJail extends CardAction {
  def apply(player: Player): Player = {
    player.goToJail()
  }
}

case class CardMoveTo(index: Int) extends CardAction {
  def apply(player: Player): Player = {
    player.moveToIndex(index)
  }
}