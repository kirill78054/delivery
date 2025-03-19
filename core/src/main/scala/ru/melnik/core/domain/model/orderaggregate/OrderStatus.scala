package ru.melnik.core.domain.model.orderaggregate

sealed abstract class OrderStatus(val name: String)

object OrderStatus {
  case object Created extends OrderStatus("created")
  case object Assigned extends OrderStatus("assigned")
  case object Completed extends OrderStatus("completed")
}