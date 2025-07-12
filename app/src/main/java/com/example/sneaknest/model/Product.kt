package com.example.sneaknest.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Product(
    val productId: String = "",
    val productName: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val productImageUrl: String = "",
    val productImageDetailUrl: String = "",
    val brand: String = "",
    val category: String = ""
) : java.io.Serializable

