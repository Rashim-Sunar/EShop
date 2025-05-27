package com.example.easyshop.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil3.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue



@Composable
fun BannerView(){

    var bannerList by remember {
        mutableStateOf<List<String>>(emptyList())
    }

    // Fetch data once when the composable enters the composition
    LaunchedEffect(Unit) {
        Firebase.firestore.collection("data")
            .document("banners")
            .get()
            .addOnCompleteListener{
                bannerList = it.result.get("urls") as List<String>
            }
    }

    if (bannerList.isEmpty()) {
        // Show loading indicator
        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column (modifier = Modifier.padding(14.dp).fillMaxWidth()){

        // Simulate infinite scroll by repeating list infinitely
        val infinitePageCount = Int.MAX_VALUE
        val startIndex = infinitePageCount / 2 // Start from middle
        val pagerState = rememberPagerState(
            initialPage = startIndex,
            pageCount = { infinitePageCount }
        )

        // Auto-scroll logic
        LaunchedEffect(pagerState) {
            while (true) {
                delay(3000) // scroll every 3 seconds
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        }

        // Calculate the actual banner index from infinite pager
        val actualIndex by remember {
            derivedStateOf {
                pagerState.currentPage % bannerList.size
            }
        }


        HorizontalPager(
            pageSpacing = 24.dp,
            state = pagerState
        ) { page ->

            Card(
                Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .graphicsLayer {
                        // Calculate the absolute offset for the current page from the
                        // scroll position. We use the absolute value which allows us to mirror
                        // any effects for both directions
                        val pageOffset = (
                                (pagerState.currentPage - page) + pagerState
                                    .currentPageOffsetFraction
                                ).absoluteValue

                        // We animate the alpha, between 50% and 100%
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
            ) {
                // Card content
                AsyncImage(
                    model = bannerList[actualIndex],
                    contentDescription = "Banner Image",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop

                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dot indicator synced with actualIndex
        DotsIndicator(
            dotCount = bannerList.size,
            selectedIndex = actualIndex
        )

    }
}

@Composable
fun DotsIndicator(
    dotCount: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    dotSize: Dp = 14.dp,
    dotSpacing: Dp = 8.dp,
    selectedColor: Color = MaterialTheme.colorScheme.secondary,
    unSelectedColor: Color = Color.Gray.copy(0.4f)
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        repeat(dotCount) { index ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(if (index == selectedIndex) selectedColor else unSelectedColor)
            )
            if (index < dotCount - 1) {
                Spacer(modifier = Modifier.width(dotSpacing))
            }
        }
    }
}



