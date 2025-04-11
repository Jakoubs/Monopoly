package de.htwg.model
import org.scalatest.matchers.should.Matchers.{shouldBe, *}
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.model.BoardField
import de.htwg.model.PropertyField.Color.{Brown, DarkBlue, Red}
import de.htwg.model.PropertyField.{House, Mortgage}
class BoardFieldSpec extends AnyWordSpec {
  "PropertyField" should {
    "build a property field" in {
      val f1 = PropertyField("kpAlee", 10, 100, 20, Some("p1"), Red, Mortgage(1000))
      f1.name should be("kpAlee")
      f1.price should be(100)
      f1.rent should be(20)
      f1.owner should be(Some("p1"))
      f1.color should be(Red)
      f1.mortgage.price should be(1000)
      f1.house.amount should be(0)
    }
    "buildHomes" in {
      val f1 = PropertyField("kpAlee", 4, 100, 20, Some("P1"), Red, Mortgage(1000))
      val p1 = Player("TestPlayer", 1000, 5)
      val (newf1, newp1) = f1.house.buyHouse(p1, f1)
      newf1.house.amount should be(1)
      newp1.balance should be(950)
    }
    "not buildHomes if balance is to low" in {
      val f1 = PropertyField("kpAlee", 4, 100, 20, Some("P1"), Red, Mortgage(1000))
      val p1 = Player("TestPlayer", 0, 5)
      val (newf1, newp1) = f1.house.buyHouse(p1, f1)
      newf1.house.amount should  be(0)
      newp1.balance should be(0)
    }
    "not buildHomes if max Hotel" in {
      val f1 = PropertyField("kpAlee", 4, 100, 20, Some("P1"), Red, Mortgage(1000),House(5))
      val p1 = Player("TestPlayer", 1000, 5)
      val (newf1, newp1) = f1.house.buyHouse(p1, f1)
      newf1.house.amount should  be(5)
      newp1.balance should be(1000)
    }

    "calculate house price based on rent correctly" in {
      val f1 = PropertyField("kpAlee", 4, 100, 20, Some("P1"), Red, Mortgage(1000))
      House().calculateHousePrice(f1.price) should be(50)

      val f2 = PropertyField("Park Place", 5, 350, 35, Some("P2"), DarkBlue, Mortgage(1750))
      House().calculateHousePrice(f2.price) should be(180)

      val f3 = PropertyField("Baltic Avenue", 5, 64, 4, Some("P3"), Brown, Mortgage(300))
      House().calculateHousePrice(f3.price) should be(40)
    }
  }

  "GoField" should {
    "be created" in {
      val goField = GoField
      goField.index should be(1)
      goField.name should be("GoField")
    }
  }

  "JailField" should {
    "be created" in{
      val jail = JailField
      jail.index should be(11)
      jail.name should be("Jail")
    }
  }

  "GoToJailField" should {
    "have a name and an index" in {
      val goToJailField = GoToJailField()
      goToJailField.name should be("GoToJail")
      goToJailField.index should be(31)
    }
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
    "have a name and an index and an amount" in {
      val freeParkingField = FreeParkingField(0)
      freeParkingField.name should be("FreeParking")
      freeParkingField.index should be(21)
      freeParkingField.amount should be(0)
    }
    "increase amount when player losemoney to Card" in {
      val player = Player("TestPlayer", 100, 21)
      val action = LoseMoney(100)
      val freeParkingField = FreeParkingField(0)
      val (updatedPlayer, updatedField) = action.apply(player,freeParkingField)
      updatedField.amount should be(100)
    }
    "give player amount when on field" in {
      val player = Player("TestPlayer", 100, 21)
      val freeParkingField = FreeParkingField(100)
      val updatedplayer = freeParkingField.apply(player)
      updatedplayer.balance should be(200)
    }
    "decrease amount when player collects money" in {
      val player = Player("TestPlayer", 100, 21)
      val freeParkingField = FreeParkingField(100)
      val updatedField = freeParkingField.resetAmount()
      updatedField.amount should be(0)
    }
  }

  "ChanceField" should {
  }
  "CommunityChestField" should {
  }
}