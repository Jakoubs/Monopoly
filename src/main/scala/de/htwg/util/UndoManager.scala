package de.htwg.util

import de.htwg.model.IMonopolyGame

class UndoManager(
                   val undoStack: List[IMonopolyGame] = Nil,
                   val redoStack: List[IMonopolyGame] = Nil
                 ) {

  def doStep(currentGame: IMonopolyGame, newGame: IMonopolyGame): UndoManager =
    UndoManager(currentGame :: undoStack, Nil)

  def undo(currentGame: IMonopolyGame): (IMonopolyGame, UndoManager) = undoStack match {
    case previous :: rest =>
      val newRedoStack = currentGame :: redoStack
      (previous, UndoManager(rest, newRedoStack))
    case Nil =>
      (currentGame, this)
  }

  def redo(currentGame: IMonopolyGame): (IMonopolyGame, UndoManager) = redoStack match {
    case next :: rest =>
      val newUndoStack = currentGame :: undoStack
      (next, UndoManager(newUndoStack, rest))
    case Nil =>
      (currentGame, this)
  }
}
