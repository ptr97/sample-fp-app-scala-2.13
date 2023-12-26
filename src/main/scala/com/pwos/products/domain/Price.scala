package com.pwos.products.domain

import cats.kernel.Monoid
import eu.timepit.refined.auto.autoUnwrap
import eu.timepit.refined.types.numeric.NonNegBigDecimal

final case class Price(value: NonNegBigDecimal) {

  def min(other: Price): Price = Price(NonNegBigDecimal.unsafeFrom(value.min(other.value)))

  def max(other: Price): Price = Price(NonNegBigDecimal.unsafeFrom(value.max(other.value)))

  def +(other: Price): Price = Price(NonNegBigDecimal.unsafeFrom(value + other.value))

}

object Price {

  val zero: Price = Price(NonNegBigDecimal(BigDecimal(0)))

  implicit val priceMonoidInstance: Monoid[Price] = new Monoid[Price] {
    override def empty: Price                       = zero
    override def combine(x: Price, y: Price): Price = Price(NonNegBigDecimal.unsafeFrom(x.value + y.value))
  }

}
