package de.htwg.model

sealed trait BoardField {
val name: String
val index: Int
}
case class PropertyField(name: String, price: Int, rent: Int, owner: Option[String] = None, color: Color, mortgage: Mortgage, house: Option[House] = None) extends BoardField {
  def calculateHousePrice(purchasePrice: Int): Int = {
    val baseHousePrice = purchasePrice / 2
    ((baseHousePrice + 9) / 10) * 10
  }

}
case object GoField extends BoardField {
  override val index: Int = 1
  override val name: String = "GoField"
  /*def getGoBonus(player: Player): Int = {
    if()
  }*/
}
case object JailField extends BoardField{
  override val index: Int = 11
  override val name: String = "Jail"
}
case object VisitField extends BoardField{
  override val index: Int = 11
  override val name: String = "JailOnVisit"
}
case class GoToJailField(player: Player) extends BoardField{
  override val index: Int = 31
  override val name: String = "JailOnVisit"

  def goToJail(player: Player): Player = {
    player.goToJail()
  }
}
case class FreeParkingField(amount: Int) extends BoardField{
  override val index: Int = 21
  override val name: String = "FreeParking"
}
case object ChanceField(name: String, index: Int) extends BoardField{

}
case class CommunityChestField(communityCardList: List[Card]) extends BoardField{
}
