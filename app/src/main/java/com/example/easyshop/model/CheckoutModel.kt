package com.example.easyshop.model

data class CheckoutModel(
    val userId: String = "",
    val item: List<CartItemModel> = listOf(),
    val totalAmount: String = "0.0",
    val address: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

