package com.example.easyshop.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.easyshop.AppUtil
import com.example.easyshop.model.ProductsModel
import com.example.easyshop.viewmodel.ProductsViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun OrderedItemFromId(
    product: ProductsModel,
    modifier: Modifier = Modifier,
    productsViewModel: ProductsViewModel = viewModel()
) {
    var userRating by remember { mutableIntStateOf(0) }
    val userId = Firebase.auth.currentUser?.uid
    var showReviewDialog by remember { mutableStateOf(false) }
    var reviewText by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Fetch existing rating
    LaunchedEffect(product.id) {
        userId?.let {
            productsViewModel.getUserRatingForProduct(product.id, it) { rating ->
                if (rating != null) userRating = rating
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.images.first(),
                contentDescription = "Product image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Delivered", fontSize = 14.sp)
                Text(
                    text = product.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black.copy(0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // ⭐ Rating Stars
                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= userRating) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Star $i",
                            tint = if (i <= userRating) Color(0xFFFFD700) else Color.Gray,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(2.dp)
                                .clickable {
                                    userRating = i
                                    productsViewModel.saveRatingToFirestore(
                                        productId = product.id,
                                        userId = userId,
                                        userRating = i,
                                        onSuccess = {
                                            AppUtil.showToast(context, "Rating saved!")
                                        },
                                        onError = { e ->
                                            Log.w("Rating Error", e)
                                            AppUtil.showToast(context, "Error saving rating: ${e.message}")
                                        }
                                    )
                                }
                        )
                    }
                }

                if (userRating > 0) {
                    Text(
                        text = "Write a Review",
                        fontSize = 14.sp,
                        color = Color.Blue,
                        modifier = Modifier.clickable {
                            showReviewDialog = true
                        }
                    )
                } else {
                    Text(
                        text = "Rate this product now",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            thickness = 1.dp,
            color = Color.LightGray
        )

        // 📝 Review Dialog
        if (showReviewDialog) {
            AlertDialog(
                onDismissRequest = { showReviewDialog = false },
                confirmButton = {
                    Text(
                        text = "Submit",
                        color = Color.Blue,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                if (userId != null) {
                                    productsViewModel.saveReviewToFirestore(
                                        productId = product.id,
                                        userId = userId,
                                        comment = reviewText,
                                        onSuccess = {
                                            showReviewDialog = false
                                            reviewText = ""
                                            AppUtil.showToast(context, "Review submitted")
                                        },
                                        onError = { e ->
                                            AppUtil.showToast(context, "Error: ${e.message}")
                                        }
                                    )
                                }
                            }
                    )
                },
                dismissButton = {
                    Text(
                        text = "Cancel",
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                showReviewDialog = false
                            }
                    )
                },
                title = { Text(text = "Write a Review") },
                text = {
                    Column {
                        Text(text = "You rated this product $userRating stars.")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = reviewText,
                            onValueChange = { reviewText = it },
                            placeholder = { Text("Write your review here...") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            )
        }
    }
}
