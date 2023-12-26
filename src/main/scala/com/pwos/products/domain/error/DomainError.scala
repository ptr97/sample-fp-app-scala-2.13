package com.pwos.products.domain.error

trait DomainError {
  def code: String
  def message: String
}
