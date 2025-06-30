package de.htwg.model.modelBaseImple

import de.htwg.model.IPlayer

class Trade {
  def tradeCall(p1: IPlayer, p2: IPlayer, p1ToP2: Int, p2ToP1: Int): Option[(IPlayer, IPlayer)] = {

    if (p1ToP2 == 0 && p2ToP1 == 0) return None

    if (p1.balance < p1ToP2 || p2.balance < p2ToP1) return None

    val updatedP1 = p1.changeBalance( - p1ToP2 + p2ToP1)
    val updatedP2 = p2.changeBalance( - p2ToP1 + p1ToP2)

    Some((updatedP1, updatedP2))
  }
}
