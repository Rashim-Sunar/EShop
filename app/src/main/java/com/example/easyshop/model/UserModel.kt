package com.example.easyshop.model

data class UserModel(
    val name: String = "",
    val email: String = "",
    val uid: String = "",
    val cartItems: Map<String, Long> = emptyMap(),
    val favorites: List<String> = emptyList()
)
