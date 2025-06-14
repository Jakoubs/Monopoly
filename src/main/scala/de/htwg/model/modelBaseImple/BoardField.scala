package de.htwg.model.modelBaseImple

  import de.htwg.model.modelBaseImple.PropertyField.*
  import de.htwg.model.*
  import de.htwg.model.modelBaseImple.FieldVisitor

  import scala.util.{Failure, Success, Try}

trait BuyableField extends BoardField {
  val price: Int
  val owner: Option[IPlayer]

  def withNewOwner(player: IPlayer): BoardField

  def buy(player: IPlayer): (BoardField, IPlayer) = {
    owner match {
      case None if player.balance > price =>
        val updatedField = withNewOwner(player)
        val updatedPlayer = player.changeBalance(-price).getOrElse(player)
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

case class PropertyField(name: String, index: Int, price: Int, rent: Int, owner: Option[IPlayer] = None,
                         color: PropertyField.Color, mortgage: PropertyField.Mortgage = PropertyField.Mortgage(),
                         house: PropertyField.House = PropertyField.House()) extends BoardField with BuyableField {
  override def withNewOwner(player: IPlayer): PropertyField = this.copy(owner = Some(player))
  override def accept[T](visitor: FieldVisitor[T]): T = visitor.visit(this)
}

object PropertyField {
  case class House(amount: Int = 0) {
    val maxHouses = 5
    def calculateHousePrice(purchasePrice: Int): Int = {
      val baseHousePrice = purchasePrice / 2
      ((baseHousePrice + 9) / 10) * 10
    }

    def buyHouse(player: IPlayer, field: PropertyField, game: IMonopolyGame): Try[(PropertyField, IPlayer)] = {
      val checkOwnership: Try[Unit] = field.owner match {
        case Some(owner) if owner.name == player.name => Success(())
        case _ => Failure(new IllegalArgumentException(s"${player.name} does not own ${field.name}."))
      }

      checkOwnership.flatMap { _ =>
        val housePrice = calculateHousePrice(field.price)

        val checkBalance: Try[Unit] =
          if (player.balance >= housePrice) Success(())
          else Failure(new IllegalArgumentException(s"${player.name} does not have enough money to buy a house on ${field.name}. Needed: $housePrice, Has: ${player.balance}."))

        checkBalance.flatMap { _ =>
          val checkMaxHouses: Try[Unit] =
            if (field.house.amount < maxHouses) Success(())
            else Failure(new IllegalArgumentException(s"${field.name} already has the maximum number of houses ($maxHouses)."))

          checkMaxHouses.flatMap { _ =>
            val colorGroupProperties = game.board.fields.collect {
              case pf: PropertyField if pf.color == field.color => pf
            }
            val ownsEntireColorGroup = colorGroupProperties.forall(_.owner.exists(_.name == player.name))

            val checkColorGroup: Try[Unit] =
              if (ownsEntireColorGroup) Success(())
              else Failure(new IllegalArgumentException(s"${player.name} must own all properties in the ${field.color} group to build a house on ${field.name}."))

            checkColorGroup.map { _ =>
              val updatedField = field.copy(house = PropertyField.House(field.house.amount + 1))
              val updatedPlayer = player.changeBalance( -housePrice).getOrElse(player)
              (updatedField, updatedPlayer)
            }
          }
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
  def addMoney(player: IPlayer): IPlayer = {
    player.changeBalance(200).getOrElse(player)
  }

  override def accept[T](visitor: FieldVisitor[T]): T = visitor.visit(this)
}

case object JailField extends BoardField {
  override val index: Int = 11
  override val name: String = "Jail"
  override def accept[T](visitor: FieldVisitor[T]): T = visitor.visit(this)
}

case class GoToJailField() extends BoardField {
  override val index: Int = 31
  override val name: String = "GoToJail"
  def goToJail(player: IPlayer): IPlayer = {
    player.goToJail
  }
  override def accept[T](visitor: FieldVisitor[T]): T = visitor.visit(this)
}

case class FreeParkingField(amount: Int) extends BoardField {
  override val index: Int = 21
  override val name: String = "FreeParking"

  def apply(player: IPlayer): IPlayer = {
    player.changeBalance(amount).getOrElse(player)
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
    MoveCard("Advance to Boardwalk", "", 3, false),
    MoveCard("Advance to Go", "(Collect $200)", 1, true),
    MoveCard("Advance to Illinois Avenue", "If you pass Go, collect $200", 4, true)
  )
  override def accept[T](visitor: FieldVisitor[T]): T = visitor.visit(this)
}

case class CommunityChestField(index: Int) extends BoardField {
  override val name: String = "communityCard"

  override def accept[T](visitor: FieldVisitor[T]): T = visitor.visit(this)
}

case class TaxField(amount: Int, index: Int) extends BoardField {
  override val name: String = "TaxField"

  override def accept[T](visitor: FieldVisitor[T]): T = visitor.visit(this)
}

case class TrainStationField(name: String, index: Int, price: Int, owner: Option[IPlayer]) extends BoardField with BuyableField {
  override def accept[T](visitor: FieldVisitor[T]): T = visitor.visit(this)
  override def withNewOwner(player: IPlayer): TrainStationField = this.copy(owner = Some(player))
}

case class UtilityField(name: String, index: Int, price: Int, utility: UtilityField.UtilityCheck, owner: Option[IPlayer]) extends BoardField with BuyableField {
  override def withNewOwner(player: IPlayer): UtilityField = this.copy(owner = Some(player))
  override def accept[T](visitor: FieldVisitor[T]): T = visitor.visit(this)
}

object UtilityField {
  enum UtilityCheck:
    case utility
}