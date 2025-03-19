package ru.melnik.core.domain.model.orderaggregate

import ru.melnik.core.domain.model.courieraggregate.Courier
import ru.melnik.core.domain.model.orderaggregate.OrderStatus.{Assigned, Completed, Created}
import ru.melnik.core.domain.sharedkernel.Location

import java.util.UUID

class Order private(val id: UUID, val location: Location, var status: OrderStatus, var courierId: Option[UUID]) {

  def assignCourier(newCourier: Courier): Unit = {
    require(newCourier != null, "courier is null")
    require(status == Created, "order status is not Created")

    this.status = Assigned
    this.courierId = Some(newCourier.id)
  }

  def finishOrder(): Unit = {
    require(courierId.isDefined, "order did`t assigned")
    require(status == Assigned, "order status is not Assigned")

    this.status = Completed
  }

}

object Order {

  def apply(id: UUID, location: Location): Order = Order(id, location, Created, None)

  private def apply(id: UUID, location: Location, status: OrderStatus, courierId: Option[UUID]): Order = {
    require(id != null, "id is null")
    require(location != null, "location is null")
    require(status != null, "status is null")
    require(courierId != null, "courierId is null")
    new Order(id, location, status, courierId)
  }

}
