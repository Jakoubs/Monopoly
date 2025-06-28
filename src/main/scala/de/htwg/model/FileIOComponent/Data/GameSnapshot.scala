package de.htwg.model.FileIOComponent.Data


case class GameSnapshot(
                         players: Vector[PlayerSnapshot],
                         currentPlayerIndex: Int,
                         boardProperties: Vector[PropertySnapshot],
                         sound: Boolean
                       )

case class PlayerSnapshot(
                           name: String,
                           balance: Int,
                           position: Int,
                           isInJail: Boolean,
                           consecutiveDoubles: Int
                         )

case class PropertySnapshot(
                             index: Int,
                             ownerName: Option[String],
                             houses: Int
                           )