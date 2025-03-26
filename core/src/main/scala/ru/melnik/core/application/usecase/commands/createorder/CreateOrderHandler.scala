package ru.melnik.core.application.usecase.commands.createorder

import doobie.postgres.implicits._
import ru.melnik.core.domain.model.orderaggregate.Order
import ru.melnik.core.domain.sharedkernel.Location
import ru.melnik.core.port.{IOrderRepository, IUnitOfWork}

import java.util.UUID
import scala.util.Try

class CreateOrderHandler(uw: IUnitOfWork, orderRepository: IOrderRepository) {
  require(uw != null)
  require(orderRepository != null)

  def handle(command: CreateOrderCommand): Try[Unit] = Try {
    orderRepository.addOrder(Order(command.basketId, Location.createRandom()))
    uw.apply()
  }
}

case class CreateOrderCommand private(basketId: UUID, street: String)

object CreateOrderCommand {
  def apply(basketId: UUID, street: String): CreateOrderCommand = {
    require(basketId != null, "Basket id must not be null")
    require(street != null && street.nonEmpty, "Street must not be empty")
    new CreateOrderCommand(basketId, street)
  }
}