package de.htwg.model

import de.htwg.model.PropertyField.*

sealed trait BoardField {
val name: String
val index: Int
}
case class PropertyField(name: String, index: Int, price: Int, rent: Int, owner: Option[String] = None,
                         color: PropertyField.Color, mortgage: PropertyField.Mortgage = PropertyField.Mortgage(),
                         house:PropertyField.House = PropertyField.House()) extends BoardField{ }
  object PropertyField {
    case class House(amount: Int = 0) {
      private val maxHouses = 5

      def calculateHousePrice(purchasePrice: Int): Int = {
        val baseHousePrice = purchasePrice / 2
        ((baseHousePrice + 9) / 10) * 10
      }

      def buyHouse(player: Player, field: PropertyField): (PropertyField, Player) = {
        if (player.balance < calculateHousePrice(field.price) || amount >= maxHouses) {
          (field, player)
        } else {
          val updatedHouse = this.copy(amount = amount + 1)
          (
            field.copy(house = updatedHouse),
            player.copy(balance = player.balance - calculateHousePrice(field.price))
          )
        }
      }
    }

    // Klasse für Mortgage
    case class Mortgage(price: Int = 0, active: Boolean = false) {
      def toggle(): Mortgage = copy(active = !active)
    }

    // Enum für Farben
    enum Color:
      case Brown, LightBlue, Pink, Orange, Red, Yellow, Green, DarkBlue
  }

case object GoField extends BoardField {
  override val index: Int = 1
  override val name: String = "GoField"
  /*def getGoBonus(player: Player): Int = {
    if()
  }

   */
}
case object JailField extends BoardField{
  override val index: Int = 11
  override val name: String = "Jail"
}
case class GoToJailField() extends BoardField{
  override val index: Int = 31
  override val name: String = "GoToJail"
  def goToJail(player: Player): Player = {
    player.goToJail()
  }
}
case class FreeParkingField(amount: Int) extends BoardField{
  override val index: Int = 21
  override val name: String = "FreeParking"

  def apply(player: Player): Player = {
    player.copy(balance = player.balance + amount)
  }

  def resetAmount(): FreeParkingField = {
    this.copy(amount = 0)
  }
}

case class ChanceField() extends BoardField {
  override val index: Int = 20
  override val name: String = "ChanceField"

}
case class CommunityChestField(communityCardList: List[Card]) extends BoardField{
  override val index: Int = 25
  override val name: String = "communityCard"
}