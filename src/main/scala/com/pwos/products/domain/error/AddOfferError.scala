package com.pwos.products.domain.error

sealed trait AddOfferError extends DomainError

object AddOfferError {

  final case object ProductDoesNotExist extends AddOfferError {
    override val code: String    = "add-offer-error-001"
    override val message: String = "Product does not exist."
  }

  final case object ProductIsClosed extends AddOfferError {
    override val code: String    = "add-offer-error-002"
    override val message: String = "Product is in closed state. You cannot add new offer to it."
  }

}
