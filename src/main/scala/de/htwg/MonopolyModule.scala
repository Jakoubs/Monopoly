// src/main/scala/de/htwg/guice/MonopolyModule.scala
package de.htwg

import com.google.inject.AbstractModule
import com.google.inject.Provides

import net.codingwell.scalaguice.ScalaModule
import javax.inject.Singleton
import de.htwg.controller.IController
import de.htwg.controller.controllerBaseImpl.Controller
import de.htwg.model.modelBaseImple.{MonopolyGame}
import de.htwg.model.modelBaseImple.Player
import de.htwg.model.{IMonopolyGame, IPlayer}
import de.htwg.model.modelBaseImple.Dice
import de.htwg.Monopoly
import net.codingwell.scalaguice.InjectorExtensions._

class MonopolyModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    // Bindings für Interfaces → Implementierungen
    bind[IController].to[Controller].asEagerSingleton()
  }

  // Wir wollen IMonopolyGame aus Monopoly.defineGame()
  @Provides @Singleton
  def provideGame(): IMonopolyGame = Monopoly.defineGame()
}
