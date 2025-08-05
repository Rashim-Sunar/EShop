package com.example.easyshop.model

data class ReviewModel(
    val userId: String? = "",
    val rating: Int = 0,
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

