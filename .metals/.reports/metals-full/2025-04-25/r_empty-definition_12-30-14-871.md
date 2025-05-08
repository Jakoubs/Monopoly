error id: scala/package.Vector#
file://<WORKSPACE>/src/main/scala/de/htwg/util/Observervable.scala
empty definition using pc, found symbol in pc: scala/package.Vector#
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -Vector#
	 -scala/Predef.Vector#
offset: 113
uri: file://<WORKSPACE>/src/main/scala/de/htwg/util/Observervable.scala
text:
```scala
package de.htwg.util
package util

trait Observer:
  def update(): Unit

trait Observable:
  var observer: Vector@@[Observer] = Vector()
  def add(s: Observer) = observer = observer :+ s
  def remove(s: Observer) = observer = observer.filterNot(o => o == s)
  def notify(): Unit = observer.foreach(o => o.update())
```


#### Short summary: 

empty definition using pc, found symbol in pc: scala/package.Vector#