package de.htwg.model

import de.htwg.Board

trait FieldVisitor[T] {
  def visit(property: PropertyField): T
  def visit(station: TrainStationField): T
  def visit(utility: UtilityField): T
  def visit(goToJail: GoToJailField): T
  def visit(freeParking: FreeParkingField): T
  def visit(chance: ChanceField): T
  def visit(communityChest: CommunityChestField): T
  def visit(tax: TaxField): T
  def visit(other: BoardField): T
}

class RentVisitor(currentPlayer: Player, allPlayers: Vector[Player], board: Board, diceResult: Int, ownedProperties: Map[Player, List[PropertyField]], ownedTrainStations: Map[Player, Int], ownedUtilities: Map[Player, Int]) extends FieldVisitor[Int] {
  override def visit(field: PropertyField): Int = {
    if (field.owner.isEmpty || field.owner.get == currentPlayer) {
      0
    } else {
      val baseRent = field.rent
      val houseRent = field.house.amount * (baseRent / 2)
      val colorGroup = board.fields.collect { case pf: PropertyField if pf.color == field.color => pf }
      val ownsAll = colorGroup.forall(_.owner.exists(_.name == field.owner.get.name))
      val monopolyMultiplier = if (ownsAll && field.house.amount == 0) 2 else 1
      (baseRent + houseRent) * monopolyMultiplier
    }
  }

  override def visit(field: TrainStationField): Int = {
    field.owner.map { owner =>
      ownedTrainStations.getOrElse(owner, 0) match {
        case 1 => 25
        case 2 => 50
        case 3 => 100
        case 4 => 200
        case _ => 0
      }
    }.getOrElse(0)
  }

  override def visit(field: UtilityField): Int = {
    field.owner.map { owner =>
      diceResult * (if (ownedUtilities.getOrElse(owner, 0) == 2) 10 else 4)
    }.getOrElse(0)
  }

  override def visit(field: GoToJailField): Int = 0
  override def visit(field: FreeParkingField): Int = 0
  override def visit(field: ChanceField): Int = 0
  override def visit(field: CommunityChestField): Int = 0
  override def visit(field: TaxField): Int = field.amount
  override def visit(field: BoardField): Int = field match {
    case GoField => 0
    case JailField => 0
    case _ => 0
  }
}