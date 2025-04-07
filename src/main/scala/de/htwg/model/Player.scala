package de.htwg.model

case class Player(name: String,
                  balance: Int,
                  position: Int = 0,
                  isInJail: Boolean = false,
                  //properties: List[PropertyField] = List()
                 ) {
  override def toString: String = name
}