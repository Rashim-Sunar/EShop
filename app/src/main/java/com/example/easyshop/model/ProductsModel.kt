package com.example.easyshop.model

data class ProductsModel(
    val id: String = "",
    val title: String = "",
    val price: String = "",
    val actualPrice: String = "",
    val category:  String = "",
    val description: String = "",
    val images: List<String> = emptyList()
)
