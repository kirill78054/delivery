package ru.melnik.core.domain.service

import ru.melnik.core.domain.model.courieraggregate.Courier
import ru.melnik.core.domain.model.orderaggregate.Order

trait IDispatchService {

  def dispatch(order: Order, couriers: Seq[Courier]): Courier

}
