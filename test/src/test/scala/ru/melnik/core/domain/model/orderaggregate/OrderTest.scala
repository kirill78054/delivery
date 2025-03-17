package ru.melnik.core.domain.model.orderaggregate

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import ru.melnik.core.domain.model.courieraggregate.Courier
import ru.melnik.core.domain.model.orderaggregate.OrderStatus.{Assigned, Completed, Created}
import ru.melnik.core.domain.sharedkernel.Location

import java.util.UUID

class OrderTest extends AnyWordSpec with Matchers {

  "Order invariants" should {
    "create an instance with valid id and location" in {
      val id = UUID.randomUUID()
      val result = Order(id, Location.minLocation)

      result should not be null
      result.id shouldBe id
      result.location should not be null
      result.location shouldBe Location.minLocation
      result.status shouldBe Created
      result.courierId shouldBe None
    }

    "create an instance with null id" in {
      val exception = intercept[IllegalArgumentException](Order(null, Location.minLocation))
      exception.getMessage shouldBe "requirement failed: id is null"
    }

    "create an instance with null location" in {
      val exception = intercept[IllegalArgumentException](Order(UUID.randomUUID(), null))
      exception.getMessage shouldBe "requirement failed: location is null"
    }
  }

  val courier: Courier = Courier("Kirill", "Car", 2, Location.minLocation)
  "Order assignCourier" should {
    "use assignCourier with valid newCourierId" in {
      val id = UUID.randomUUID()
      val order = Order(id, Location.minLocation)
      order.assignCourier(courier)

      order should not be null
      order.id shouldBe id
      order.location should not be null
      order.location shouldBe Location.minLocation
      order.status shouldBe Assigned
      order.courierId shouldBe Some(courier.id)
    }

    "use assignCourier with null newCourierId" in {
      val id = UUID.randomUUID()
      val order = Order(id, Location.minLocation)
      val exception = intercept[IllegalArgumentException](order.assignCourier(null))
      exception.getMessage shouldBe "requirement failed: courier is null"
    }

    "use assignCourier with assign order" in {
      val id = UUID.randomUUID()
      val order = Order(id, Location.minLocation)
      order.assignCourier(courier)
      val exception = intercept[IllegalArgumentException](order.assignCourier(courier))
      exception.getMessage shouldBe "requirement failed: order status is not Created"
    }
  }

  "Order finishOrder" should {
    val id = UUID.randomUUID()

    "use finishOrder with valid status" in {
      val order = Order(id, Location.minLocation)
      order.assignCourier(courier)
      order.finishOrder()

      order should not be null
      order.id shouldBe id
      order.location should not be null
      order.location shouldBe Location.minLocation
      order.status shouldBe Completed
      order.courierId shouldBe Some(courier.id)
    }

    "use finishOrder with not valid status" in {
      val exception = intercept[IllegalArgumentException](Order(id, Location.minLocation).finishOrder())
      exception.getMessage shouldBe "requirement failed: order did`t assigned"
    }
  }

}
