package com.emotionalai.companion.repository

import android.content.Context
import com.emotionalai.companion.model.Persona
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class PersonaRepository(private val context: Context) {
    private val gson = Gson()

    suspend fun loadPersona(personaName: String): Persona? = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets
                .open("persona/$personaName.json")
                .bufferedReader()
                .use { it.readText() }
            gson.fromJson(jsonString, Persona::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun validatePersona(persona: Persona): Boolean {
        return persona.personality.BigFive.agreeableness in 0.0..1.0 &&
               persona.behavior.emoji_usage in 0.0..1.0 &&
               persona.behavior.response_speed > 0
    }
} 