package de.htwg.model;

class Trade {
  def tradeCall(p1: Player, p2: Player, p1ToP2: Int, p2ToP1: Int): Option[(Player, Player)] = {

    if (p1ToP2 == 0 && p2ToP1 == 0) return None

    if (p1.balance < p1ToP2 || p2.balance < p2ToP1) return None

    val updatedP1 = p1.copy(balance = p1.balance - p1ToP2 + p2ToP1)
    val updatedP2 = p2.copy(balance = p2.balance - p2ToP1 + p1ToP2)

    Some((updatedP1, updatedP2))
  }
}
