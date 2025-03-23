package ru.melnik.core.domain.service

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import ru.melnik.core.domain.model.courieraggregate.Courier
import ru.melnik.core.domain.model.orderaggregate.Order
import ru.melnik.core.domain.model.orderaggregate.OrderStatus.Assigned
import ru.melnik.core.domain.sharedkernel.Location

import java.util.UUID

class IDispatchServiceTest extends AnyWordSpec with Matchers {

  private val sut: IDispatchService = new DispatchService()

  "dispatch" should {
    "three couriers, first one is fast" in {
      val order = Order(UUID.randomUUID(), Location(10, 10))
      val kirill = Courier("Kirill", "Car", 3, Location(1, 1))
      val andrey = Courier("Andrey", "Bicycle", 2, Location(1, 1))
      val ivan = Courier("Iven", "Food", 1, Location(1, 1))
      val couriers = Seq(kirill, andrey, ivan)

      val courier = sut.dispatch(order, couriers)
      courier shouldBe kirill
      order.courierId shouldBe Some(kirill.id)
      order.status shouldBe Assigned
    }

    "three couriers, first one is fast, second one is close" in {
      val order = Order(UUID.randomUUID(), Location(10, 10))
      val kirill = Courier("Kirill", "Car", 3, Location(1, 1))
      val andrey = Courier("Andrey", "Bicycle", 2, Location(7, 7))
      val ivan = Courier("Iven", "Food", 1, Location(7, 7))
      val couriers = Seq(kirill, andrey, ivan)

      val courier = sut.dispatch(order, couriers)
      courier shouldBe andrey
      order.courierId shouldBe Some(andrey.id)
      order.status shouldBe Assigned
    }

    "three couriers, first one is fast, second third is close" in {
      val order = Order(UUID.randomUUID(), Location(10, 10))
      val kirill = Courier("Kirill", "Car", 3, Location(1, 1))
      val andrey = Courier("Andrey", "Bicycle", 2, Location(5, 5))
      val ivan = Courier("Iven", "Food", 1, Location(8, 8))
      val couriers = Seq(kirill, andrey, ivan)

      val courier = sut.dispatch(order, couriers)
      courier shouldBe ivan
      order.courierId shouldBe Some(ivan.id)
      order.status shouldBe Assigned
    }

    "three couriers, first - second - third equal distance" in {
      val order = Order(UUID.randomUUID(), Location(10, 10))
      val kirill = Courier("Kirill", "Car", 3, Location(7, 7))
      val andrey = Courier("Andrey", "Bicycle", 2, Location(8, 8))
      val ivan = Courier("Iven", "Food", 1, Location(9, 9))
      val couriers = Seq(kirill, andrey, ivan)

      val courier = sut.dispatch(order, couriers)
      courier shouldBe kirill
      order.courierId shouldBe Some(kirill.id)
      order.status shouldBe Assigned
    }
  }
}
