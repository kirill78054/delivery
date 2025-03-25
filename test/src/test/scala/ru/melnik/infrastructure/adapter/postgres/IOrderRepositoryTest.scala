package ru.melnik.infrastructure.adapter.postgres

import cats.effect.unsafe.implicits.global
import doobie.implicits._
import doobie.postgres.implicits._
import ru.melnik.core.domain.model.courieraggregate.Courier
import ru.melnik.core.domain.model.orderaggregate.Order
import ru.melnik.core.domain.sharedkernel.Location

import java.util.UUID

class IOrderRepositoryTest extends PostgreSQLContextTest {

  "addOrder" in {
    withTransaction(tr => {
      val repository = new IOrderRepository(tr)
      val order = Order(UUID.randomUUID(), Location.minLocation)
      repository.addOrder(order)

      val maybeOrder = sql"SELECT id,location_x,location_y,order_status, courier_id FROM delivery_order where id = ${order.id}"
        .query[(UUID, Int, Int, String, Option[UUID])]
        .option
        .transact(tr)
        .unsafeRunSync()

      maybeOrder.isDefined shouldBe true
      maybeOrder shouldBe Some(order.id, order.location.x, order.location.y, order.status.name, order.courierId)
    })
  }

  "updateOrder" in {
    withTransaction(tr => {
      val repository = new IOrderRepository(tr)
      val order = Order(UUID.randomUUID(), Location.minLocation)
      repository.addOrder(order)
      val courier = Courier("Kirill", "Car", 2, Location.minLocation)
      order.assignCourier(courier)

      repository.updateOrder(order)

      val maybeOrder = sql"SELECT id,location_x,location_y,order_status, courier_id FROM delivery_order where id = ${order.id}"
        .query[(UUID, Int, Int, String, Option[UUID])]
        .option
        .transact(tr)
        .unsafeRunSync()

      maybeOrder.isDefined shouldBe true
      maybeOrder shouldBe Some(order.id, order.location.x, order.location.y, order.status.name, order.courierId)
    })
  }

  "findOrderById" in {
    withTransaction(tr => {
      val repository = new IOrderRepository(tr)
      val order = Order(UUID.randomUUID(), Location.minLocation)
      repository.addOrder(order)

      val maybeOrder = repository.findOrderById(order.id)
      order.id shouldBe maybeOrder.id
      order.location.x shouldBe maybeOrder.location.x
      order.location.y shouldBe maybeOrder.location.y
      order.status.name shouldBe maybeOrder.status.name
      order.courierId shouldBe maybeOrder.courierId
    })
  }

  "findRandomCreatedOrder" in {
    withTransaction(tr => {
      val repository = new IOrderRepository(tr)
      val order1 = Order(UUID.randomUUID(), Location.minLocation)
      val order2 = Order(UUID.randomUUID(), Location.minLocation)
      repository.addOrder(order1)
      repository.addOrder(order2)

      val orders = repository.findRandomCreatedOrder()
      orders.isDefined shouldBe true
    })
  }

  "findAllAssignerOrder" in {
    withTransaction(tr => {
      val repository = new IOrderRepository(tr)
      val order1 = Order(UUID.randomUUID(), Location.minLocation)
      order1.assignCourier(Courier("Kirill", "Car", 2, Location.minLocation))
      val order2 = Order(UUID.randomUUID(), Location.minLocation)
      order2.assignCourier(Courier("Andrey", "Car", 2, Location.minLocation))
      repository.addOrder(order1)
      repository.addOrder(order2)

      val orders = repository.findAllAssignerOrder()
      orders.size shouldBe 2
      orders shouldBe Seq(order1, order2)
    })
  }

}
