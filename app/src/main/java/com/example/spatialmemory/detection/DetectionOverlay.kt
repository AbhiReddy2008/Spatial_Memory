package com.example.spatialmemory.detection

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import android.graphics.Paint
import android.graphics.RectF

@Composable
fun DetectionOverlay(
    detections: List<Detection>,
    modifier: Modifier = Modifier
) {

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {

        val canvasWidth = size.width
        val canvasHeight = size.height

        val boxPaint = Paint().apply {
            color = android.graphics.Color.GREEN
            style = Paint.Style.STROKE
            strokeWidth = 5f
            isAntiAlias = true
        }

        val backgroundPaint = Paint().apply {
            color = android.graphics.Color.GREEN
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 38f
            style = Paint.Style.FILL
            isAntiAlias = true
            typeface = android.graphics.Typeface.create(
                android.graphics.Typeface.DEFAULT,
                android.graphics.Typeface.BOLD
            )
        }

        drawIntoCanvas { canvas ->

            val nativeCanvas = canvas.nativeCanvas

            detections.forEach { detection ->

                // Coordinates are normalized from 0 to 1.
                val left = detection.left * canvasWidth
                val top = detection.top * canvasHeight
                val right = detection.right * canvasWidth
                val bottom = detection.bottom * canvasHeight

                // Ignore invalid boxes.
                if (right <= left || bottom <= top) {
                    return@forEach
                }

                val rect = RectF(
                    left,
                    top,
                    right,
                    bottom
                )

                // Draw the bounding rectangle.
                nativeCanvas.drawRect(
                    rect,
                    boxPaint
                )

                // Prepare object label.
                val label = "${detection.label} " +
                    "${(detection.confidence * 100).toInt()}%"

                val textWidth = textPaint.measureText(label)
                val labelHeight = 48f

                // Keep the label inside the preview.
                val labelLeft = left.coerceIn(
                    0f,
                    (canvasWidth - textWidth - 20f).coerceAtLeast(0f)
                )

                val labelTop = (top - labelHeight)
                    .coerceAtLeast(0f)

                // Draw green background behind the label.
                nativeCanvas.drawRect(
                    labelLeft,
                    labelTop,
                    labelLeft + textWidth + 20f,
                    labelTop + labelHeight,
                    backgroundPaint
                )

                // Draw label text.
                nativeCanvas.drawText(
                    label,
                    labelLeft + 10f,
                    labelTop + 36f,
                    textPaint
                )
            }
        }
    }
}