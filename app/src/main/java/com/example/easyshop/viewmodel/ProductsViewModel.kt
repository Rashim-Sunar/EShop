package com.example.easyshop.viewmodel

import androidx.lifecycle.ViewModel
import com.example.easyshop.model.ProductsModel
import com.example.easyshop.model.ReviewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ProductsViewModel : ViewModel() {
    private val firestore = Firebase.firestore

    fun saveRatingToFirestore(
        productId: String,
        userId: String?,
        userRating: Int,
        comment: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (userId == null) {
            onError(IllegalArgumentException("User ID cannot be null"))
            return
        }

        val productRef = firestore.collection("data").document("stock")
            .collection("products").document(productId)

        val userReviewRef = productRef.collection("reviews").document(userId)

        firestore.runTransaction { transaction ->
            val productSnapshot = transaction.get(productRef)
            val reviewSnapshot = transaction.get(userReviewRef)

            val product = productSnapshot.toObject(ProductsModel::class.java)
                ?: throw Exception("Product not found")

            val currentBreakdown = product.ratingBreakdown.toMutableMap()
            var currentTotal = product.totalRatings

            // If the user has rated before, remove old rating first
            if (reviewSnapshot.exists()) {
                val previousReview = reviewSnapshot.toObject(ReviewModel::class.java)
                val oldRating = previousReview?.rating ?: 0
                currentBreakdown["$oldRating"] = (currentBreakdown["$oldRating"] ?: 1) - 1
                currentTotal -= 1
            }

            // Now add the new rating
            currentBreakdown["$userRating"] = (currentBreakdown["$userRating"] ?: 0) + 1
            val newTotal = currentTotal + 1

            // Calculate new average
            var totalScore = 0
            for ((stars, count) in currentBreakdown) {
                val starValue = stars.toIntOrNull() ?: 0
                totalScore += starValue * count
            }
            val newAverage = if (newTotal > 0) totalScore.toDouble() / newTotal else 0.0

            // Save the new/updated review
            val newReview = ReviewModel(
                userId = userId,
                rating = userRating,
                comment = comment,
                timestamp = System.currentTimeMillis()
            )
            transaction.set(userReviewRef, newReview)  // using userId as doc ID ensures one review per user

            // Update product fields
            transaction.update(productRef, mapOf(
                "averageRating" to newAverage,
                "totalRatings" to newTotal,
                "ratingBreakdown" to currentBreakdown
            ))
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { e ->
            onError(e)
        }
    }

    // Fetch the rating for given product by current user
    fun getUserRatingForProduct(
        productId: String,
        userId: String,
        onResult: (Int?) -> Unit
    ) {
        val reviewRef = Firebase.firestore
            .collection("data").document("stock")
            .collection("products").document(productId)
            .collection("reviews").document(userId)

        reviewRef.get()
            .addOnSuccessListener { snapshot ->
                val rating = snapshot.getLong("rating")?.toInt()
                onResult(rating)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

}
