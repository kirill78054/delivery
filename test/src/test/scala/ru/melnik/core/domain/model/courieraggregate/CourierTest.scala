package ru.melnik.core.domain.model.courieraggregate

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import ru.melnik.core.domain.model.courieraggregate.CourierStatus.{Busy, Free}
import ru.melnik.core.domain.sharedkernel.Location

class CourierTest extends AnyWordSpec with Matchers {

  "Courier invariants" should {
    val courierName: String = "Kirill"
    val transportName: String = "Car"
    val transportSpeed: Int = 2

    "create an instance with valid id and location" in {
      val result = Courier(courierName, transportName, transportSpeed, Location.minLocation)

      result should not be null
      result.id should not be null
      result.name shouldBe courierName
      result.transport should not be null
      result.transport.name shouldBe transportName
      result.transport.speed shouldBe transportSpeed
      result.location should not be null
      result.location shouldBe Location.minLocation
      result.courierStatus shouldBe Free
    }

    "create an instance with null name" in {
      val exception = intercept[IllegalArgumentException](Courier(null, transportName, transportSpeed, Location.minLocation))
      exception.getMessage shouldBe "requirement failed: id is null"
    }

    "create an instance with empty name" in {
      val exception = intercept[IllegalArgumentException](Courier("", transportName, transportSpeed, Location.minLocation))
      exception.getMessage shouldBe "requirement failed: id is empty"
    }

    "create an instance with null location" in {
      val exception = intercept[IllegalArgumentException](Courier(courierName, transportName, transportSpeed, null))
      exception.getMessage shouldBe "requirement failed: location is null"
    }
  }

  "setCourierStatus" should {

    "setBusy with valid state" in {
      val courier = Courier("Kirill", "Car", 2, Location.minLocation)
      courier.setBusy()

      courier.courierStatus shouldBe Busy
    }

    "double setBusy" in {
      val courier = Courier("Kirill", "Car", 2, Location.minLocation)
      courier.setBusy()
      val exception = intercept[IllegalArgumentException](courier.setBusy())
      exception.getMessage shouldBe "courierStatus status is busy"
    }

    "setFree with valid state" in {
      val courier = Courier("Kirill", "Car", 2, Location.minLocation)
      courier.setBusy()
      courier.setFree()

      courier.courierStatus shouldBe Free
    }

    "double setFree" in {
      val courier = Courier("Kirill", "Car", 2, Location.minLocation)
      courier.setFree()
      courier.setFree()

      courier.courierStatus shouldBe Free
    }
  }

  "move" should {
    "move with valid state" in {
      val courier = Courier("Kirill", "Car", 2, Location.minLocation)
      courier.setBusy()
      courier.move(Location(5, 5))
    }

    "move with free courier" in {
      val courier = Courier("Kirill", "Car", 2, Location.minLocation)
      val exception = intercept[IllegalArgumentException](courier.move(Location(5, 5)))
      exception.getMessage shouldBe "requirement failed: courierStatus status is`t busy"
    }
  }

  "calcTimeToTarget" should {
    "calcTimeToTarget with valid state and result 8" in {
      val courier = Courier("Kirill", "Car", 1, Location.minLocation)
      courier.calcTimeToTarget(Location(5, 5)) shouldBe 8
    }
    "calcTimeToTarget with valid state and result 4" in {
      val courier = Courier("Kirill", "Car", 2, Location.minLocation)
      courier.calcTimeToTarget(Location(5, 5)) shouldBe 4
    }
    "calcTimeToTarget with valid state and result 2" in {
      val courier = Courier("Kirill", "Car", 3, Location(2, 2))
      courier.calcTimeToTarget(Location(5, 5)) shouldBe 2
    }

    "move with busy courier" in {
      val courier = Courier("Kirill", "Car", 2, Location.minLocation)
      courier.setBusy()
      val exception = intercept[IllegalArgumentException](courier.calcTimeToTarget(Location(5, 5)))
      exception.getMessage shouldBe "requirement failed: courierStatus status is`t free"
    }
  }

}
