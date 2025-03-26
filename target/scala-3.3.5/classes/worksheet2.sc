import scala.util.Random;

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

// Ein Monopoly-Feld
sealed trait BoardField
case class PropertyField(name: String, price: Int, rent: Int, /*owner: Option[Player] = None,*/ color: Color, mortgage: Mortgage, house: Option[House] = None) extends BoardField
case object GoField extends BoardField // "Los"-Feld
case object JailField extends BoardField
case object VisitField extends BoardField
case class GoToJailField(destination: Int) extends BoardField
case class FreeParkingField(amount: Int) extends BoardField
case object ChanceField extends BoardField
case object CommunityChestField extends BoardField

// Ein Spieler im Monopoly
case class Player(
                   name: String,
                   balance: Int,
                   position: Int = 0,
                   isInJail: Boolean = false,
                   properties: List[PropertyField] = List()
                 )
sealed trait CardAction
case class GainMoney(amount: Int) extends CardAction
case class LoseMoney(amount: Int) extends CardAction
case class GoToJail(destination: Int) extends CardAction
case class MoveToIndex(index: Int) extends CardAction

// Das Monopoly-Spielbrett
case class Board(fields: Vector[BoardField])

// Monopoly-Spiel-Zustand
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

def rollDice(): Int = {
  val a =   Random.nextInt(6) + 1
  val b =   Random.nextInt(6) + 1
  println(s"Du hast ${a} und ${b} gewürfelt! Das sind ${a + b} Züge.")
  a + b
}

