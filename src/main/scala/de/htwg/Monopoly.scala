package de.htwg

import de.htwg.controller.controllerBaseImpl.Controller
import de.htwg.model.modelBaseImple.PropertyField.Color.{Brown, DarkBlue, Green, LightBlue, Orange, Pink, Red, Yellow}
import de.htwg.model.modelBaseImple.PropertyField.calculateRent
import de.htwg.model.modelBaseImple.MonopolyGame
import scala.io.StdIn.readLine
import scala.util.Random
import de.htwg.view.Tui
import de.htwg.view.GUI
import de.htwg.model.*
import de.htwg.model.modelBaseImple.{BoardField, ChanceField, CommunityChestField, Dice, FreeParkingField, GoField, GoToJailField, JailField, Player, PropertyField, SoundPlayer, TaxField, TrainStationField, UtilityField}
import com.google.inject.Guice
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future // Import Future for asynchronous execution

case class Board(fields: Vector[BoardField])

object Monopoly:

  def main(args: Array[String]): Unit = {
    val injector = Guice.createInjector(new MonopolyModule)

    // Get the controller instance from Guice
    val controllerInstance = injector.getInstance(classOf[controller.controllerBaseImpl.Controller])

    // Inject the controller into Tui
    val tui = injector.getInstance(classOf[Tui]) // Tui is already @Inject, so this should work
    // Ensure Tui's constructor takes Controller: class Tui @Inject() (controller: Controller)

    // Pass the controller to GUI
    val gui = injector.getInstance(classOf[GUI.type]) // GUI is an object, so it won't be constructed by Guice.
    // We need to set its controller manually.
    gui.setController(controllerInstance) // Call the setter method

    Future {
      tui.run() // tui.run(controller) is no longer needed if controller is injected into Tui
    }

    // 4. GUI starten
    gui.main(args)
  }


