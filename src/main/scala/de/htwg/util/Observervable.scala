package de.htwg.util
package util

trait Observer:
  def update(e: Event): Unit

trait Observable:
  var observer: Vector[Observer] = Vector()
  def add(s: Observer) = subscribers = subscribers :+ s
  def remove(s: Observer) = subscribers = subscribers.filterNot(o => o == s)
  def notify(e: Event) = subscribers.foreach(o => o.update(e))