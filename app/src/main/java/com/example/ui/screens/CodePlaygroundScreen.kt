package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentSdkConfig
import com.example.ui.theme.AgentCyan
import com.example.ui.theme.CodeBlockBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DeepDarkBackground
import com.example.ui.theme.FirebaseAmber
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.AgentConfigViewModel
import com.example.ui.viewmodel.AgentSpaceViewModel

@Composable
fun CodePlaygroundScreen(
    configViewModel: AgentConfigViewModel,
    spaceViewModel: AgentSpaceViewModel,
    modifier: Modifier = Modifier
) {
    val config by configViewModel.config.collectAsState()
    val tools by configViewModel.availableTools.collectAsState()

    var selectedLangTab by remember { mutableStateOf(0) } // 0: TypeScript (@google/genai), 1: Kotlin (Android)
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val tsCode = remember(config, tools) {
        generateTypeScriptCode(config, tools.filter { it.isEnabled })
    }

    val kotlinCode = remember(config, tools) {
        generateKotlinCode(config)
    }

    val currentCode = if (selectedLangTab == 0) tsCode else kotlinCode

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepDarkBackground)
            .padding(12.dp)
    ) {
        // --- Header and Actions ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = AgentCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AGENT SDK CODE & PLAYGROUND",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Row {
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Agent SDK Code", currentCode)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = FirebaseAmber)
                }

                IconButton(
                    onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, currentCode)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Agent SDK Code"))
                    },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = AgentCyan)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- Language Selector Tabs ---
        TabRow(
            selectedTabIndex = selectedLangTab,
            containerColor = DarkSurfaceElevated,
            contentColor = FirebaseAmber,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedLangTab]),
                    color = FirebaseAmber
                )
            }
        ) {
            Tab(
                selected = selectedLangTab == 0,
                onClick = { selectedLangTab = 0 },
                text = { Text("TypeScript (@google/genai)", fontSize = 11.sp) }
            )
            Tab(
                selected = selectedLangTab == 1,
                onClick = { selectedLangTab = 1 },
                text = { Text("Kotlin (Android SDK)", fontSize = 11.sp) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- Code View Container ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CodeBlockBackground)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = currentCode,
                    color = Color(0xFFE2E8F0),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- Run in Live Agent Session Button ---
        Button(
            onClick = {
                spaceViewModel.sendUserTurn(
                    "Simulate execution of Live Agent session script with ${config.voiceName} voice and fear best console tools.",
                    config
                )
                Toast.makeText(context, "Executing script turn in Live Session...", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = FirebaseAmber),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("run_script_btn")
        ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Run Script in Live Agent Session",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

private fun generateTypeScriptCode(config: AgentSdkConfig, enabledTools: List<com.example.data.model.ToolDefinition>): String {
    val toolFunctionsList = enabledTools.filter { !it.isBuiltIn || it.name != "googleSearch" }.joinToString(",\n        ") { t ->
        """
        {
          name: "${t.name}",
          description: "${t.description}"
        }
        """.trimIndent()
    }

    return """
// Generated by Agent SDK Space for https://github.com/thanabartbb/agents-sdk.space
// Dependencies: npm install @google/genai @types/node

import {
  GoogleGenAI,
  LiveServerMessage,
  MediaResolution,
  Modality,
  Session,
  ThinkingLevel,
} from '@google/genai';
import { writeFile } from 'fs';

const responseQueue: LiveServerMessage[] = [];
let session: Session | undefined = undefined;

async function handleTurn(): Promise<LiveServerMessage[]> {
  const turn: LiveServerMessage[] = [];
  let done = false;
  while (!done) {
    const message = await waitMessage();
    turn.push(message);
    if (message.serverContent && message.serverContent.turnComplete) {
      done = true;
    }
  }
  return turn;
}

async function waitMessage(): Promise<LiveServerMessage> {
  let done = false;
  let message: LiveServerMessage | undefined = undefined;
  while (!done) {
    message = responseQueue.shift();
    if (message) {
      handleModelTurn(message);
      done = true;
    } else {
      await new Promise((resolve) => { setTimeout(resolve, 100); });
    }
  }
  return message!;
}

const audioParts: string[] = [];
function handleModelTurn(message: LiveServerMessage) {
  if (message.toolCall) {
    message.toolCall.functionCalls?.forEach(functionCall => {
      console.log('[Tool Call] ' + functionCall.name + ' args: ' + JSON.stringify(functionCall.args));
    });

    session?.sendToolResponse({
      functionResponses:
        message.toolCall.functionCalls?.map(functionCall => ({
          id: functionCall.id,
          name: functionCall.name,
          response: { status: 'SUCCESS_FROM_FIREBASE_CONSOLE' }
        })) ?? []
    });
  }

  if (message.serverContent?.modelTurn?.parts) {
    const part = message.serverContent?.modelTurn?.parts?.[0];
    if (part?.inlineData) {
      audioParts.push(part.inlineData.data ?? '');
    }
    if (part?.text) {
      console.log('[Zephyr Live] ' + part.text);
    }
  }
}

async function main() {
  const ai = new GoogleGenAI({
    apiKey: process.env['GEMINI_API_KEY'],
  });

  const model = 'models/${config.liveModelName}';

  const tools = [
    ${if (config.isGoogleSearchEnabled) "{ googleSearch: {} }," else ""}
    {
      functionDeclarations: [
        $toolFunctionsList
      ]
    }
  ];

  const config = {
    responseModalities: [Modality.AUDIO],
    mediaResolution: MediaResolution.MEDIA_RESOLUTION_MEDIUM,
    thinkingConfig: {
      thinkingLevel: ThinkingLevel.${config.thinkingLevel},
    },
    speechConfig: {
      voiceConfig: {
        prebuiltVoiceConfig: {
          voiceName: '${config.voiceName}',
        }
      }
    },
    contextWindowCompression: {
      triggerTokens: '${config.triggerTokens}',
      slidingWindow: { targetTokens: '${config.slidingWindowTokens}' },
    },
    tools,
  };

  session = await ai.live.connect({
    model,
    callbacks: {
      onopen: () => console.log('Live connected to Agent SDK Space'),
      onmessage: (msg) => responseQueue.push(msg),
      onerror: (err) => console.error('Error:', err),
      onclose: (close) => console.log('Closed:', close.reason)
    },
    config
  });

  session.sendClientContent({
    turns: ["Connect to fear best console and report agent status."]
  });

  await handleTurn();
  session.close();
}

main();
    """.trimIndent()
}

private fun generateKotlinCode(config: AgentSdkConfig): String {
    return """
// Kotlin Android SDK Implementation
package com.example.agent

import com.example.BuildConfig
import com.example.data.remote.GeminiClient
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiGenerateRequest
import com.example.data.remote.GeminiGenerationConfig
import com.example.data.remote.GeminiPart

suspend fun runAgentTurn(prompt: String): String {
    val apiKey = BuildConfig.GEMINI_API_KEY
    val request = GeminiGenerateRequest(
        contents = listOf(
            GeminiContent(
                role = "user",
                parts = listOf(GeminiPart(text = prompt))
            )
        ),
        generationConfig = GeminiGenerationConfig(
            temperature = 0.7f
        ),
        systemInstruction = GeminiContent(
            parts = listOf(GeminiPart(text = "${config.systemInstruction.take(60)}..."))
        )
    )

    val response = GeminiClient.apiService.generateContent(
        model = "${config.modelName}",
        apiKey = apiKey,
        request = request
    )
    return response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
}
    """.trimIndent()
}
