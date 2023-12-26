package com.pwos.products.domain

import eu.timepit.refined.types.string.NonEmptyString

final case class ProductCode(value: NonEmptyString)
