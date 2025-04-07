package de.htwg

class Monopoly

import model.Player
import model.Dice

object Monopoly:
  def main(args: Array[String]): Unit = {
    println("Hello ")
    val (a, b) = Dice().rollDice()
    println(s"$a and $b")
  }

