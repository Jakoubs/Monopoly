package de.htwg.model

sealed trait BoardField {
val name: String
val index: Int
}
case class PropertyField(name: String, price: Int, rent: Int, owner: Option[String] = None, color: Color, mortgage: Mortgage, house: Option[House] = None) extends BoardField {
}
case object GoField extends BoardField {
  override val index: Int = 1
}
case object JailField extends BoardField{
}
case object VisitField extends BoardField{
}
case class GoToJailField(destination: Int) extends BoardField{
}
case class FreeParkingField(amount: Int) extends BoardField{
}
case object ChanceField extends BoardField{
}
case class CommunityChestField(communityCardList: List[Card]) extends BoardField{
}