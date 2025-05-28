package com.example.easyshop.pages

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
                productsList.value = it.result.documents.mapNotNull { doc ->
                    doc.toObject(ProductsModel::class.java)
                }
            }
    }

    LazyColumn {
        items(productsList.value){ product ->
            Text("${product.title} + ---> ${product.actualPrice}")
            Spacer(modifier = Modifier.height(10.dp))
        }

    }
}