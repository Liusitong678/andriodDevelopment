package com.example.sneaknest.listener

import com.example.sneaknest.model.Product

interface OnProductClickListener {
    fun onProductItemClick(product: Product?)
}
