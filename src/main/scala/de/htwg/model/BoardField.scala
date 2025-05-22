package de.htwg.model
import de.htwg.MonopolyGame
import de.htwg.model.PropertyField.*
import scala.util.{Try, Success, Failure}

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
  def accept[T](visitor: FieldVisitor[T]): T
  val name: String
  val index: Int
}
case class PropertyField(name: String, index: Int, price: Int, rent: Int, owner: Option[Player] = None,
                         color: PropertyField.Color, mortgage: PropertyField.Mortgage = PropertyField.Mortgage(),
                         house:PropertyField.House = PropertyField.House()) extends BoardField with BuyableField
                         {
                           override def withNewOwner(player: Player): PropertyField = this.copy(owner = Some(player))
                           override def accept[T](visitor: FieldVisitor[T]): T = visitor.visit(this)
                         }
  object PropertyField {
    case class House(amount: Int = 0) {
      val maxHouses = 5
      def calculateHousePrice(purchasePrice: Int): Int = {
        val baseHousePrice = purchasePrice / 2
        ((baseHousePrice + 9) / 10) * 10
      }
      def buyHouse(player: Player, field: PropertyField, game: MonopolyGame): Try[(PropertyField, Player)] = Try {
        // Check 1: Does the player own the property?
        val ownsProperty = field.owner.exists(_.name == player.name)
        if (!ownsProperty) {
          throw new IllegalArgumentException(s"${player.name} does not own ${field.name}.")
        }

        val housePrice = calculateHousePrice(field.price)

        // Check 2: Does the player have enough money?
        if (player.balance < housePrice) {
          throw new IllegalArgumentException(s"${player.name} does not have enough money to buy a house on ${field.name}. Needed: $housePrice, Has: ${player.balance}.")
        }

        // Check 3: Can a house be built (maxHouses)?
        if (field.house.amount >= maxHouses) {
          throw new IllegalArgumentException(s"${field.name} already has the maximum number of houses ($maxHouses).")
        }

        // Check 4: Does the player own all properties in the color group?
        val colorGroupProperties = game.board.fields.collect {
          case pf: PropertyField if pf.color == field.color => pf
        }
        val ownsEntireColorGroup = colorGroupProperties.forall(_.owner.exists(_.name == player.name))

        if (!ownsEntireColorGroup) {
          throw new IllegalArgumentException(s"${player.name} must own all properties in the ${field.color} group to build houses on ${field.name}.")
        }

        // If all checks pass, proceed with the purchase
        val updatedField = field.copy(house = PropertyField.House(field.house.amount + 1))
        val updatedPlayer = player.copy(balance = player.balance - housePrice)
        (updatedField, updatedPlayer)
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

  override def accept[T](visitor: FieldVisitor[T]): T = visitor.visit(this)

}
case object JailField extends BoardField{
  override val index: Int = 11
  override val name: String = "Jail"
  override def accept[T](visitor: FieldVisitor[T]): T = visitor.visit(this)
}
case class GoToJailField() extends BoardField{
  override val index: Int = 31
  override val name: String = "GoToJail"
  def goToJail(player: Player): Player = {
    player.goToJail()
  }
  override def accept[T](visitor: FieldVisitor[T]): T = visitor.visit(this)
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

  override def accept[T](visitor: FieldVisitor[T]): T = visitor.visit(this)

}

case class ChanceField(idx: Int) extends BoardField {
  override val index: Int = idx
  override val name: String = "ChanceField"
  val CardList: List[Card] = List(
    MoveCard("Advance to Boardwalk", "",3, false), //Welcher Index?
    MoveCard("Advance to Go", "(Collect $200)",1, true),
    MoveCard("Advance to Illinois Avenue", "If you pass Go, collect $200", 4, true) // Welcher Index?
  )
  override def accept[T](visitor: FieldVisitor[T]): T = visitor.visit(this)
}
case class CommunityChestField(index: Int) extends BoardField{
  override val name: String = "communityCard"

  override def accept[T](visitor: FieldVisitor[T]): T = visitor.visit(this)

}
case class TaxField(amount: Int, index: Int) extends BoardField {
  override val name: String = "TaxField"

  override def accept[T](visitor: FieldVisitor[T]): T = visitor.visit(this)

}
case class TrainStationField(name: String ,index: Int, price: Int, owner: Option[Player]) extends BoardField with BuyableField
{
  override def accept[T](visitor: FieldVisitor[T]): T = visitor.visit(this)
  override def withNewOwner(player: Player): TrainStationField = this.copy(owner = Some(player))
}

case class UtilityField(name: String, index: Int, price: Int, utility: UtilityField.UtilityCheck,  owner: Option[Player]) extends BoardField with BuyableField
{
  override def withNewOwner(player: Player): UtilityField = this.copy(owner = Some(player))
  override def accept[T](visitor: FieldVisitor[T]): T = visitor.visit(this)
}
  object UtilityField {

    enum UtilityCheck:
      case utility
    
  }