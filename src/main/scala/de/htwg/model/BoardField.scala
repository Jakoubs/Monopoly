package de.htwg.model
import de.htwg.model.PropertyField.*

sealed trait BoardField {
val name: String
val index: Int
}
case class PropertyField(name: String, index: Int, price: Int, rent: Int, owner: Option[Player] = None,
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

    case class Mortgage(price: Int = 0, active: Boolean = false) {
      def toggle(): Mortgage = copy(active = !active)
    }

    enum Color:
      case Brown, LightBlue, Pink, Orange, Red, Yellow, Green, DarkBlue

    def calculateRent(property: PropertyField): Int = {
      val rent = property.rent
      val finalRent =  property.rent + property.house.amount * (rent / 2)
      finalRent
    }
  }

case object GoField extends BoardField {
  override val index: Int = 1
  override val name: String = "GoField"
  def addMoney(player: Player): Player = {
    player.copy(balance = player.balance + 200)
  }
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

case class ChanceField(idx: Int) extends BoardField {
  override val index: Int = idx
  override val name: String = "ChanceField"
  val CardList: List[Card] = List(
    MoveCard("Advance to Boardwalk", "",3, false), //Welcher Index?
    MoveCard("Advance to Go", "(Collect $200)",1, true),
    MoveCard("Advance to Illinois Avenue", "If you pass Go, collect $200", 4, true) // Welcher Index?
  )

}
case class CommunityChestField(idx: Int) extends BoardField{
  override val index: Int = idx
  override val name: String = "communityCard"
}
case class TaxField(amount: Int, idx: Int) extends BoardField {
  override val index: Int = idx
  override val name: String = "TaxField"
}
case class TrainStationField(nm: String ,idx: Int, owner: Option[Player]) extends BoardField {
  override val index: Int = idx
  override val name: String = nm
}
case class UtilityField(nm: String, idx: Int, owner: Option[Player]) extends BoardField{
  override val index: Int = idx
  override val name: String = nm
}