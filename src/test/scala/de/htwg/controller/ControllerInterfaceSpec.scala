import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.controller.mockImpl.ControllerMock
import de.htwg.model.modelBaseImple.{Player, PropertyField}

class ControllerInterfaceSpec extends AnyWordSpec with Matchers {

  val mockPlayer = Player("Mock", 1500, 0, false, 0)

  "getOwnedProperties" should {
    "return an empty map when no properties are owned" in {
      val controller = new ControllerMock
      controller.getOwnedProperties() shouldBe Map(mockPlayer -> List())
    }

    "return a map with owned properties for each player" in {
      val player = Player("Player 1", 1500, 1, isInJail = false, 0)
      val property = PropertyField("brown1", 2, 100, 10, Some(player), PropertyField.Color.Brown, PropertyField.Mortgage(10, false), PropertyField.House(0))
      val controller = new ControllerMock
      controller.updateBoardAndPlayer(property, player)
      controller.getOwnedProperties() shouldBe Map(mockPlayer -> List())
    }
  }

  "getOwnedTrainStations" should {
    "return an empty map when no train stations are owned" in {
      val controller = new ControllerMock
      controller.getOwnedTrainStations() shouldBe Map(mockPlayer -> 0)
    }

    "return a map with the count of owned train stations for each player" in {
      val player = Player("Player 1", 1500, 1, isInJail = false, 0)
      val controller = new ControllerMock
      controller.updatePlayer(player)
      controller.getOwnedTrainStations() shouldBe Map(mockPlayer -> 0)
    }
  }

  "getOwnedUtilities" should {
    "return an empty map when no utilities are owned" in {
      val controller = new ControllerMock
      controller.getOwnedUtilities() shouldBe Map(mockPlayer -> 0)
    }

    "return a map with the count of owned utilities for each player" in {
      val player = Player("Player 1", 1500, 1, isInJail = false, 0)
      val controller = new ControllerMock
      controller.updatePlayer(player)
      controller.getOwnedUtilities() shouldBe Map(mockPlayer -> 0)
    }
  }
}