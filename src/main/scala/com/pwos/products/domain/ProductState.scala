package com.pwos.products.domain

import enumeratum.values.StringDoobieEnum
import enumeratum.values.StringEnum
import enumeratum.values.StringEnumEntry

sealed abstract class ProductState(val value: String) extends StringEnumEntry

object ProductState extends StringEnum[ProductState] with StringDoobieEnum[ProductState] {

  final case object Open   extends ProductState("open")
  final case object Closed extends ProductState("closed")

  override def values: IndexedSeq[ProductState] = findValues

}
