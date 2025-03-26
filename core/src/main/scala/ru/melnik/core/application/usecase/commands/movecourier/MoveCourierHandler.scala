package ru.melnik.core.application.usecase.commands.movecourier

import doobie.postgres.implicits._
import ru.melnik.core.domain.service.IDispatchService
import ru.melnik.core.port.{ICourierRepository, IOrderRepository, IUnitOfWork}

import scala.util.Try

class MoveCourierHandler(
  uw: IUnitOfWork,
  dispatchService: IDispatchService,
  orderRepository: IOrderRepository,
  courierRepository: ICourierRepository
) {
  require(uw != null)
  require(dispatchService != null)
  require(orderRepository != null)
  require(courierRepository != null)

  def handle(): Try[Unit] = Try {
    orderRepository.findAllAssignerOrder()
      .filter(_.courierId.isDefined)
      .foreach(order => {
        val courier = courierRepository.findCourierById(order.courierId.get)
        courier.move(order.location)
        if (order.location == courier.location) {
          order.finishOrder()
          courier.setFree()
          orderRepository.updateOrder(order)
        }
        courierRepository.updateCourier(courier)
        uw.apply()
      })
  }
}