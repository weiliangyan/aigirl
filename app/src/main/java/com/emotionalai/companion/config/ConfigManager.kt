package com.emotionalai.companion.config

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "llm_config")

class ConfigManager(private val context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val PROVIDER_KEY = stringPreferencesKey("provider")
        private val API_KEY_KEY = stringPreferencesKey("api_key")
        private val MODEL_NAME_KEY = stringPreferencesKey("model_name")
        private val BASE_URL_KEY = stringPreferencesKey("base_url")
        private val TEMPERATURE_KEY = stringPreferencesKey("temperature")
        private val MAX_TOKENS_KEY = stringPreferencesKey("max_tokens")
    }

    suspend fun saveConfig(config: LLMConfig) {
        dataStore.edit { preferences ->
            preferences[PROVIDER_KEY] = config.provider.name
            preferences[API_KEY_KEY] = config.apiKey
            preferences[MODEL_NAME_KEY] = config.modelName
            preferences[BASE_URL_KEY] = config.baseUrl
            preferences[TEMPERATURE_KEY] = config.temperature.toString()
            preferences[MAX_TOKENS_KEY] = config.maxTokens.toString()
        }
    }

    fun getConfig(): Flow<LLMConfig> {
        return dataStore.data.map { preferences ->
            LLMConfig(
                provider = LLMProvider.valueOf(preferences[PROVIDER_KEY] ?: LLMProvider.OPENAI.name),
                apiKey = preferences[API_KEY_KEY] ?: "",
                modelName = preferences[MODEL_NAME_KEY] ?: "gpt-3.5-turbo",
                baseUrl = preferences[BASE_URL_KEY] ?: "",
                temperature = preferences[TEMPERATURE_KEY]?.toFloatOrNull() ?: 0.7f,
                maxTokens = preferences[MAX_TOKENS_KEY]?.toIntOrNull() ?: 2000
            )
        }
    }

    suspend fun clearConfig() {
        dataStore.edit { it.clear() }
    }
} 