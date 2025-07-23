package com.example.easyshop.model

data class CartItemModel(
    val productId: String = "",
    val name: String = "",
    val price: String = "0.0",
    val quantity: Int = 1
)
