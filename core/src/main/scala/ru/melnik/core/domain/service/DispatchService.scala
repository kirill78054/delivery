package ru.melnik.core.domain.service

import ru.melnik.core.domain.model.courieraggregate.Courier
import ru.melnik.core.domain.model.orderaggregate.Order

class DispatchService extends IDispatchService {
  override def dispatch(order: Order, couriers: Seq[Courier]): Courier = {
    val courier = couriers
      .map(courier => (courier, courier.calcTimeToTarget(order.location)))
      .minBy(_._2)
      ._1

    order.assignCourier(courier)
    courier
  }
}
