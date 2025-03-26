package ru.melnik.core.application.usecase.commands.setcourier

import doobie.postgres.implicits._
import ru.melnik.core.domain.service.IDispatchService
import ru.melnik.core.port.{ICourierRepository, IOrderRepository, IUnitOfWork}

import scala.util.Try

class SetCourierHandler(uw: IUnitOfWork,
                        dispatchService: IDispatchService,
                        orderRepository: IOrderRepository,
                        courierRepository: ICourierRepository
                       ) {
  require(uw != null)
  require(dispatchService != null)
  require(orderRepository != null)
  require(courierRepository != null)

  def handle(): Try[Unit] = Try {
    val couriers = courierRepository.findAllFreeCourier()

    if (couriers.nonEmpty) {
      orderRepository.findRandomCreatedOrder()
        .map(order => {
          val courier = dispatchService.dispatch(order, couriers)

          courier.setBusy()
          order.assignCourier(courier)

          orderRepository.updateOrder(order)
          courierRepository.updateCourier(courier)
          uw.apply()
        })
    }


  }
}