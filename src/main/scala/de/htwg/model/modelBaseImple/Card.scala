package de.htwg.model.modelBaseImple

import de.htwg.model.*

sealed trait Card {
  def name: String
  def description: String
  def action: CardAction
}

case class MoneyCard(name: String, description: String, amount: Int) extends Card {
  override def action: CardAction = GainMoney(amount)
}

case class MoveCard(name: String, description: String, index: Int, collectMoney: Boolean) extends Card {
    override def action: CardAction = CardMoveTo(index, collectMoney)

}

case class PenaltyCard(name: String, description: String, amount: Int) extends Card {
  override def action: CardAction = LoseMoney(amount)
}

case class JailCard(name: String, description: String) extends Card {
  override def action: CardAction = CardToJail
}


