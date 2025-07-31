package com.example.easyshop.viewmodel

import com.example.easyshop.model.CheckoutModel
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.easyshop.model.ProductsModel

class OrdersViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    var orders by mutableStateOf<List<CheckoutModel>>(emptyList())
        private set

    var productMap by mutableStateOf<Map<String, ProductsModel>>(emptyMap())
        private set

    init {
        fetchProducts()  // fetch this before or along with orders
        fetchOrders()
    }

    private fun fetchProducts() {
        firestore.collection("data").document("stock")
            .collection("products")
            .get()
            .addOnSuccessListener { result ->
                val map = result.documents.associate { doc ->
                    val product = doc.toObject(ProductsModel::class.java)
                    doc.id to product!!
                }
                productMap = map
            }
    }

    private fun fetchOrders() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(uid)
            .collection("orders")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                if (value != null) {
                    orders = value.toObjects(CheckoutModel::class.java)
                }
            }
    }
}
