package ru.melnik.core.domain.model.orderaggregate

sealed abstract class OrderStatus(val name: String)

object OrderStatus {
  case object Created extends OrderStatus("created")
  case object Assigned extends OrderStatus("assigned")
  case object Completed extends OrderStatus("completed")

  def apply(name: String): OrderStatus = name.toLowerCase match {
    case "created" => Created
    case "assigned" => Assigned
    case "completed" => Completed
    case _ => throw new IllegalArgumentException(s"Order status not match, status: $name")
  }
}