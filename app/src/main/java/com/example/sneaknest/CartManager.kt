package com.example.sneaknest

import com.example.sneaknest.model.CartItem

object CartManager {

    private val cartItems = mutableListOf<CartItem>()

    fun getCartItems(): List<CartItem> = cartItems

    fun addToCart(item: CartItem) {
        val existing = cartItems.find {
            it.productItem?.productId == item.productItem?.productId
        }

        if (existing != null) {
            existing.totalQuantity += item.totalQuantity
        } else {
            cartItems.add(item)
        }
    }

    fun removeFromCart(productId: String) {
        cartItems.removeIf {
            it.productItem?.productId == productId
        }
    }

    fun clearCart() = cartItems.clear()

    val totalPrice: Double
        get() = cartItems.sumOf {
            (it.productItem?.price ?: 0.0) * it.totalQuantity
        }

    fun updateItemQuantity(productId: String, quantity: Int) {
        cartItems.find {
            it.productItem?.productId == productId
        }?.totalQuantity = quantity
    }

    val cartItemCount: Int
        get() = cartItems.size
}
