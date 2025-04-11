package de.htwg.model

sealed trait CardAction

case class GainMoney(amount: Int) extends CardAction {
  def apply(player: Player): Player = {
    player.copy(balance = player.balance + amount)
  }
}

case class LoseMoney(amount: Int) extends CardAction {
  def apply(player: Player, freeParkingField: FreeParkingField): (Player,FreeParkingField) = {
    val updatedPlayer = player.copy(balance = player.balance - amount)
    val updatedField = freeParkingField.copy(amount = freeParkingField.amount + amount)
    (updatedPlayer, updatedField)
  }
}

case object CardToJail extends CardAction {
  def apply(player: Player): Player = {
    player.goToJail()
  }
}

case class CardMoveTo(index: Int, collectMoney: Boolean) extends CardAction {
  def apply(player: Player): Player = {
    if(collectMoney)
      player.playerMove(rollDice = () =>((index-player.position)%40,0))
    else
      player.moveToIndex(index)

  }
}