package com.emotionalai.companion.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedMessageCard(
    modifier: Modifier = Modifier,
    isFromUser: Boolean,
    content: @Composable () -> Unit
) {
    val backgroundColor = if (isFromUser) {
        MaterialTheme.colors.primary
    } else {
        MaterialTheme.colors.surface
    }
    val contentColor = if (isFromUser) {
        MaterialTheme.colors.onPrimary
    } else {
        MaterialTheme.colors.onSurface
    }

    Card(
        modifier = modifier
            .padding(4.dp)
            .widthIn(max = 340.dp)
            .animateContentSize(),
        backgroundColor = backgroundColor,
        elevation = 2.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.padding(12.dp),
            contentAlignment = if (isFromUser) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            content()
        }
    }
}

@Composable
fun AnimatedInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String,
    onSend: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut()
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                placeholder = { Text(placeholder) },
                maxLines = 4,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    focusedIndicatorColor = MaterialTheme.colors.primary,
                    unfocusedIndicatorColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                )
            )
        }

        IconButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.Keyboard else Icons.Default.Edit,
                contentDescription = if (isExpanded) "收起键盘" else "展开输入框"
            )
        }

        if (value.isNotBlank()) {
            IconButton(
                onClick = onSend,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "发送",
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }
}

@Composable
fun AnimatedVoiceButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Icon(
            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
            contentDescription = if (isRecording) "停止录音" else "开始录音",
            tint = if (isRecording) MaterialTheme.colors.error else MaterialTheme.colors.primary
        )
    }
}

@Composable
fun AnimatedEmotionIndicator(
    emotion: Emotion,
    modifier: Modifier = Modifier
) {
    val color = when (emotion.type) {
        EmotionType.HAPPY -> Color(0xFFFFD700)
        EmotionType.SAD -> Color(0xFF4169E1)
        EmotionType.ANGRY -> Color(0xFFFF4500)
        EmotionType.EXCITED -> Color(0xFFFF69B4)
        EmotionType.ANXIOUS -> Color(0xFF9370DB)
        EmotionType.CALM -> Color(0xFF98FB98)
        EmotionType.MELANCHOLY -> Color(0xFF708090)
        else -> MaterialTheme.colors.primary
    }

    Box(
        modifier = modifier
            .size(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color)
    )
}

@Composable
fun AnimatedLoadingIndicator(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        )
    )

    CircularProgressIndicator(
        modifier = modifier
            .size(24.dp)
            .graphicsLayer {
                rotationZ = rotation
            },
        strokeWidth = 2.dp,
        color = MaterialTheme.colors.primary
    )
} 