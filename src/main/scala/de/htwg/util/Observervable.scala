package de.htwg.util
package util

trait Observer:
  def update(): Unit

trait Observable:
  var observer: Vector[Observer] = Vector()

  def add(s: Observer): Unit = observer = observer :+ s
  def remove(s: Observer): Unit = observer = observer.filterNot(o => o == s)
  def notifyObservers(): Unit = observer.foreach(o => o.update())