package com.emotionalai.companion.model

data class Message(
    val id: Long = 0,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromUser: Boolean,
    val emotion: Emotion? = null,
    val mediaType: MediaType = MediaType.TEXT
)

enum class MediaType {
    TEXT,
    VOICE,
    IMAGE
} 