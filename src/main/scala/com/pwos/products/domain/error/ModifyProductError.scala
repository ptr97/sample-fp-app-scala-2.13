package com.pwos.products.domain.error

sealed trait ModifyProductError extends DomainError

object ModifyProductError {

  final case object ProductDoesNotExist extends ModifyProductError {
    override val code: String    = "modify-offer-error-001"
    override val message: String = "Product does not exist."
  }

  final case object NotEnoughOffersToCloseAggregation extends ModifyProductError {
    override val code: String    = "modify-offer-error-002"
    override val message: String = "Not enough offers to close product aggregation."
  }

}
