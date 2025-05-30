package com.example.easyshop.pages

import android.R
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.easyshop.model.ProductsModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

@Composable
fun ProductDetailsPage(productId: String?) {
    if (productId.isNullOrEmpty()) {
        Text("Invalid product ID")
        return
    }

    val productDetails = remember { mutableStateOf<ProductsModel?>(null) }

    LaunchedEffect(productId) {
        try {
            val snapshot = Firebase.firestore
                .collection("data")
                .document("stock")
                .collection("products")
                .document(productId)
                .get()
                .await()

            productDetails.value = snapshot.toObject(ProductsModel::class.java)
        } catch (e: Exception) {
            Log.e("ProductDetails", "Error fetching product: ${e.message}")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(18.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        productDetails.value?.let { product ->

            val pagerState = rememberPagerState(0) {
                product.images.size
            }

            HorizontalPager(pagerState) {page ->
                AsyncImage(
                    model = product.images[page],
                    contentDescription = "Product images",
                    modifier = Modifier.height(300.dp).fillMaxWidth()
                )
            }

            Text(
                text = product.title,
                fontSize = 16.sp,
                modifier = Modifier.padding(12.dp),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start

            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
               Row(
                  verticalAlignment = Alignment.CenterVertically
               ){
                   Text(
                       text = "$${product.price}",
                       modifier = Modifier.padding(6.dp),
                       textDecoration = TextDecoration.LineThrough,
                   )

                   Text(
                       text = "$${product.actualPrice}",
                       modifier = Modifier.padding(6.dp),
                       style = MaterialTheme.typography.titleMedium
                   )
               }

                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favourite icon",
                        tint = Color.Black.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    onClick = {},
                    elevation = ButtonDefaults.buttonElevation()
                ) {
                    Text(
                        text = "Add to Cart",
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )

                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "ShoppingCart icon"
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "About the product:",
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Highlights",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))
            product.otherDetails.forEach{(k, v) ->
                Row {
                    Text(text = "$k: ", style = MaterialTheme.typography.titleMedium)
                    Text(text = v, style = MaterialTheme .typography.bodyLarge)
                }
            }

        } ?: Text("Loading product details...")
    }
}

