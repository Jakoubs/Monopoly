package de.htwg.model

case class Color(name: String, group: Int)
sealed trait BoardField {
val name: String
val index: Int
}
case class PropertyField(name: String, index: Int, price: Int, rent: Int, owner: Option[String] = None,color: String, mortgage: Int, house: Option[Int] = None) extends BoardField {
  def calculateHousePrice(purchasePrice: Int): Int = {
    val baseHousePrice = purchasePrice / 2
    ((baseHousePrice + 9) / 10) * 10
  }
  def buyHouse(player: Player,field: PropertyField): (PropertyField, Player) = {
    if(player.balance < field.calculateHousePrice(field.price) || field.house.get==5)
      (field, player)
    else
      (field.copy(house = Some(house.get + 1)), player.copy(balance = player.balance - price))
  }


}

case object GoField extends BoardField {
  override val index: Int = 1
  override val name: String = "GoField"
  /*def getGoBonus(player: Player): Int = {
    if()
  }

   */
}
case object JailField extends BoardField{
  override val index: Int = 11
  override val name: String = "Jail"
}
case object VisitField extends BoardField{
  override val index: Int = 11
  override val name: String = "JailOnVisit"
}
case class GoToJailField() extends BoardField{
  override val index: Int = 31
  override val name: String = "GoToJail"
  def goToJail(player: Player): Player = {
    player.goToJail()
  }
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
}

case class ChanceField() extends BoardField {
  override val index: Int = 20
  override val name: String = "ChanceField"

}
case class CommunityChestField(communityCardList: List[Card]) extends BoardField{
  override val index: Int = 25
  override val name: String = "communityCard"
}