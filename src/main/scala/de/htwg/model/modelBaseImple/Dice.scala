package de.htwg.model.modelBaseImple

import scala.util.Random

class Dice:
  def rollDice(valid: Boolean): (Int, Int) = {
    if(valid) {
      val waitFuture = SoundPlayer().playBackground("src/main/resources/sound/RollDice.wav")
    }
    val a = Random.nextInt(6) + 1
    val b = Random.nextInt(6) + 1
    (a, b)
  }

  def rollDice(d1: Int, d2: Int): (Int, Int) = {
      (d1, d2)
  }




