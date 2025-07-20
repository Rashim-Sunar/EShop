package com.example.easyshop

import android.content.Context
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
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
                val cartItem = it.result.get("cartItems") as? Map<String, Long> ?: emptyMap() // Map productId to quantity
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

    fun updateCartQuantity(productId: String?, quantity: Long, context: Context) {
        val userId = Firebase.auth.currentUser?.uid
        if (productId.isNullOrEmpty() || userId.isNullOrEmpty()) return

        val userDoc = Firebase.firestore.collection("users").document(userId)

        userDoc.update("cartItems.$productId", quantity)
            .addOnSuccessListener {
                showToast(context, "Quantity updated in cart!")
            }
            .addOnFailureListener {
                showToast(context, "Failed to update quantity.")
            }
    }

    fun removeFromCart(productId: String?, context: Context){
        val userId = Firebase.auth.currentUser?.uid
        if(productId.isNullOrEmpty() || userId.isNullOrEmpty()) return

        val userDoc = Firebase.firestore.collection("users").document(userId)

        userDoc.update("cartItems.$productId", FieldValue.delete())
            .addOnSuccessListener {
                showToast(context, "Item removed from the cart")
            }
            .addOnFailureListener {
                showToast(context, "Failed to remove item from cart!")
            }
    }

    fun calculateTotalPrice(price: String, quantity: Long, plateFormFee: Int = 0) : String{
        val cleanPrice = price.replace(",", "")
        val price = cleanPrice.toIntOrNull() ?: 0 // Safely parse to int
        val total = price*quantity

        // Format back with commas
        return "%,d".format(total + plateFormFee)
    }

    fun calculateDiscount(price: String, actualPrice: String): String {
        val cleanPrice = price.replace(",", "")
        val cleanActualPrice = actualPrice.replace(",", "")

        val priceInt = cleanPrice.toIntOrNull() ?: return "0%"
        val actualPriceInt = cleanActualPrice.toIntOrNull() ?: return "0%"

        if (actualPriceInt >= priceInt) return "0%" // No discount

        val discount = priceInt - actualPriceInt
        val percentage = (discount.toDouble() / priceInt.toDouble()) * 100

        return "${percentage.toInt()}%"  // Rounded down to nearest integer
    }

    fun calculateDiscountAmount(price: String, actualPrice: String): String{
        val cleanPrice = price.replace(",","").toIntOrNull() ?: return "0"
        val cleanActualPrice = actualPrice.replace(",", "").toIntOrNull() ?: return "0"

        return "${cleanPrice - cleanActualPrice}"
    }
}