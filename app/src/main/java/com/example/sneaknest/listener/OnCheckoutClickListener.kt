package com.example.sneaknest.listener

import com.example.sneaknest.model.CartItem

interface OnCheckoutClickListener {
    fun onCheckoutClick(cartItems: List<CartItem>)
}
