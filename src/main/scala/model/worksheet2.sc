import scala.util.Random

case class House(
                  price: Int,
                  amount: Int
                )

case class Mortgage(
                     price: Int,
                     active: Boolean
                   )

enum Color:
  case Brown, LightBlue, Pink, Orange, Red, Yellow, Green, DarkBlue

sealed trait BoardField
case class PropertyField(name: String, price: Int, rent: Int, owner: Option[String] = None, color: Color, mortgage: Mortgage, house: Option[House] = None) extends BoardField
case object GoField extends BoardField // "Los"-Feld
case object JailField extends BoardField
case object VisitField extends BoardField
case class GoToJailField(destination: Int) extends BoardField
case class FreeParkingField(amount: Int) extends BoardField
case object ChanceField extends BoardField
case object CommunityChestField extends BoardField

def rollDice(): (Int, Int) = {
  val a =   Random.nextInt(6) + 1
  val b =   Random.nextInt(6) + 1
  println(s"You rolled $a and $b! That's ${a + b} moves.")
  (a, b)
}

// Ein Spieler im Monopoly
case class Player(
                   name: String,
                   balance: Int,
                   position: Int = 0,
                   isInJail: Boolean = false,
                   properties: List[PropertyField] = List()
                 ) {
  def moveToIndex(newPos: Int): Player = {
    if(!isInJail) {
      copy(position = position + newPos)
    }
    this
  }

  def releaseFromJail(): Player = {
    copy(isInJail = false)
  }

  def getIsInJail: Boolean = {
    isInJail
  }
  def goToJail(): Player = {
    copy(position = 11, isInJail = true)
  }
  def playerMove(rollcount: Int = 1): Player = {
    if (rollcount == 3) {
      println("You rolled doubles 3 times -> Jail :(")
      return goToJail()
    }
    if(!isInJail){
      val (diceA, diceB) = rollDice()
      val updatePlayer = moveToIndex(diceA + diceB)
      //playerAction() -> Noch Implimentieren
      if(diceA == diceB) {
        return updatePlayer.playerMove(rollcount + 1)
      }
      return updatePlayer
    }
    this
  }
}

sealed trait CardAction
case class GainMoney(amount: Int) extends CardAction {
  def apply(player: Player): Player = {
    player.copy(balance = player.balance + amount)
  }
}
case class LoseMoney(amount: Int) extends CardAction {
  def apply(player: Player): Player = {
    player.copy(balance = player.balance - amount)
  }
}
case object CardToJail extends CardAction {
  def apply(player: Player): Player = {
    player.goToJail()
  }
}
case class CardMoveTo(index: Int) extends CardAction {
  def apply(player: Player): Player = {
    player.moveToIndex(index)
  }
}

sealed trait Card  {
  def name: String
  def discription: String
  def action: CardAction
}
case class MoneyCard(name: String, discription: String, amount: Int) extends Card {
  override def action: CardAction = GainMoney(amount)
}
case class MoveCard(name: String, discription: String, index: Int) extends Card {
  override def action: CardAction = CardMoveTo(index)
}
case class PenaltyCard(name: String, discription: String, amount: Int) extends Card {
  override def action: CardAction = LoseMoney(amount)

}
case class JailCard(name: String, discription: String) extends Card {
  override def action: CardAction = CardToJail

}

case class Board(fields: Vector[BoardField])

case class MonopolyGame(
                         players: Vector[Player],
                         board: Board,
                         currentPlayerIndex: Int = 0
                       )

val defaultBoard: Board = Board(Vector(
  GoField,
  PropertyField("Mediterranean Avenue", price = 60, rent = 2, color = Color.Green, mortgage = Mortgage(price = 10, active = false)),
  PropertyField("Baltic Avenue", price = 60, rent = 4, color = Color.Brown, mortgage = Mortgage(price = 10, active = false)),
  JailField,
  FreeParkingField(amount = 30),
  CommunityChestField,
  ChanceField,
))

val game: MonopolyGame = MonopolyGame(
  players = Vector(
    Player("Alice", 1500, 5),
    Player("Bob", 1500)
  ),
  board = defaultBoard
)



val P1 = Player("KP", 1500,  5)
P1.position
val P2 = P1.playerMove()
P2.position

val P3 = GainMoney(amount = 100).apply(player = P1)
P3.balance
val P4 = LoseMoney(amount = 100).apply(player = P3)
P4.balance
val P5 = CardToJail.apply(P4)
P5.position
val P6 = CardMoveTo(index = 10).apply(P5)
val P7 = P6.releaseFromJail()
P7.isInJail