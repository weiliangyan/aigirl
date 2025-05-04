package com.emotionalai.companion.emotion

import android.content.Context
import com.emotionalai.companion.model.*
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.exp
import kotlin.math.ln

class EmotionAnalyzer(private val context: Context) {
    private var textModel: Interpreter? = null
    private var audioModel: Interpreter? = null
    private var imageModel: Interpreter? = null

    init {
        loadModels()
    }

    private fun loadModels() {
        try {
            textModel = Interpreter(loadModelFile("text_emotion_model.tflite"))
            audioModel = Interpreter(loadModelFile("audio_emotion_model.tflite"))
            imageModel = Interpreter(loadModelFile("image_emotion_model.tflite"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadModelFile(modelName: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    suspend fun analyzeEmotion(input: InputBundle): EmotionAnalysis {
        val textEmotion = input.text?.let { analyzeTextEmotion(it) }
        val audioEmotion = input.audioPath?.let { analyzeAudioEmotion(it) }
        val imageEmotion = input.imagePath?.let { analyzeImageEmotion(it) }

        // 使用加权融合算法
        val finalEmotion = fuseEmotions(textEmotion, audioEmotion, imageEmotion)

        return EmotionAnalysis(
            textEmotion = textEmotion,
            audioEmotion = audioEmotion,
            imageEmotion = imageEmotion,
            finalEmotion = finalEmotion
        )
    }

    private fun analyzeTextEmotion(text: String): Emotion {
        val inputBuffer = preprocessText(text)
        val outputBuffer = ByteBuffer.allocateDirect(EmotionType.values().size * 4)
        outputBuffer.order(ByteOrder.nativeOrder())

        textModel?.run(inputBuffer, outputBuffer)

        val emotions = FloatArray(EmotionType.values().size)
        outputBuffer.rewind()
        outputBuffer.get(emotions)

        // 应用softmax
        val softmaxEmotions = softmax(emotions)
        val maxIndex = softmaxEmotions.indices.maxByOrNull { softmaxEmotions[it] } ?: 0

        return Emotion(
            type = EmotionType.values()[maxIndex],
            intensity = softmaxEmotions[maxIndex],
            confidence = calculateConfidence(softmaxEmotions)
        )
    }

    private fun analyzeAudioEmotion(audioPath: String): Emotion {
        val inputBuffer = preprocessAudio(audioPath)
        val outputBuffer = ByteBuffer.allocateDirect(EmotionType.values().size * 4)
        outputBuffer.order(ByteOrder.nativeOrder())

        audioModel?.run(inputBuffer, outputBuffer)

        val emotions = FloatArray(EmotionType.values().size)
        outputBuffer.rewind()
        outputBuffer.get(emotions)

        val softmaxEmotions = softmax(emotions)
        val maxIndex = softmaxEmotions.indices.maxByOrNull { softmaxEmotions[it] } ?: 0

        return Emotion(
            type = EmotionType.values()[maxIndex],
            intensity = softmaxEmotions[maxIndex],
            confidence = calculateConfidence(softmaxEmotions)
        )
    }

    private fun analyzeImageEmotion(imagePath: String): Emotion {
        val inputBuffer = preprocessImage(imagePath)
        val outputBuffer = ByteBuffer.allocateDirect(EmotionType.values().size * 4)
        outputBuffer.order(ByteOrder.nativeOrder())

        imageModel?.run(inputBuffer, outputBuffer)

        val emotions = FloatArray(EmotionType.values().size)
        outputBuffer.rewind()
        outputBuffer.get(emotions)

        val softmaxEmotions = softmax(emotions)
        val maxIndex = softmaxEmotions.indices.maxByOrNull { softmaxEmotions[it] } ?: 0

        return Emotion(
            type = EmotionType.values()[maxIndex],
            intensity = softmaxEmotions[maxIndex],
            confidence = calculateConfidence(softmaxEmotions)
        )
    }

    private fun fuseEmotions(
        textEmotion: Emotion?,
        audioEmotion: Emotion?,
        imageEmotion: Emotion?
    ): Emotion {
        val emotions = listOfNotNull(textEmotion, audioEmotion, imageEmotion)
        if (emotions.isEmpty()) return Emotion(EmotionType.NEUTRAL, 0.5f, 1.0f)

        // 动态权重计算
        val weights = calculateWeights(emotions)
        
        // 加权融合
        val weightedEmotions = emotions.zip(weights).map { (emotion, weight) ->
            emotion.intensity * weight
        }

        val maxIndex = weightedEmotions.indices.maxByOrNull { weightedEmotions[it] } ?: 0
        val dominantEmotion = emotions[maxIndex]

        return Emotion(
            type = dominantEmotion.type,
            intensity = weightedEmotions[maxIndex],
            confidence = calculateFusionConfidence(emotions, weights)
        )
    }

    private fun calculateWeights(emotions: List<Emotion>): List<Float> {
        val confidences = emotions.map { it.confidence }
        val totalConfidence = confidences.sum()
        
        return if (totalConfidence > 0) {
            confidences.map { it / totalConfidence }
        } else {
            List(emotions.size) { 1f / emotions.size }
        }
    }

    private fun calculateFusionConfidence(
        emotions: List<Emotion>,
        weights: List<Float>
    ): Float {
        return emotions.zip(weights).sumOf { (emotion, weight) ->
            emotion.confidence * weight
        }.toFloat()
    }

    private fun softmax(input: FloatArray): FloatArray {
        val max = input.maxOrNull() ?: 0f
        val expValues = input.map { exp(it - max) }
        val sum = expValues.sum()
        return expValues.map { it / sum }.toFloatArray()
    }

    private fun calculateConfidence(emotions: FloatArray): Float {
        val maxValue = emotions.maxOrNull() ?: 0f
        val entropy = -emotions.sumOf { 
            if (it > 0) it * ln(it.toDouble()) else 0.0 
        }
        val maxEntropy = ln(emotions.size.toDouble())
        val normalizedEntropy = (entropy / maxEntropy).toFloat()
        
        return maxValue * (1 - normalizedEntropy)
    }

    private fun preprocessText(text: String): ByteBuffer {
        // 文本预处理：分词、向量化等
        val inputBuffer = ByteBuffer.allocateDirect(512 * 4)
        inputBuffer.order(ByteOrder.nativeOrder())
        
        // TODO: 实现文本预处理逻辑
        
        return inputBuffer
    }

    private fun preprocessAudio(audioPath: String): ByteBuffer {
        // 音频预处理：特征提取、归一化等
        val inputBuffer = ByteBuffer.allocateDirect(128 * 4)
        inputBuffer.order(ByteOrder.nativeOrder())
        
        // TODO: 实现音频预处理逻辑
        
        return inputBuffer
    }

    private fun preprocessImage(imagePath: String): ByteBuffer {
        // 图像预处理：调整大小、归一化等
        val inputBuffer = ByteBuffer.allocateDirect(224 * 224 * 3 * 4)
        inputBuffer.order(ByteOrder.nativeOrder())
        
        // TODO: 实现图像预处理逻辑
        
        return inputBuffer
    }
} 