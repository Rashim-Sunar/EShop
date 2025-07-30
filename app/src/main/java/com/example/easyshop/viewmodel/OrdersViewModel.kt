package com.example.easyshop.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.easyshop.model.CheckoutModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class OrdersViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    var orders by mutableStateOf<List<CheckoutModel>>(emptyList())
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
                    orders = value.toObjects(CheckoutModel::class.java)
                }
            }
    }
}
