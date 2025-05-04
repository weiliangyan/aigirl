package com.emotionalai.companion.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.emotionalai.companion.model.Message
import com.emotionalai.companion.model.Persona
import com.emotionalai.companion.ui.theme.*
import com.emotionalai.companion.voice.RecognitionState
import com.emotionalai.companion.voice.VoiceRecognitionService
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    persona: Persona,
    messages: List<Message>,
    onSendMessage: (String) -> Unit,
    onSendImage: (Uri) -> Unit,
    onVoiceInput: () -> Unit,
    onImageInput: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var messageText by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    
    val voiceService = remember { VoiceRecognitionService(context) }
    val voiceState by voiceService.recognitionState.collectAsState()

    // 图片选择器
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onSendImage(it) }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 顶部栏
        TopAppBar(
            title = { Text(persona.base.name) },
            actions = {
                IconButton(onClick = { /* 打开设置 */ }) {
                    Icon(Icons.Default.Settings, contentDescription = "设置")
                }
            }
        )

        // 消息列表
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                AnimatedMessageCard(
                    isFromUser = message.isFromUser
                ) {
                    Column {
                        Text(
                            text = message.content,
                            color = if (message.isFromUser) 
                                MaterialTheme.colors.onPrimary 
                            else 
                                MaterialTheme.colors.onSurface
                        )
                        message.emotion?.let { emotion ->
                            Row(
                                modifier = Modifier.padding(top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AnimatedEmotionIndicator(emotion)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${emotion.type.name} (${(emotion.intensity * 100).toInt()}%)",
                                    style = MaterialTheme.typography.caption
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // 语音识别状态
        AnimatedVisibility(
            visible = voiceState is RecognitionState.Listening,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedLoadingIndicator()
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "正在聆听...",
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }

        // 输入区域
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 语音输入按钮
            AnimatedVoiceButton(
                isRecording = isRecording,
                onClick = {
                    if (isRecording) {
                        voiceService.stopListening()
                    } else {
                        voiceService.startListening()
                    }
                    isRecording = !isRecording
                }
            )

            // 图片输入按钮
            IconButton(onClick = { imagePicker.launch("image/*") }) {
                Icon(Icons.Default.Image, contentDescription = "选择图片")
            }

            // 文本输入框
            AnimatedInputField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = "输入消息...",
                onSend = {
                    if (messageText.isNotBlank()) {
                        onSendMessage(messageText)
                        messageText = ""
                    }
                }
            )
        }
    }

    // 处理语音识别结果
    LaunchedEffect(voiceState) {
        when (voiceState) {
            is RecognitionState.Result -> {
                val text = (voiceState as RecognitionState.Result).text
                onSendMessage(text)
                isRecording = false
            }
            is RecognitionState.Error -> {
                scope.launch {
                    // TODO: 显示错误提示
                }
                isRecording = false
            }
            else -> {}
        }
    }
}

@Composable
fun MessageItem(message: Message) {
    val alignment = if (message.isFromUser) Alignment.End else Alignment.Start
    val backgroundColor = if (message.isFromUser) MaterialTheme.colors.primary else MaterialTheme.colors.surface
    val contentColor = if (message.isFromUser) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Card(
            modifier = Modifier
                .padding(4.dp)
                .widthIn(max = 340.dp),
            backgroundColor = backgroundColor
        ) {
            when (message.mediaType) {
                MediaType.TEXT -> {
                    Text(
                        text = message.content,
                        modifier = Modifier.padding(8.dp),
                        color = contentColor
                    )
                }
                MediaType.IMAGE -> {
                    // TODO: 显示图片
                    Text(
                        text = "[图片]",
                        modifier = Modifier.padding(8.dp),
                        color = contentColor
                    )
                }
                MediaType.VOICE -> {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "播放语音",
                            tint = contentColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "语音消息",
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
} 