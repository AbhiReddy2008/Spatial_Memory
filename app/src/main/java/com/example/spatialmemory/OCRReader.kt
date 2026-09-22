package com.example.spatialmemory.detection

import android.graphics.Bitmap
import android.graphics.Rect

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class OCRReader {

    private val recognizer =
        TextRecognition.getClient(
            TextRecognizerOptions.DEFAULT_OPTIONS
        )

    fun process(
        bitmap: Bitmap,
        rotationDegrees: Int,
        onResult: (List<OCRResult>) -> Unit
    ) {

        val inputImage =
            InputImage.fromBitmap(
                bitmap,
                rotationDegrees
            )

        recognizer.process(inputImage)
            .addOnSuccessListener { result ->

                val detectedTexts =
                    result.textBlocks.map { block ->

                        OCRResult(
                            text = block.text,
                            boundingBox = block.boundingBox
                        )
                    }

                onResult(detectedTexts)
            }
            .addOnFailureListener { exception ->

                println(
                    "OCR >>> ERROR: ${exception.message}"
                )

                onResult(emptyList())
            }
    }

    fun close() {
        recognizer.close()
    }
}


data class OCRResult(
    val text: String,
    val boundingBox: Rect?
)