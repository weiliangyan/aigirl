package com.emotionalai.companion.model

data class InputBundle(
    val text: String? = null,
    val audioPath: String? = null,
    val imagePath: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class EmotionAnalysis(
    val textEmotion: Emotion? = null,
    val audioEmotion: Emotion? = null,
    val imageEmotion: Emotion? = null,
    val finalEmotion: Emotion
)

data class Emotion(
    val type: EmotionType,
    val intensity: Float, // 0.0-1.0
    val confidence: Float // 0.0-1.0
)

enum class EmotionType {
    HAPPY, SAD, ANGRY, NEUTRAL, EXCITED, ANXIOUS, CALM, MELANCHOLY
} 