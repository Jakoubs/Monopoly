package de.htwg.controller.mockImpl

import de.htwg.controller._
import de.htwg.model._
import de.htwg.MonopolyGame

class ControllerMock extends ControllerInterface {
  override def game: MonopolyGame = null
  override def dice: Dice = null
  override def state: GameState = null
  override def currentPlayer: Player = Player("Mock", 0, 1500, 0, false, 0, 0, false)
  override def board: Board = null
  override def players: Vector[Player] = Vector(currentPlayer)
  override def sound: Boolean = false

  override def getTurnInfo: TurnInfo = null
  override def updateTurnInfo(newInfo: TurnInfo): Unit = {}

  override def handleInput(input: OpEnum): Unit = {}

  override def updatePlayer(player: Player): Unit = {}
  override def updateBoardAndPlayer(field: BoardField, player: Player): Unit = {}
  override def switchToNextPlayer(): Unit = {}

  override def executeCommand(cmd: Command): Unit = {}
  override def undo(): Unit = {}
  override def redo(): Unit = {}

  override def isGameOver: Boolean = false

  override def getBoardString: String = "Mock Board"
  override def getInventory: String = "Mock Inventory"
  override def getCurrentPlayerStatus: String = "Mock Status"

  override def getOwnedProperties(): Map[Player, List[PropertyField]] = Map(currentPlayer -> List())
  override def getOwnedTrainStations(): Map[Player, Int] = Map(currentPlayer -> 0)
  override def getOwnedUtilities(): Map[Player, Int] = Map(currentPlayer -> 0)
}