package de.htwg.model

import de.htwg.model.modelBaseImple.{FreeParkingField, Player}

trait CardInterface:

  type Card
  type CardAction = (Player, FreeParkingField) => (Player, FreeParkingField)

  trait CardFactory:
    def gainMoney(amount: Int): Card
    def loseMoney(amount: Int): Card
    def goToJail: Card
    def moveTo(index: Int, collectMoney: Boolean): Card

  val cardFactory: CardFactory

  extension (c: Card) def action: CardAction
