package de.htwg.model.modelBaseImple

import de.htwg.Board
import de.htwg.model.IPlayer
class Trade {
  def tradeCall(
                 p1: IPlayer,
                 p2: IPlayer,
                 Mp1ToP2: Int,
                 Mp2ToP1: Int,
                 pP1toP2: List[BuyableField],
                 pP2toP1: List[BuyableField],
                 board: Board
               ): Option[(IPlayer, IPlayer, Board)] = {


    if (p1.balance < Mp1ToP2 || p2.balance < Mp2ToP1) {
      return None
    }

    val updatedP1 = p1.changeBalance(-Mp1ToP2 + Mp2ToP1)
    val updatedP2 = p2.changeBalance(-Mp2ToP1 + Mp1ToP2)

    val p1ToP2Indices = pP1toP2.map(_.index).toSet
    val p2ToP1Indices = pP2toP1.map(_.index).toSet

    val updatedFields = board.fields.zipWithIndex.map { case (field, idx) =>
      val fieldIndex = idx + 1

      field match {
        case bf: BuyableField =>
          val originalOwner = bf.owner.map(_.name).getOrElse("None")

          if (p1ToP2Indices.contains(fieldIndex)) {
            bf match {
              case pf: PropertyField => pf.copy(owner = Some(updatedP2))
              case tf: TrainStationField => tf.copy(owner = Some(updatedP2))
              case uf: UtilityField => uf.copy(owner = Some(updatedP2))
              case _ =>
                bf
            }
          } else if (p2ToP1Indices.contains(fieldIndex)) {
            bf match {
              case pf: PropertyField => pf.copy(owner = Some(updatedP1))
              case tf: TrainStationField => tf.copy(owner = Some(updatedP1))
              case uf: UtilityField => uf.copy(owner = Some(updatedP1))
              case _ =>
                bf
            }
          } else {
            bf
          }
        case f => f
      }
    }

    val updatedBoard = board.copy(fields = updatedFields)

    val verifyP1Properties = updatedP1.getProperties(updatedBoard.fields)
    val verifyP2Properties = updatedP2.getProperties(updatedBoard.fields)

    Some((updatedP1, updatedP2, updatedBoard))
  }
}