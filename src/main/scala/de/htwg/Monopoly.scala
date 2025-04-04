package de.htwg

class Monopoly

import model.Player
import model.Dice

object Monopoly:
  def main(args: Array[String]): Unit = {
    val student = Player("Alice")
    println("Hello " + student)
    val (a, b) = Dice().rollDice()
    println(s"$a and $b")
  }

