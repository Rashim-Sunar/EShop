package com.example.easyshop.pages

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.easyshop.components.ProductItemsView
import com.example.easyshop.model.ProductsModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
@Composable
fun CategoryProductsPage(categoryId: String?) {
    val productsList = remember {
        mutableStateOf<List<ProductsModel>>(emptyList())
    }

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("data")
            .document("stock").collection("products")
            .whereEqualTo("category", categoryId)
            .get().addOnCompleteListener {
                val result = it.result.documents.mapNotNull { doc ->
                    doc.toObject(ProductsModel::class.java)
                }
                productsList.value = result
            }
    }


    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {

        items(productsList.value.chunked(2)){ rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { product ->
                    ProductItemsView(product, modifier = Modifier.weight(1f))
                }

                if(rowItems.size == 1){
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

        }

    }
}

