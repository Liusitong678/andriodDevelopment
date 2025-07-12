package com.example.sneaknest.model


data class CartItem(
    var productItem: Product? = null,
    var totalPrice: Double = 0.0,
    var totalQuantity: Int = 1
) : java.io.Serializable


