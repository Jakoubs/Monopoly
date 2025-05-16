package de.htwg.model
import de.htwg.MonopolyGame
import de.htwg.model.PropertyField.*

trait BuyableField extends BoardField {
  val price: Int
  val owner: Option[Player]

  def withNewOwner(player: Player): BoardField

  def buy(player: Player): (BoardField, Player) = {
    owner match {
      case None if player.balance > price =>
        val updatedField = withNewOwner(player)
        val updatedPlayer = player.copy(balance = player.balance - price)
        (updatedField, updatedPlayer)
      case _ =>
        (this, player)
    }
  }
}

trait BoardField {
val name: String
val index: Int
}
case class PropertyField(name: String, index: Int, price: Int, rent: Int, owner: Option[Player] = None,
                         color: PropertyField.Color, mortgage: PropertyField.Mortgage = PropertyField.Mortgage(),
                         house:PropertyField.House = PropertyField.House()) extends BoardField with BuyableField
                         {
                           override def withNewOwner(player: Player): PropertyField = this.copy(owner = Some(player))
                         }
  object PropertyField {
    case class House(amount: Int = 0) {
      val maxHouses = 5
      def calculateHousePrice(purchasePrice: Int): Int = {
        val baseHousePrice = purchasePrice / 2
        ((baseHousePrice + 9) / 10) * 10
      }
      def buyHouse(player: Player, field: PropertyField, game: MonopolyGame): (PropertyField, Player) = {
        if (player.balance < calculateHousePrice(field.price) || amount >= maxHouses) {
          (field, player)
        } else {
          if (field.owner.exists(_.name == player.name)) {
            val colorGroup = game.board.fields.collect {
              case pf: PropertyField if pf.color == field.color => pf
            }
            val ownsAll = colorGroup.forall(_.owner.exists(_.name == player.name))

              if (ownsAll && field.house.amount < maxHouses) {
                val updatedField = field.copy(house = PropertyField.House(field.house.amount + 1))
                val updatedPlayer = player.copy(balance = player.balance - calculateHousePrice(field.price))
                (updatedField,updatedPlayer)
              } else {
              (field, player)
              }
          }else {
            (field, player)
          }
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
      val finalRent = rent + property.house.amount * Math.ceil(rent.toDouble / 2).toInt
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
case class CommunityChestField(index: Int) extends BoardField{
  override val name: String = "communityCard"
}
case class TaxField(amount: Int, index: Int) extends BoardField {
  override val name: String = "TaxField"
}
case class TrainStationField(name: String ,index: Int, price: Int, owner: Option[Player]) extends BoardField with BuyableField
{
  override def withNewOwner(player: Player): TrainStationField = this.copy(owner = Some(player))
}

case class UtilityField(name: String, index: Int, price: Int, utility: UtilityField.UtilityCheck,  owner: Option[Player]) extends BoardField with BuyableField
{
  override def withNewOwner(player: Player): UtilityField = this.copy(owner = Some(player))
}
  object UtilityField {

    enum UtilityCheck:
      case utility
    
  }