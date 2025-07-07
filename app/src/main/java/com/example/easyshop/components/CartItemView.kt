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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.Disposable
import com.example.easyshop.AppUtil
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
                            text = "$${AppUtil.calculateTotalPrice(product.price, selectedQuantity.longValue)}",
                            textDecoration = TextDecoration.LineThrough,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.DarkGray
                        )

                        Text(
                            text = "  $${AppUtil.calculateTotalPrice(product.actualPrice, selectedQuantity.longValue)}",
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
                            text = AppUtil.calculateDiscount(product.price, product.actualPrice),
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

