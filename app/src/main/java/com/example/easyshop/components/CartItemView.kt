package com.example.easyshop.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartItemView(productId: String, quantity: Long) {

    val productDetail = remember { mutableStateOf<ProductsModel?>(null) }
    val isDropdownExpanded = remember { mutableStateOf(false) }
    val selectedQuantity = remember { mutableLongStateOf(quantity) }
    val quantityDropDownList = listOf("1", "2", "3", "more")

    LaunchedEffect(productId) {
        try {
            val snapshot = Firebase.firestore.collection("data")
                .document("stock")
                .collection("products")
                .document(productId)
                .get()
                .await()

            val result = snapshot.toObject(ProductsModel::class.java)
            if (result != null) {
                productDetail.value = result
            } else {
                Log.d("CartItemView", "Fetched null product detail...")
            }
        } catch (e: Exception) {
            Log.d("CartItemView", "Error fetching product detail with productId: ${e.message}")
        }
    }

    productDetail.value?.let { product ->
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    // Product Image
                    AsyncImage(
                        model = product.images.firstOrNull(),
                        contentDescription = "Product Image",
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    // Quantity Row
                    Spacer(modifier = Modifier.height(4.dp))

                    // Dropdown for quantity
                    Box {
                        OutlinedButton(
                            onClick = { isDropdownExpanded.value = true },
                            modifier = Modifier
                                .width(80.dp)
                                .height(36.dp),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = "Qty: ${selectedQuantity.longValue}", fontSize = 14.sp)
                        }

                        DropdownMenu(
                            expanded = isDropdownExpanded.value,
                            onDismissRequest = { isDropdownExpanded.value = false },
                            modifier = Modifier
                                .width(80.dp)
                        ) {
                            quantityDropDownList.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item, fontSize = 14.sp) },
                                    onClick = {
                                        selectedQuantity.longValue = item.toLongOrNull() ?: selectedQuantity.longValue
                                        isDropdownExpanded.value = false
                                    }
                                )
                            }
                        }
                    }

                }

                Spacer(modifier = Modifier.width(12.dp))

                // Product Title and Price Details
                Column(modifier = Modifier.weight(1f)) {

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = product.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp)
                    )

                    Row {
                        Text(
                            text = "$${product.price}",
                            textDecoration = TextDecoration.LineThrough,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.DarkGray
                        )

                        Text(
                            text = "  $${product.actualPrice}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row{
                        Text(
                            text = "Discount:  ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(66,161,85)
                        )

                        Text(
                            text = "54%",
                            color = Color(66,161,85),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Discount icon",
                            tint = Color(66,161,85),
                            modifier = Modifier.size(24.dp),
                        )
                    }

                }
            }

            HorizontalDivider()
        }
    }
}
