package com.emotionalai.companion.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emotionalai.companion.config.LLMConfig
import com.emotionalai.companion.config.LLMProvider

@Composable
fun LLMConfigScreen(
    currentConfig: LLMConfig,
    onSave: (LLMConfig) -> Unit,
    onBack: () -> Unit
) {
    var provider by remember { mutableStateOf(currentConfig.provider) }
    var apiKey by remember { mutableStateOf(currentConfig.apiKey) }
    var modelName by remember { mutableStateOf(currentConfig.modelName) }
    var baseUrl by remember { mutableStateOf(currentConfig.baseUrl) }
    var temperature by remember { mutableStateOf(currentConfig.temperature.toString()) }
    var maxTokens by remember { mutableStateOf(currentConfig.maxTokens.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("大模型配置") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // 提供商选择
            Text("选择提供商", style = MaterialTheme.typography.subtitle1)
            Spacer(modifier = Modifier.height(8.dp))
            LLMProvider.values().forEach { providerOption ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(
                        selected = provider == providerOption,
                        onClick = { provider = providerOption }
                    )
                    Text(providerOption.name)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // API密钥
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API密钥") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 模型名称
            OutlinedTextField(
                value = modelName,
                onValueChange = { modelName = it },
                label = { Text("模型名称") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 基础URL（可选）
            OutlinedTextField(
                value = baseUrl,
                onValueChange = { baseUrl = it },
                label = { Text("基础URL（可选）") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 温度
            OutlinedTextField(
                value = temperature,
                onValueChange = { temperature = it },
                label = { Text("温度 (0.0-1.0)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 最大Token数
            OutlinedTextField(
                value = maxTokens,
                onValueChange = { maxTokens = it },
                label = { Text("最大Token数") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 保存按钮
            Button(
                onClick = {
                    onSave(
                        LLMConfig(
                            provider = provider,
                            apiKey = apiKey,
                            modelName = modelName,
                            baseUrl = baseUrl,
                            temperature = temperature.toFloatOrNull() ?: 0.7f,
                            maxTokens = maxTokens.toIntOrNull() ?: 2000
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("保存配置")
            }
        }
    }
} 