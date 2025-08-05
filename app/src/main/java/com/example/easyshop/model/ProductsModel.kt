package com.example.easyshop.model

data class ProductsModel(
    val id: String = "",
    val title: String = "",
    val price: String = "",
    val actualPrice: String = "",
    val category: String = "",
    val description: String = "",
    val images: List<String> = emptyList(),
    val otherDetails: Map<String, String> = emptyMap(),

    val averageRating: Double = 0.0,
    val totalRatings: Int = 0,
    val ratingBreakdown: Map<String, Int> = mapOf("1" to 0, "2" to 0, "3" to 0, "4" to 0, "5" to 0)
)
