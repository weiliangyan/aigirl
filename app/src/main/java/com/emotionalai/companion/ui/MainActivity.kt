package com.emotionalai.companion.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emotionalai.companion.config.ConfigManager
import com.emotionalai.companion.config.LLMConfig
import com.emotionalai.companion.config.LLMProvider
import com.emotionalai.companion.model.Message
import com.emotionalai.companion.model.Persona
import com.emotionalai.companion.voice.VoiceRecognitionService
import com.emotionalai.companion.image.ImageProcessingService

class MainActivity : ComponentActivity() {
    private lateinit var configManager: ConfigManager
    private lateinit var voiceService: VoiceRecognitionService
    private lateinit var imageService: ImageProcessingService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configManager = ConfigManager(this)
        voiceService = VoiceRecognitionService(this)
        imageService = ImageProcessingService(this)

        setContent {
            MaterialTheme {
                MainScreen(
                    configManager = configManager,
                    voiceService = voiceService,
                    imageService = imageService
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceService.destroy()
    }
}

@Composable
fun MainScreen(
    configManager: ConfigManager,
    voiceService: VoiceRecognitionService,
    imageService: ImageProcessingService
) {
    val navController = rememberNavController()
    var showConfig by remember { mutableStateOf(false) }
    var currentConfig by remember { mutableStateOf(LLMConfig(LLMProvider.OPENAI)) }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var currentPersona by remember { mutableStateOf(Persona()) }

    // 收集配置更新
    LaunchedEffect(Unit) {
        configManager.getConfig().collect { config ->
            currentConfig = config
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("情感AI助手") },
                actions = {
                    IconButton(onClick = { showConfig = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(navController = navController, startDestination = "chat") {
                composable("chat") {
                    ChatScreen(
                        persona = currentPersona,
                        messages = messages,
                        onSendMessage = { text ->
                            messages = messages + Message(
                                content = text,
                                isFromUser = true
                            )
                        },
                        onSendImage = { uri ->
                            // 处理图片
                        },
                        onVoiceInput = {
                            // 处理语音输入
                        },
                        onImageInput = {
                            // 处理图片输入
                        }
                    )
                }
            }

            // 配置对话框
            if (showConfig) {
                AlertDialog(
                    onDismissRequest = { showConfig = false },
                    title = { Text("大模型配置") },
                    text = {
                        LLMConfigScreen(
                            currentConfig = currentConfig,
                            onSave = { config ->
                                // 保存配置
                            },
                            onBack = { showConfig = false }
                        )
                    },
                    confirmButton = {},
                    dismissButton = {}
                )
            }
        }
    }
} 