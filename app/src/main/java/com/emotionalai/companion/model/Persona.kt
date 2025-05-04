package com.emotionalai.companion.model

data class Persona(
    val base: BaseInfo,
    val personality: Personality,
    val behavior: Behavior
)

data class BaseInfo(
    val name: String,
    val age: Int,
    val archetype: String
)

data class Personality(
    val BigFive: BigFiveTraits,
    val coping_style: String,
    val moral_principles: List<String>,
    val cognitive_biases: List<String>
)

data class BigFiveTraits(
    val openness: Double,
    val conscientiousness: Double,
    val extraversion: Double,
    val agreeableness: Double,
    val neuroticism: Double
)

data class Behavior(
    val response_speed: Double,
    val typing_style: String,
    val emoji_usage: Double
) 