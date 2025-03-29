package de.htwg.model

import scala.util.Random

class Dice:
  def rollDice(): (Int, Int) = {
    val a = Random.nextInt(6) + 1
    val b = Random.nextInt(6) + 1
    //println(s"You rolled $a and $b! That's ${a + b} moves.")
    (a, b)
  }

