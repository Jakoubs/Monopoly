package de.htwg.controller
import de.htwg.model.IMonopolyGame
import de.htwg.model.modelBaseImple.{BuyableField, Player, PropertyField}
import scala.util.{Failure, Success, Try}

// Funktionales Command Interface
trait Command {
  def execute(game: IMonopolyGame): IMonopolyGame
}

// Buy Command - funktional
case class BuyCommand[T <: BuyableField](
                                          field: T,
                                          player: Player
                                        ) extends Command {

  def execute(game: IMonopolyGame): IMonopolyGame = {
    val (updatedField, updatedPlayer) = field.buy(player)
    game.withUpdatedBoardAndPlayer(updatedField, updatedPlayer)
  }
}

// Buy House Command - saubere funktionale Version
case class BuyHouseCommand(
                            field: PropertyField,
                            player: Player
                          ) extends Command {

  def execute(game: IMonopolyGame): IMonopolyGame = {
    // Annahme: IMonopolyGame hat eine buyHouseForPlayer Methode
    game.buyHouse(field, player) match {
      case Success(updatedGame) => updatedGame
      case Failure(_) => game // Unverändert bei Fehler
    }
  }
}

// Roll Dice Command - mit Würfelwerten als Parameter
case class RollDiceCommand(sound: Boolean) extends Command {
  def execute(game: IMonopolyGame): IMonopolyGame = {
    game.rollDice(sound)
  }
}

// Pay Jail Fee Command - funktional
case class PayJailFeeCommand(player: Player) extends Command {
  def execute(game: IMonopolyGame): IMonopolyGame = {
    val updatedPlayer = player.copy(
      isInJail = false,
      balance = player.balance - 50
    )
    game.withUpdatedPlayer(updatedPlayer)
  }
}