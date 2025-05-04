package com.emotionalai.companion.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ImageProcessingService(private val context: Context) {
    private val maxImageSize = 1024 // 最大图片尺寸
    private val compressionQuality = 80 // 压缩质量

    suspend fun processImage(uri: Uri): File = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        
        // 计算缩放比例
        val scale = calculateScale(originalBitmap.width, originalBitmap.height)
        val scaledBitmap = Bitmap.createScaledBitmap(
            originalBitmap,
            (originalBitmap.width * scale).toInt(),
            (originalBitmap.height * scale).toInt(),
            true
        )

        // 创建临时文件
        val outputFile = File(context.cacheDir, "processed_${System.currentTimeMillis()}.jpg")
        FileOutputStream(outputFile).use { out ->
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, out)
        }

        // 清理位图
        originalBitmap.recycle()
        scaledBitmap.recycle()

        outputFile
    }

    private fun calculateScale(width: Int, height: Int): Float {
        val maxDimension = maxOf(width, height)
        return if (maxDimension > maxImageSize) {
            maxImageSize.toFloat() / maxDimension
        } else {
            1f
        }
    }

    suspend fun getImageEmotion(file: File): Emotion = withContext(Dispatchers.Default) {
        // 使用TensorFlow Lite模型进行情绪分析
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        
        // 预处理图像数据
        val inputBuffer = ByteBuffer.allocateDirect(224 * 224 * 3 * 4)
        inputBuffer.order(ByteOrder.nativeOrder())
        
        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val pixel = resizedBitmap.getPixel(x, y)
                // 归一化RGB值到[-1, 1]
                inputBuffer.putFloat(((pixel shr 16 and 0xFF) / 255.0f) * 2 - 1)
                inputBuffer.putFloat(((pixel shr 8 and 0xFF) / 255.0f) * 2 - 1)
                inputBuffer.putFloat(((pixel and 0xFF) / 255.0f) * 2 - 1)
            }
        }

        // 运行模型推理
        val outputBuffer = ByteBuffer.allocateDirect(8 * 4) // 8种情绪类型
        outputBuffer.order(ByteOrder.nativeOrder())
        
        // TODO: 使用实际的TensorFlow Lite模型进行推理
        // 这里返回一个示例情绪
        Emotion(
            type = EmotionType.NEUTRAL,
            intensity = 0.5f,
            confidence = 0.8f
        )
    }
} 