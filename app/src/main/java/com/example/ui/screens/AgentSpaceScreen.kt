package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentSdkConfig
import com.example.data.model.ChatMessage
import com.example.data.model.MessageRole
import com.example.ui.components.AudioWaveVisualizer
import com.example.ui.components.ToolExecutionCard
import com.example.ui.theme.AgentCyan
import com.example.ui.theme.CodeBlockBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DeepDarkBackground
import com.example.ui.theme.FirebaseAmber
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.AgentSpaceViewModel
import com.example.ui.viewmodel.LiveSessionStatus

@Composable
fun AgentSpaceScreen(
    viewModel: AgentSpaceViewModel,
    config: AgentSdkConfig,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messages.collectAsState()
    val sessionStatus by viewModel.sessionStatus.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()
    val isAutoPlay by viewModel.isAudioAutoPlay.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepDarkBackground)
    ) {
        // --- Live Session Top Control Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            when (sessionStatus) {
                                LiveSessionStatus.CONNECTED -> SuccessGreen
                                LiveSessionStatus.STREAMING_AUDIO -> AgentCyan
                                LiveSessionStatus.EXECUTING_TOOL -> FirebaseAmber
                                LiveSessionStatus.CONNECTING -> Color(0xFFFFB300)
                                LiveSessionStatus.DISCONNECTED -> Color.Gray
                            }
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "LIVE SESSION",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = when (sessionStatus) {
                            LiveSessionStatus.CONNECTED -> "Active • ${config.voiceName} Voice"
                            LiveSessionStatus.STREAMING_AUDIO -> "Streaming Audio (${config.voiceName})"
                            LiveSessionStatus.EXECUTING_TOOL -> "Tool Dispatching..."
                            LiveSessionStatus.CONNECTING -> "Connecting WebRTC..."
                            LiveSessionStatus.DISCONNECTED -> "Session Paused"
                        },
                        color = when (sessionStatus) {
                            LiveSessionStatus.CONNECTED -> SuccessGreen
                            LiveSessionStatus.STREAMING_AUDIO -> AgentCyan
                            LiveSessionStatus.EXECUTING_TOOL -> FirebaseAmber
                            else -> Color.Gray
                        },
                        fontSize = 11.sp
                    )
                }
            }

            // Audio Waveform
            AudioWaveVisualizer(
                isSpeaking = isSpeaking,
                barCount = 14,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // Actions
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.toggleAutoPlay() },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("toggle_audio_autoplay")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Toggle Auto Voice",
                        tint = if (isAutoPlay) FirebaseAmber else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.toggleSessionConnection() },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("toggle_session_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Session Power",
                        tint = if (sessionStatus != LiveSessionStatus.DISCONNECTED) SuccessGreen else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.clearChat() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear Chat",
                        tint = Color.LightGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // --- Chat & Tool Turn List ---
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                when (msg.role) {
                    MessageRole.USER -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp))
                                    .background(Color(0xFF1E3A5F))
                                    .border(1.dp, Color(0xFF2E5984), RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    MessageRole.MODEL -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(DarkSurfaceElevated)
                                    .border(1.dp, AgentCyan.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = "Agent",
                                    tint = AgentCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(modifier = Modifier.weight(1f, fill = false)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp))
                                        .background(DarkSurface)
                                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp))
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = msg.text,
                                            color = Color(0xFFE2E8F0),
                                            fontSize = 14.sp,
                                            lineHeight = 20.sp
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(Color(0xFF0F172A))
                                                    .clickable {
                                                        viewModel.speakMessage(msg.text, config.voiceName)
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = "Play Audio",
                                                    tint = FirebaseAmber,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Listen (${config.voiceName})",
                                                    color = FirebaseAmber,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    MessageRole.TOOL_CALL -> {
                        ToolExecutionCard(
                            toolName = msg.toolCallName ?: "Function",
                            argsJson = msg.toolCallArgs,
                            resultJson = null,
                            isCompleted = false
                        )
                    }

                    MessageRole.TOOL_RESPONSE -> {
                        ToolExecutionCard(
                            toolName = msg.toolCallName ?: "Function",
                            argsJson = null,
                            resultJson = msg.toolResponseResult,
                            isCompleted = true
                        )
                    }

                    else -> {}
                }
            }
        }

        // --- Quick Suggestion Action Chips ---
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val chips = listOf(
                "Query Firestore" to "Query fear best console Firestore database",
                "Firebase Stats" to "What is the status of fear best console?",
                "Google Search" to "Search latest news on Agent SDK Space",
                "Voice Hello" to "Say hello in Zephyr voice"
            )
            items(chips) { (label, prompt) ->
                SuggestionChip(
                    onClick = {
                        viewModel.sendUserTurn(prompt, config)
                    },
                    label = {
                        Text(
                            text = label,
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = DarkSurfaceElevated
                    ),
                    border = SuggestionChipDefaults.suggestionChipBorder(
                        borderColor = DarkSurfaceBorder,
                        enabled = true
                    )
                )
            }
        }

        // --- Input Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("agent_input_field"),
                placeholder = {
                    Text(
                        text = "Message Agent or type a tool command...",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                },
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = AgentCyan,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedContainerColor = CodeBlockBackground,
                    unfocusedContainerColor = CodeBlockBackground
                ),
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Mic simulation button
            IconButton(
                onClick = {
                    inputText = "Live audio input: Test turn with Zephyr voice"
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E293B))
                    .testTag("mic_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = FirebaseAmber,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Send button
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendUserTurn(inputText, config)
                        inputText = ""
                    }
                },
                enabled = inputText.isNotBlank(),
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (inputText.isNotBlank()) FirebaseAmber else Color(0xFF334155))
                    .testTag("send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (inputText.isNotBlank()) Color.Black else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
