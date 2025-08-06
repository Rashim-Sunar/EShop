package com.example.easyshop.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.easyshop.components.OrderedItemFromId
import com.example.easyshop.viewmodel.OrdersViewModel

@Composable
fun OrdersPage(ordersViewModel: OrdersViewModel = viewModel()) {
    val orders = ordersViewModel.orders
    val productMap = ordersViewModel.productMap

    val allCartItems = orders.flatMap { it.item }

    Column {
        Text(
            text = "My Orders",
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(start = 12.dp, top = 16.dp, bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
//            items(orders) { order ->
//                order.item.forEach { cartItem ->
//                    val product = productMap[cartItem.productId]
//                    if (product != null) {
//                        OrderedItemFromId(product)
//                    }
//                }
//            }

            items(allCartItems) { cartItem ->
                val product = productMap[cartItem.productId]
                if (product != null) {
                    OrderedItemFromId(product)
                }
            }

        }
    }
}
