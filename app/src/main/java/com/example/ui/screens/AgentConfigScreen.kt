package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AgentCyan
import com.example.ui.theme.CodeBlockBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DeepDarkBackground
import com.example.ui.theme.FirebaseAmber
import com.example.ui.theme.FirebaseYellow
import com.example.ui.viewmodel.AgentConfigViewModel
import com.example.ui.viewmodel.AgentSpaceViewModel

@Composable
fun AgentConfigScreen(
    configViewModel: AgentConfigViewModel,
    spaceViewModel: AgentSpaceViewModel,
    modifier: Modifier = Modifier
) {
    val config by configViewModel.config.collectAsState()
    val tools by configViewModel.availableTools.collectAsState()

    var showAddToolDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepDarkBackground)
            .verticalScroll(scrollState)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- Header ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = FirebaseAmber)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "AGENT SDK CONFIGURATION",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        // --- Model Selection Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "TARGET MODEL",
                    color = AgentCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                val models = listOf(
                    "gemini-3.5-flash",
                    "gemini-3.1-pro-preview",
                    "gemini-3.1-flash-live-preview"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    models.forEach { m ->
                        FilterChip(
                            selected = config.modelName == m,
                            onClick = { configViewModel.updateModel(m) },
                            label = {
                                Text(
                                    text = if (m.contains("live")) "Live Preview" else m.replace("gemini-", ""),
                                    fontSize = 11.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FirebaseAmber,
                                selectedLabelColor = Color.Black,
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color.White
                            ),
                            modifier = Modifier.testTag("model_chip_$m")
                        )
                    }
                }
            }
        }

        // --- Speech & Voice Config Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VOICE ENGINE (Zephyr PrebuiltVoiceConfig)",
                        color = AgentCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    IconButton(
                        onClick = {
                            spaceViewModel.speakMessage(
                                "Hello, this is ${config.voiceName} voice synthesized for Agent SDK Space.",
                                config.voiceName
                            )
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Test Voice", tint = FirebaseAmber)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val voices = listOf("Zephyr", "Kore", "Puck", "Aoede", "Fenrir")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    voices.forEach { voice ->
                        FilterChip(
                            selected = config.voiceName.equals(voice, ignoreCase = true),
                            onClick = {
                                configViewModel.updateVoice(voice)
                                spaceViewModel.voiceManager.setVoice(voice)
                            },
                            label = { Text(voice, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FirebaseAmber,
                                selectedLabelColor = Color.Black,
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color.White
                            ),
                            modifier = Modifier.testTag("voice_chip_$voice")
                        )
                    }
                }
            }
        }

        // --- Context Window Compression Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "CONTEXT WINDOW COMPRESSION",
                    color = AgentCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = config.triggerTokens,
                        onValueChange = { configViewModel.updateContextCompression(it, config.slidingWindowTokens) },
                        label = { Text("Trigger Tokens") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = config.slidingWindowTokens,
                        onValueChange = { configViewModel.updateContextCompression(config.triggerTokens, it) },
                        label = { Text("Sliding Window") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "THINKING LEVEL:",
                    color = Color.LightGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("MINIMAL", "LOW", "HIGH").forEach { level ->
                        FilterChip(
                            selected = config.thinkingLevel.equals(level, ignoreCase = true),
                            onClick = { configViewModel.updateThinkingLevel(level) },
                            label = { Text(level, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FirebaseAmber,
                                selectedLabelColor = Color.Black,
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // --- System Instruction Editor Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "SYSTEM INSTRUCTION PROMPT",
                    color = AgentCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = config.systemInstruction,
                    onValueChange = { configViewModel.updateSystemInstruction(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = CodeBlockBackground,
                        unfocusedContainerColor = CodeBlockBackground,
                        focusedBorderColor = AgentCyan,
                        unfocusedBorderColor = DarkSurfaceBorder
                    )
                )
            }
        }

        // --- Tools & Function Declarations Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AGENT TOOLS & FUNCTION DECLARATIONS",
                        color = AgentCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Button(
                        onClick = { showAddToolDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = FirebaseAmber),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Tool", color = Color.Black, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                tools.forEach { tool ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tool.name,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = tool.description,
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }

                        Switch(
                            checked = tool.isEnabled,
                            onCheckedChange = { configViewModel.toggleTool(tool.name, tool.isEnabled) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = FirebaseAmber,
                                uncheckedTrackColor = Color(0xFF1E293B)
                            )
                        )
                    }
                }
            }
        }
    }

    if (showAddToolDialog) {
        var toolNameInput by remember { mutableStateOf("") }
        var toolDescInput by remember { mutableStateOf("") }
        var toolParamsInput by remember {
            mutableStateOf("""{"type":"object","properties":{"input":{"type":"string"}}}""")
        }

        AlertDialog(
            onDismissRequest = { showAddToolDialog = false },
            title = { Text("Register Function Declaration", color = Color.White) },
            text = {
                Column {
                    OutlinedTextField(
                        value = toolNameInput,
                        onValueChange = { toolNameInput = it },
                        label = { Text("Function Name (e.g. checkInventory)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = toolDescInput,
                        onValueChange = { toolDescInput = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = toolParamsInput,
                        onValueChange = { toolParamsInput = it },
                        label = { Text("Parameters JSON Schema") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (toolNameInput.isNotBlank()) {
                            configViewModel.addCustomTool(toolNameInput, toolDescInput, toolParamsInput)
                            showAddToolDialog = false
                            Toast.makeText(context, "Tool registered", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FirebaseAmber)
                ) {
                    Text("Register Tool", color = Color.Black)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddToolDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = DarkSurface
        )
    }
}
