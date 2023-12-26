package com.pwos.products.exceptions

case class AppInitializationException(message: String) extends RuntimeException(message)
