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
import com.google.firebase.firestore.FieldPath

class OrdersViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    var orders by mutableStateOf<List<CheckoutModel>>(emptyList())
        private set

    var productMap by mutableStateOf<Map<String, ProductsModel>>(emptyMap())
        private set

    init {
        fetchOrders()
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
                    val fetchedOrders = value.toObjects(CheckoutModel::class.java)
                    orders = fetchedOrders
                    fetchProductsForOrders(fetchedOrders)
                }
            }
    }

    private fun fetchProductsForOrders(orderList: List<CheckoutModel>) {
        val productIds = orderList.map { it.item[0].productId }.distinct()

        val allProductDocs = mutableMapOf<String, ProductsModel>()

        val chunks = productIds.chunked(10) // Firestore allows max 10 for 'whereIn'
        chunks.forEach { chunk ->
            firestore.collection("data").document("stock").collection("products")
                .whereIn(FieldPath.documentId(), chunk)
                .get()
                .addOnSuccessListener { snapshot ->
                    for (doc in snapshot.documents) {
                        val product = doc.toObject(ProductsModel::class.java)
                        if (product != null) {
                            allProductDocs[doc.id] = product
                        }
                    }
                    // Update only once all chunks have been processed
                    productMap = allProductDocs.toMap()
                }
        }
    }
}
