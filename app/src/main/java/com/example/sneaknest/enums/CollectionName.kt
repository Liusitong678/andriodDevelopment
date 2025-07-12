package com.example.sneaknest.enums

enum class CollectionName(val collectionName: String, val desc: String) {
    PRODUCTS("products", "The collections of product"),
    CART_ITEMS("cartItems", "The collections of cart item");

    override fun toString(): String {
        return collectionName
    }
}
