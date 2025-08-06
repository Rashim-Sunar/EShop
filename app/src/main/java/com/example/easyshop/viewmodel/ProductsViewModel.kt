package com.example.easyshop.viewmodel

import androidx.lifecycle.ViewModel
import com.example.easyshop.model.ProductsModel
import com.example.easyshop.model.ReviewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ProductsViewModel : ViewModel() {
    private val firestore = Firebase.firestore

    // Save or update rating only (without comment)
    fun saveRatingToFirestore(
        productId: String,
        userId: String?,
        userRating: Int,
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

            // Remove previous rating if exists
            if (reviewSnapshot.exists()) {
                val previousReview = reviewSnapshot.toObject(ReviewModel::class.java)
                val oldRating = previousReview?.rating ?: 0
                currentBreakdown["$oldRating"] = (currentBreakdown["$oldRating"] ?: 1) - 1
                currentTotal -= 1
            }

            // Add new rating
            currentBreakdown["$userRating"] = (currentBreakdown["$userRating"] ?: 0) + 1
            val newTotal = currentTotal + 1

            // Calculate new average
            var totalScore = 0
            for ((stars, count) in currentBreakdown) {
                val starValue = stars.toIntOrNull() ?: 0
                totalScore += starValue * count
            }
            val newAverage = if (newTotal > 0) totalScore.toDouble() / newTotal else 0.0

            // Preserve existing comment if exists
            val existingComment = reviewSnapshot.toObject(ReviewModel::class.java)?.comment ?: ""

            val newReview = ReviewModel(
                userId = userId,
                rating = userRating,
                comment = existingComment,
                timestamp = System.currentTimeMillis()
            )
            transaction.set(userReviewRef, newReview)

            // Update product ratings
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

    // Save or update only the comment (no effect on rating calculations)
    fun saveReviewToFirestore(
        productId: String,
        userId: String?,
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

        userReviewRef.get().addOnSuccessListener { snapshot ->
            val currentReview = snapshot.toObject(ReviewModel::class.java)
            if (currentReview != null) {
                val updatedReview = currentReview.copy(
                    comment = comment,
                    timestamp = System.currentTimeMillis()
                )
                userReviewRef.set(updatedReview)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onError(it) }
            } else {
                // No existing rating yet; optionally, you can avoid saving a comment without a rating
                onError(Exception("Please rate the product before adding a comment."))
            }
        }.addOnFailureListener {
            onError(it)
        }
    }

    fun getUserRatingForProduct(
        productId: String,
        userId: String,
        onResult: (Int?) -> Unit
    ) {
        val reviewRef = firestore
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

