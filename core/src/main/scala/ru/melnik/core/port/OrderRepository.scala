package ru.melnik.core.port

import ru.melnik.core.domain.model.orderaggregate.Order

import java.util.UUID

trait OrderRepository {
  def addOrder(order: Order): Unit
  def updateOrder(order: Order): Unit
  def findOrderById(orderId: UUID): Order
  def findRandomCreatedOrder(): Option[Order]
  def findAllAssignerOrder(): Seq[Order]
}