package com.example.easyshop.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalDashedDivider(
    color: Color = Color(0xFFBDBDBD),
    dashLength: Float = 10f,
    gapLength: Float = 6f,
    strokeWidth: Float = 1f,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
) {
    Canvas(modifier = modifier) {
        val totalWidth = size.width
        val y = size.height / 2

        var currentX = 0f

        while (currentX < totalWidth) {
            val endX = (currentX + dashLength).coerceAtMost(totalWidth)
            drawLine(
                color = color,
                start = Offset(currentX, y),
                end = Offset(endX, y),
                strokeWidth = strokeWidth
            )
            currentX += dashLength + gapLength
        }
    }
}


