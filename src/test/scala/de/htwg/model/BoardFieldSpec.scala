package de.htwg.model
import org.scalatest.matchers.should.Matchers.{shouldBe, *}
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.model.BoardField
class BoardFieldSpec extends AnyWordSpec {
  "PropertyField" should {
    "build a property field" in{
      val f1 = PropertyField("kpAlee",10,100,20,Some("p1"),"red",1000,Some(0))
      f1.name should be("kpAlee")
      f1.price should be(100)
      f1.rent should be(20)
      f1.owner should be(Some("p1"))
      f1.color should be("red")
      f1.mortgage should be(1000)
      f1.house should be(Some(0))
    }
    "buildHomes" in{
      val f1 = PropertyField("kpAlee",4, 100, 20, Some("P1"), "red", 1000, Some(0))
      val p1 = Player("TestPlayer", 1000, 5)
      val (newf1, newp1) = f1.buyHouse(p1, f1, 1)
      newf1.house should be(Some(1))
      newp1.balance should be(900)
    }
    "calculate house price based on rent correctly" in {
      val f1 = PropertyField("kpAlee",4, 100, 20, Some("P1"), "red", 1000, Some(0))
      f1.calculateHousePrice(f1.price) should be(50)

      val f2 = PropertyField("Park Place",5, 350, 35, Some("P2"), "blue", 1750, Some(0))
      f2.calculateHousePrice(f2.price) should be(180)

      val f3 = PropertyField("Baltic Avenue",5, 64, 4, Some("P3"), "brown", 300, Some(0))
      f3.calculateHousePrice(f3.price) should be(40)
    }
  }

  "GoField" should {
  }

  "JailField" should {
  }

  "VisitField" should {
  }
  "GoToJailField" should {
    "send a Player to jail" in {
      val player = Player("TestPlayer", 1000, 5)
      val goToJailField = GoToJailField()
      val updatedPlayer = goToJailField.goToJail(player)
      updatedPlayer.position should be(11)
      updatedPlayer.isInJail should be(true)
    }

    "not give Player money when moving over Go" in {
      val player = Player("TestPlayer", 1000, 5)
      val goToJailField = GoToJailField()
      val updatedPlayer = goToJailField.goToJail(player)
      updatedPlayer.balance should be(1000)
    }

  }
  "FreeParkingField" should {
  }
  "ChanceField" should {
  }
  "CommunityChestField" should {
  }
}
