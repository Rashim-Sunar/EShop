package com.example.easyshop

import android.content.Context
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

object AppUtil {
    fun showToast(context : Context, message: String?){
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    fun addToCart(productId: String?, context: Context){
        val userDoc = Firebase.firestore.collection("users")
            .document(Firebase.auth.currentUser!!.uid)

        userDoc.get().addOnCompleteListener { it ->
            if (it.isSuccessful){
                val cartItem = it.result.get("cartItems") as? Map<String, Long> ?: emptyMap()
                val currentQuantity = cartItem[productId] ?: 0
                val updatedQuantity = currentQuantity + 1

                userDoc.update(mapOf<String, Long>("cartItems.$productId" to updatedQuantity))
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            showToast(context, "Item added to cart successfully!")
                        }else{
                            showToast(context, "Failed to add item to the cart!")
                        }
                    }
            }
        }
    }
}