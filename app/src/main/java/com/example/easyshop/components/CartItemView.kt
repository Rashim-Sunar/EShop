package com.example.easyshop.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.easyshop.model.ProductsModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

@Composable
fun CartItemView(productId: String, quantity: Long){

    val productDetail = remember {
        mutableStateOf<ProductsModel?>(null)
    }

    LaunchedEffect(productId) {
        try{
            val snapshot = Firebase.firestore.collection("data")
                .document("stock")
                .collection("products")
                .document(productId)
                .get()
                .await()

            val result = snapshot.toObject(ProductsModel::class.java)
            if(result != null){
                productDetail.value = result
            }else{
                Log.d("CartItemView", "Fetched null product detail...")
            }
        }catch (e : Exception){
            Log.d("CartItemView", "Error fetching product detail with productId: ${e.message}")
        }
    }

    productDetail.value?.let { product ->
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(50.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(){
                    // Product Image
                    AsyncImage(
                        model = product.images.firstOrNull(),
                        contentDescription = "Product Image",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    //Quantity
                    Text(
                        text = "Qty: $quantity",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Product Title and Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp)
                    )

                    Row(){
                        Text(
                            text = "$${product.price}",
                            textDecoration = TextDecoration.LineThrough,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )

                        Text(
                            text = "  $${product.actualPrice}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                }
            }
        }

        HorizontalDivider()
    }

}