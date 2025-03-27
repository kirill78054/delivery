package ru.melnik.infrastructure.adapter.postgres

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor.Aux
import ru.melnik.core.domain.model.orderaggregate.{Order, OrderStatus}
import ru.melnik.core.domain.sharedkernel.Location
import ru.melnik.core.port.OrderRepository

import java.util.UUID

class IOrderRepository(tr: Aux[IO, Unit]) extends OrderRepository {

  override def addOrder(order: Order): Unit = {
    sql"""
      insert into delivery_order (id,location_x,location_y,order_status, courier_id)
      values (${order.id}, ${order.location.x}, ${order.location.y}, ${order.status.name}, ${order.courierId})
    """
      .update
      .run
      .transact(tr)
      .unsafeRunSync()
  }

  override def updateOrder(order: Order): Unit = {
    sql"""
      update delivery_order set
      location_x = ${order.location.x}
      , location_y = ${order.location.y}
      , order_status = ${order.status.name}
      , courier_id = ${order.courierId}
      where id = ${order.id}
    """
      .update
      .run
      .transact(tr)
      .unsafeRunSync()
  }

  override def findOrderById(orderId: UUID): Order = {
    sql"SELECT id,location_x,location_y,order_status, courier_id FROM delivery_order where id = $orderId"
      .query[(UUID, Int, Int, String, Option[UUID])]
      .option
      .transact(tr)
      .unsafeRunSync()
      .map(order => {
        Order.fromDb(order._1, Location(order._2, order._3), OrderStatus(order._4), order._5)
      }).getOrElse(throw new NoSuchElementException(s"Order not found, id = $orderId"))
  }

  override def findRandomCreatedOrder(): Option[Order] = {
    sql"SELECT id,location_x,location_y,order_status, courier_id FROM delivery_order where order_status = 'created' limit 1"
      .query[(UUID, Int, Int, String, Option[UUID])]
      .option
      .transact(tr)
      .unsafeRunSync()
      .map(order => {
        Order.fromDb(order._1, Location(order._2, order._3), OrderStatus(order._4), order._5)
      })
  }

  override def findAllAssignerOrder(): Seq[Order] = {
    sql"SELECT id,location_x,location_y,order_status, courier_id FROM delivery_order where order_status = 'assigned'"
      .query[(UUID, Int, Int, String, Option[UUID])]
      .to[List]
      .transact(tr)
      .unsafeRunSync()
      .map(order => {
        Order.fromDb(order._1, Location(order._2, order._3), OrderStatus(order._4), order._5)
      })

  }
}
