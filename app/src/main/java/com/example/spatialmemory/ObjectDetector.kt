package com.example.spatialmemory.detection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import kotlin.math.max
import kotlin.math.min

data class Detection(
    val classId: Int,
    val label: String,
    val confidence: Float,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

class ObjectDetector(private val context: Context) {

    companion object {
        private const val TAG = "YOLO26"
        private const val MODEL_NAME = "yolo26n.tflite"
        private const val INPUT_SIZE = 640
        private const val NUM_CLASSES = 80
        private const val NUM_CANDIDATES = 8400
        private const val CONFIDENCE_THRESHOLD = 0.35f
        private const val IOU_THRESHOLD = 0.45f

        private val COCO_LABELS = listOf(
            "person", "bicycle", "car", "motorcycle", "airplane",
            "bus", "train", "truck", "boat", "traffic light",
            "fire hydrant", "stop sign", "parking meter", "bench",
            "bird", "cat", "dog", "horse", "sheep", "cow",
            "elephant", "bear", "zebra", "giraffe", "backpack",
            "umbrella", "handbag", "tie", "suitcase", "frisbee",
            "skis", "snowboard", "sports ball", "kite", "baseball bat",
            "baseball glove", "skateboard", "surfboard", "tennis racket",
            "bottle", "wine glass", "cup", "fork", "knife", "spoon",
            "bowl", "banana", "apple", "sandwich", "orange", "broccoli",
            "carrot", "hot dog", "pizza", "donut", "cake", "chair",
            "couch", "potted plant", "bed", "dining table", "toilet",
            "tv", "laptop", "mouse", "remote", "keyboard", "cell phone",
            "microwave", "oven", "toaster", "sink", "refrigerator",
            "book", "clock", "vase", "scissors", "teddy bear",
            "hair drier", "toothbrush"
        )
    }

    private var interpreter: Interpreter? = null

    @Synchronized
    fun loadModel() {
        if (interpreter != null) return

        try {
            val options = Interpreter.Options().apply {
                setNumThreads(2)
            }

            interpreter = Interpreter(loadModelFile(), options)

            val model = interpreter!!
            Log.d(TAG, "Model loaded")

            for (i in 0 until model.inputTensorCount) {
                val tensor = model.getInputTensor(i)
                Log.d(
                    TAG,
                    "INPUT $i: ${tensor.shape().contentToString()} ${tensor.dataType()}"
                )
            }

            for (i in 0 until model.outputTensorCount) {
                val tensor = model.getOutputTensor(i)
                Log.d(
                    TAG,
                    "OUTPUT $i: ${tensor.shape().contentToString()} ${tensor.dataType()}"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Model loading failed", e)
            interpreter?.close()
            interpreter = null
        }
    }

    /**
     * Takes JPEG bytes from CameraFrame.imageData and returns detections.
     * Call this on a background thread, not the Android main thread.
     */
    @Synchronized
    fun detect(jpegBytes: ByteArray): List<Detection> {
        val model = interpreter ?: run {
            Log.e(TAG, "detect() called before model was loaded")
            return emptyList()
        }

        val bitmap = BitmapFactory.decodeByteArray(
            jpegBytes, 0, jpegBytes.size
        ) ?: return emptyList()

        try {
            val resized = Bitmap.createScaledBitmap(
                bitmap, INPUT_SIZE, INPUT_SIZE, true
            )

            val input = bitmapToNchwBuffer(resized)
            val output = Array(1) {
                Array(84) { FloatArray(NUM_CANDIDATES) }
            }

            model.run(input, output)

            val detections = decodeOutput(output[0])

            if (resized !== bitmap) resized.recycle()

            Log.d(TAG, "Detected ${detections.size} objects")
            return detections
        } catch (e: Exception) {
            Log.e(TAG, "Inference failed", e)
            return emptyList()
        } finally {
            bitmap.recycle()
        }
    }

    private fun bitmapToNchwBuffer(bitmap: Bitmap): ByteBuffer {
        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        bitmap.getPixels(
            pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE
        )

        val buffer = ByteBuffer
            .allocateDirect(4 * 3 * INPUT_SIZE * INPUT_SIZE)
            .order(ByteOrder.nativeOrder())

        // NCHW planar RGB, normalized to [0,1].
        val planeSize = INPUT_SIZE * INPUT_SIZE

        for (channel in 0 until 3) {
            for (pixel in pixels) {
                val value = when (channel) {
                    0 -> (pixel shr 16) and 0xFF // R
                    1 -> (pixel shr 8) and 0xFF  // G
                    else -> pixel and 0xFF       // B
                }

                buffer.putFloat(value / 255.0f)
            }
        }

        buffer.rewind()
        return buffer
    }

    /**
     * Expected output layout: [84][8400]
     * First 4 values are box coordinates; next 80 are class scores.
     */
    private fun decodeOutput(output: Array<FloatArray>): List<Detection> {
        val candidates = mutableListOf<Detection>()

        for (i in 0 until NUM_CANDIDATES) {
            var bestClass = -1
            var bestScore = 0f

            for (classIndex in 0 until NUM_CLASSES) {
                val score = output[4 + classIndex][i]

                if (score > bestScore) {
                    bestScore = score
                    bestClass = classIndex
                }
            }

            if (bestScore < CONFIDENCE_THRESHOLD || bestClass < 0) {
                continue
            }

            // YOLO-style center x, center y, width, height.
            val cx = output[0][i]
            val cy = output[1][i]
            val width = output[2][i]
            val height = output[3][i]

            val left = (cx - width / 2f).coerceIn(0f, INPUT_SIZE.toFloat())
            val top = (cy - height / 2f).coerceIn(0f, INPUT_SIZE.toFloat())
            val right = (cx + width / 2f).coerceIn(0f, INPUT_SIZE.toFloat())
            val bottom = (cy + height / 2f).coerceIn(0f, INPUT_SIZE.toFloat())

            if (right <= left || bottom <= top) continue

            candidates.add(
                Detection(
                    classId = bestClass,
                    label = COCO_LABELS.getOrElse(bestClass) { "class_$bestClass" },
                    confidence = bestScore,
                    left = left / INPUT_SIZE,
                    top = top / INPUT_SIZE,
                    right = right / INPUT_SIZE,
                    bottom = bottom / INPUT_SIZE
                )
            )
        }

        return applyNms(candidates)
    }

    private fun applyNms(
        detections: List<Detection>
    ): List<Detection> {
        val kept = mutableListOf<Detection>()

        // Suppress overlapping boxes only when they belong to the same class.
        for (classGroup in detections.groupBy { it.classId }.values) {
            val sorted = classGroup.sortedByDescending { it.confidence }
            val selected = mutableListOf<Detection>()

            for (candidate in sorted) {
                val overlaps = selected.any {
                    intersectionOverUnion(candidate, it) > IOU_THRESHOLD
                }

                if (!overlaps) selected.add(candidate)
            }

            kept.addAll(selected)
        }

        return kept.sortedByDescending { it.confidence }
    }

    private fun intersectionOverUnion(a: Detection, b: Detection): Float {
        val x1 = max(a.left, b.left)
        val y1 = max(a.top, b.top)
        val x2 = min(a.right, b.right)
        val y2 = min(a.bottom, b.bottom)

        val intersection = max(0f, x2 - x1) * max(0f, y2 - y1)

        val areaA = (a.right - a.left) * (a.bottom - a.top)
        val areaB = (b.right - b.left) * (b.bottom - b.top)
        val union = areaA + areaB - intersection

        return if (union <= 0f) 0f else intersection / union
    }

    private fun loadModelFile(): java.nio.MappedByteBuffer {
        val descriptor = context.assets.openFd(MODEL_NAME)
        FileInputStream(descriptor.fileDescriptor).use { input ->
            return input.channel.map(
                FileChannel.MapMode.READ_ONLY,
                descriptor.startOffset,
                descriptor.declaredLength
            )
        }
    }

    @Synchronized
    fun close() {
        interpreter?.close()
        interpreter = null
    }
}