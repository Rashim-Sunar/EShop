package com.example.easyshop.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.easyshop.AppUtil
import com.example.easyshop.components.HorizontalDashedDivider
import com.example.easyshop.model.ProductsModel
import com.example.easyshop.viewmodel.CheckoutViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.easyshop.GlobalNavController.navController
import com.example.easyshop.model.CartItemModel
import com.example.easyshop.model.CheckoutModel

@Composable
fun CheckoutPage(productId: String, quantity: Long, checkoutViewModel : CheckoutViewModel = viewModel()) {
    val context = LocalContext.current
    val productDetail = remember { mutableStateOf<ProductsModel?>(null) }

    val scrollState = rememberScrollState()
    val address = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val customerName = remember { mutableStateOf("") }
    val placingOrder = remember{mutableStateOf(false)}

    // Fetch product details from Firebase
    LaunchedEffect(productId) {
        Firebase.firestore.collection("data")
            .document("stock")
            .collection("products")
            .document(productId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    productDetail.value = snapshot.toObject(ProductsModel::class.java)
                }
            }
    }

    productDetail.value?.let { product ->
        Column (modifier = Modifier.padding(16.dp).verticalScroll(scrollState)) {
            Text("Checkout", style = MaterialTheme.typography.headlineMedium)

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().height(120.dp)
            ){
                AsyncImage(
                    model = product.images.firstOrNull(),
                    contentDescription = null,
                    modifier = Modifier
                        .height(200.dp)
                        .width(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Text(product.title, fontSize = 15.sp, fontWeight = FontWeight.Normal, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }

            //--------------------Pricing Details--------------------------------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(8.dp)
            ){
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Price Details", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

                   HorizontalDashedDivider()

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                        Text(text = "Price ($quantity item)")
                        Text(text = "$${AppUtil.calculateTotalPrice(product.price, quantity)}")
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                        Text(text = "Discount")
                        Text(text = "- $${
                            AppUtil.calculateDiscountAmount(
                                AppUtil.calculateTotalPrice(product.price, quantity),
                            AppUtil.calculateTotalPrice(product.actualPrice, quantity)
                            )
                        }")
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                        Text(text = "Platform Fee")
                        Text("+ $10")
                    }
                    HorizontalDashedDivider()

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                        Text(text = "Total Amount", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text("$${AppUtil.calculateTotalPrice(product.actualPrice, quantity, 10)}",  fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            //---------------------- Delivery Details Section-------------------------

            Text("Delivery Details", fontWeight = FontWeight.Bold)

            DeliveryDetails(
                address = address.value,
                onAddressChange = { address.value = it },
                phone = phone.value,
                onPhoneChange = { phone.value = it },
                name = customerName.value,
                onNameChange = {customerName.value = it}
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ---------------------- Order Button ------------------------------------------

            Button(
                onClick = {
                    placingOrder.value = true

                    val cartItem = CartItemModel(
                        productId = productId,
                        name = product.title,
                        price = product.actualPrice,
                        quantity = quantity.toInt()
                    )

                    val totalAmount = AppUtil.calculateTotalPrice(product.actualPrice, quantity, 10)

                    val order = CheckoutModel(
                        userId = "", // Will be set in ViewModel
                        item = listOf(cartItem),
                        totalAmount = totalAmount,
                        address = address.value,
                        timestamp = System.currentTimeMillis()
                    )

                    checkoutViewModel.placeOrder(order) { success, error ->
                        placingOrder.value = false
                        if (success) {
                            AppUtil.showToast(context, "Order placed successfully!")
                            // Navigate to another screen if needed
                            navController.navigate("order_success/${totalAmount}")
                        } else {
                            AppUtil.showToast(context, error ?: "Something went wrong.")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !placingOrder.value
            ) {
                Text(if (placingOrder.value) "Placing Order...." else "Place Order")
            }

        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun DeliveryDetails(
    address: String,
    onAddressChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    name: String,
    onNameChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            label = { Text("Address") },
            placeholder = { Text("Enter delivery address") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true

        )

        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("Phone Number") },
            placeholder = { Text("Enter contact number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Customer Name") },
            placeholder = { Text("Enter your name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}


