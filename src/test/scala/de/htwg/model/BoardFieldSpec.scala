package de.htwg.model
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
class BoardFieldSpec extends AnyWordSpec {
  "PropertyField" should {
    "build a property field" in {
      val f1 = PropertyField("kpAlee", 10, 100, 20, Some("p1"), "red", 1000, Some(0))
      f1.name should be("kpAlee")
      f1.price should be(100)
      f1.rent should be(20)
      f1.owner should be(Some("p1"))
      f1.color should be("red")
      f1.mortgage should be(1000)
      f1.house should be(Some(0))
    }
    "buildHomes" in {
      val f1 = PropertyField("kpAlee", 4, 100, 20, Some("P1"), "red", 1000, Some(0))
      val p1 = Player("TestPlayer", 1000, 5)
      val (newf1, newp1) = f1.buyHouse(p1, f1)
      newf1.house should be(Some(1))
      newp1.balance should be(900)
    }
    "not buildHomes if balance is to low" in {
      val f1 = PropertyField("kpAlee", 4, 100, 20, Some("P1"), "red", 1000, Some(0))
      val p1 = Player("TestPlayer", 0, 5)
      val (newf1, newp1) = f1.buyHouse(p1, f1)
      newf1.house should  be(Some(0))
      newp1.balance should be(0)
    }
    "not buildHomes if max Hotel" in {
      val f1 = PropertyField("kpAlee", 4, 100, 20, Some("P1"), "red", 1000, Some(5))
      val p1 = Player("TestPlayer", 1000, 5)
      val (newf1, newp1) = f1.buyHouse(p1, f1)
      newf1.house should  be(Some(5))
      newp1.balance should be(1000)
    }

    "calculate house price based on rent correctly" in {
      val f1 = PropertyField("kpAlee", 4, 100, 20, Some("P1"), "red", 1000, Some(0))
      f1.calculateHousePrice(f1.price) should be(50)

      val f2 = PropertyField("Park Place", 5, 350, 35, Some("P2"), "blue", 1750, Some(0))
      f2.calculateHousePrice(f2.price) should be(180)

      val f3 = PropertyField("Baltic Avenue", 5, 64, 4, Some("P3"), "brown", 300, Some(0))
      f3.calculateHousePrice(f3.price) should be(40)
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
    "be created" in {
      val chance = ChanceField()
      chance.index should be(20)
      chance.name should be("ChanceField")
    }
  }
  "CommunityChestField" should {
    "be created" in {
      val gotToJail = JailCard("Go to jail","You are a criminal! Go to jail.")
      val community = CommunityChestField(List(gotToJail))
      community.index should be(25)
      community.name should be("communityCard")
    }
  }
}