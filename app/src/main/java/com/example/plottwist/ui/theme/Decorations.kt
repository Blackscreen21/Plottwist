package com.example.plottwist.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun FiligreeBorder(
    modifier: Modifier = Modifier,
    color: Color = RichGold
) {
    Canvas(modifier = modifier.fillMaxWidth().height(80.dp)) {
        val width = size.width
        val height = size.height

        // Top border line
        drawLine(
            color = color,
            start = Offset(0f, height * 0.25f),
            end = Offset(width, height * 0.25f),
            strokeWidth = 2.dp.toPx()
        )

        // Decorative dots
        val dotCount = 50
        for (i in 0 until dotCount) {
            val x = (i * width / dotCount)
            drawCircle(
                color = color,
                radius = 1.5.dp.toPx(),
                center = Offset(x, height * 0.15f)
            )
        }

        // Center diamond ornament
        val centerX = width / 2
        val centerY = height * 0.65f
        val size = 25.dp.toPx()

        val path = Path().apply {
            moveTo(centerX, centerY - size / 2)
            lineTo(centerX + size / 3, centerY)
            lineTo(centerX, centerY + size / 2)
            lineTo(centerX - size / 3, centerY)
            close()
        }

        drawPath(path, color = color, style = Stroke(width = 2.dp.toPx()))
        drawCircle(color = color, radius = size / 8, center = Offset(centerX, centerY))
    }
}

@Composable
fun OrnateDivider(
    modifier: Modifier = Modifier,
    color: Color = RichGold
) {
    Canvas(modifier = modifier.fillMaxWidth().height(20.dp)) {
        val width = size.width
        val centerY = size.height / 2

        // Lines with center ornament
        drawLine(
            color = color,
            start = Offset(0f, centerY),
            end = Offset(width * 0.42f, centerY),
            strokeWidth = 1.dp.toPx()
        )

        drawLine(
            color = color,
            start = Offset(width * 0.58f, centerY),
            end = Offset(width, centerY),
            strokeWidth = 1.dp.toPx()
        )

        val centerX = width / 2
        drawCircle(color = color, radius = 3.dp.toPx(), center = Offset(centerX, centerY))
        drawCircle(
            color = color,
            radius = 6.dp.toPx(),
            center = Offset(centerX, centerY),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

// Rotated diamond (90° from vertical to horizontal)
@Composable
fun SideDiamond(
    modifier: Modifier = Modifier,
    color: Color = RichGold
) {
    Canvas(modifier = modifier.size(30.dp, 20.dp)) {
        val width = size.width
        val height = size.height
        val centerX = width / 2
        val centerY = height / 2

        val path = Path().apply {
            moveTo(centerX - width / 3, centerY)
            lineTo(centerX, centerY - height / 3)
            lineTo(centerX + width / 3, centerY)
            lineTo(centerX, centerY + height / 3)
            close()
        }

        drawPath(path, color = color, style = Stroke(width = 2.dp.toPx()))
        drawCircle(color = color, radius = 2.dp.toPx(), center = Offset(centerX, centerY))
    }
}

@Composable
fun CornerFiligree(
    modifier: Modifier = Modifier,
    color: Color = RichGold
) {
    Canvas(modifier = modifier.size(50.dp)) {
        val width = size.width
        val height = size.height

        val path = Path().apply {
            moveTo(0f, height * 0.3f)
            cubicTo(0f, height * 0.15f, width * 0.15f, 0f, width * 0.3f, 0f)
            moveTo(width * 0.2f, height * 0.2f)
            cubicTo(width * 0.3f, height * 0.1f, width * 0.4f, height * 0.15f, width * 0.5f, height * 0.3f)
        }

        drawPath(path, color = color, style = Stroke(width = 2.dp.toPx()))
        drawCircle(color = color, radius = 2.dp.toPx(), center = Offset(width * 0.15f, height * 0.15f))
        drawCircle(color = color, radius = 2.dp.toPx(), center = Offset(width * 0.3f, height * 0.05f))
    }
}

@Composable
fun ButtonDots(
    modifier: Modifier = Modifier,
    color: Color = RichGold
) {
    Canvas(modifier = modifier.height(12.dp)) {
        val width = size.width
        val centerY = size.height / 2
        val dotCount = 50

        for (i in 0 until dotCount) {
            val x = (i * width / dotCount)
            drawCircle(
                color = color,
                radius = 1.5.dp.toPx(),
                center = Offset(x, centerY)
            )
        }
    }
}