package de.htwg.model.modelBaseImple

  import de.htwg.Board
  import de.htwg.model.modelBaseImple.{ Player}

  class FieldVisitor(currentPlayer: Player, allPlayers: Vector[Player], board: Board, diceResult: Int, ownedProperties: Map[Player, List[PropertyField]], ownedTrainStations: Map[Player, Int], ownedUtilities: Map[Player, Int]) {
    def visit(field: PropertyField): Int = {
      if (field.owner.isEmpty || field.owner.get == currentPlayer) {
        0
      } else {
        val baseRent = field.rent
        val houseRent = field.house.amount * (baseRent / 2)
        val colorGroup = board.fields.collect { case pf: PropertyField if pf.color == field.color => pf }
        val ownsAll = colorGroup.forall(_.owner.exists(_.name == field.owner.get.name))
        val monopolyMultiplier = if (ownsAll) 2 else 1
        (baseRent + houseRent) * monopolyMultiplier
      }
    }

    def visit(field: TrainStationField): Int = {
      field.owner.map { owner =>
        ownedTrainStations.getOrElse(owner.asInstanceOf[Player], 0) match {
          case 1 => 25
          case 2 => 50
          case 3 => 100
          case 4 => 200
          case _ => 0
        }
      }.getOrElse(0)
    }

    def visit(field: UtilityField): Int = {
      field.owner.map { owner =>
        diceResult * (if (ownedUtilities.getOrElse(owner.asInstanceOf[Player], 0) == 2) 10 else 4)
      }.getOrElse(0)
    }

    def visit(field: TaxField): Int = field.amount

    def visit(field: GoToJailField): Int = 0
    def visit(field: FreeParkingField): Int = 0
    def visit(field: ChanceField): Int = 0
    def visit(field: CommunityChestField): Int = 0
    def visit(field: BoardField): Int = field match {
      case GoField => 0
      case JailField => 0
    }
  }