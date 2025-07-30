package com.example.easyshop.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.easyshop.AppUtil
import com.example.easyshop.GlobalNavController
import com.example.easyshop.model.ProductsModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartItemView(productId: String, quantity: Long) {

    val productDetail = remember { mutableStateOf<ProductsModel?>(null) }
    val isDropdownExpanded = remember { mutableStateOf(false) }
    val selectedQuantity = remember { mutableLongStateOf(quantity) }
    val quantityDropDownList = listOf("1", "2", "3", "more")

    // For custom popUp
    val showCustomQtyDialog = remember { mutableStateOf(false) }
    val customQtyInput = remember { mutableStateOf("") }

    val context = LocalContext.current

    DisposableEffect(productId) {
        val listenerRegistration = Firebase.firestore.collection("data")
            .document("stock")
            .collection("products")
            .document(productId)
            .addSnapshotListener { snapshot, error ->  // snapshotListener for getting realtime data on snapshot or data change
                if (error != null) {
                    Log.w("CartItemView", "Error fetching product details", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    productDetail.value = snapshot.toObject(ProductsModel::class.java)
                } else {
                    Log.d("CartItemView", "Product not found or null snapshot.")
                }
            }

        onDispose {
            listenerRegistration.remove() // Detach a listener
        }
    }

    productDetail.value?.let { product ->
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
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
                    Spacer(modifier = Modifier.height(2.dp))

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
                                        if(item == "more") {
                                            showCustomQtyDialog.value = true
                                        }else{
                                            val newQty = item.toLongOrNull()
                                            if(newQty != null){
                                                selectedQuantity.longValue = item.toLongOrNull() ?: selectedQuantity.longValue
                                                AppUtil.updateCartQuantity(productId, newQty, context)
                                            }
                                        }
                                        isDropdownExpanded.value = false
                                    }
                                )
                            }
                        }
                    }

                }

                Spacer(modifier = Modifier.width(12.dp))

                // Product Title and Price Details
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "₹${product.price}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                textDecoration = TextDecoration.LineThrough,
                                color = Color.Gray
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "₹${product.actualPrice}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Discount: ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF3F51B5),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = AppUtil.calculateDiscount(product.price, product.actualPrice),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color(0xFF3F51B5),
                                fontWeight = FontWeight.Bold,
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Discount icon",
                            tint = Color(0xFF3F51B5),
                            modifier = Modifier
                                .size(18.dp)

                                .padding(start = 4.dp)
                        )
                    }
                }
            }

            // row for remove from cart and order button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp), // reduces space above and below the row
                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Remove Button
                TextButton(
                    onClick = {
                        AppUtil.removeFromCart(productId, context)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red),
                    border = _root_ide_package_.androidx.compose.foundation.BorderStroke(1.dp, Color.Gray),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Remove")
                }

                // Order Now Button
                TextButton(
                    onClick = {
                        GlobalNavController.navController.navigate("checkout/$productId/${selectedQuantity.longValue}")
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF2E7D32)),
                    border = _root_ide_package_.androidx.compose.foundation.BorderStroke(1.dp, Color.Gray),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Order Now",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Order Now")
                }
            }
            Spacer(modifier = Modifier.height(6.dp))

        }
    }

    CustomQuantityDialog(
        showDialog = showCustomQtyDialog.value,
        currentInput = customQtyInput.value,
        onInputChange = { customQtyInput.value = it },
        onConfirm = {
            val customQty = customQtyInput.value.toLongOrNull()
            if (customQty != null && customQty > 0) {
                selectedQuantity.longValue = customQty
                AppUtil.updateCartQuantity(productId, customQty, context)
            }
            showCustomQtyDialog.value = false
            customQtyInput.value = ""
        },
        onDismiss = {
            showCustomQtyDialog.value = false
            customQtyInput.value = ""
        }
    )

}

// ui for popUp to change the quantity
@Composable
fun CustomQuantityDialog(
    showDialog: Boolean,
    currentInput: String,
    onInputChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            },
            title = { Text("Enter Quantity") },
            text = {
                OutlinedTextField(
                    value = currentInput,
                    onValueChange = {
                        if (it.all { ch -> ch.isDigit() }) {
                            onInputChange(it)
                        }
                    },
                    label = { Text("Quantity") },
                    singleLine = true
                )
            }
        )
    }
}


