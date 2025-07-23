package com.example.easyshop.viewmodel

import androidx.lifecycle.ViewModel
import com.example.easyshop.model.CheckoutModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class CheckoutViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    fun placeOrder(
        checkoutModel: CheckoutModel,
        onResult: (Boolean, String?) -> Unit
    ) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val orderRef = firestore.collection("users")
                .document(userId)
                .collection("orders")
                .document() // Auto-generated ID

            orderRef.set(checkoutModel.copy(userId = userId))
                .addOnSuccessListener {
                    onResult(true, null)
                }
                .addOnFailureListener {
                    onResult(false, it.localizedMessage)
                }
        } else {
            onResult(false, "User not logged in.")
        }
    }
}
