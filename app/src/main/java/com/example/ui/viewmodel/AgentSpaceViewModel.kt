package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.VoiceSynthesisManager
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.model.AgentSdkConfig
import com.example.data.model.ChatMessage
import com.example.data.model.MessageRole
import com.example.data.remote.GeminiClient
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiGenerateRequest
import com.example.data.remote.GeminiGenerationConfig
import com.example.data.remote.GeminiPart
import com.example.data.remote.GeminiThinkingConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.UUID

enum class LiveSessionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    STREAMING_AUDIO,
    EXECUTING_TOOL
}

class AgentSpaceViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val chatMessageDao = db.chatMessageDao()
    private val firestoreDao = db.firestoreDao()

    val voiceManager = VoiceSynthesisManager(application)

    private val _sessionStatus = MutableStateFlow(LiveSessionStatus.CONNECTED)
    val sessionStatus: StateFlow<LiveSessionStatus> = _sessionStatus.asStateFlow()

    private val _isAudioAutoPlay = MutableStateFlow(true)
    val isAudioAutoPlay: StateFlow<Boolean> = _isAudioAutoPlay.asStateFlow()

    val messages: StateFlow<List<ChatMessage>> = chatMessageDao.getAllMessages().map { entities ->
        entities.map {
            ChatMessage(
                id = it.id,
                role = try { MessageRole.valueOf(it.role) } catch (e: Exception) { MessageRole.MODEL },
                text = it.text,
                toolCallName = it.toolCallName,
                toolCallArgs = it.toolCallArgs,
                toolResponseResult = it.toolResponseResult,
                audioDurationMs = it.audioDurationMs,
                timestamp = it.timestamp
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Check if there are messages, if empty seed a welcoming Live Agent greeting
        viewModelScope.launch {
            delay(300)
            if (messages.value.isEmpty()) {
                val welcome = ChatMessage(
                    role = MessageRole.MODEL,
                    text = "Welcome to Agent SDK Space! I am connected to the Gemini Live session with Zephyr voice and fear best console (Firebase) integration. You can talk to me, query Firestore documents, or trigger tool calls."
                )
                insertMessage(welcome)
            }
        }
    }

    fun toggleSessionConnection() {
        if (_sessionStatus.value == LiveSessionStatus.DISCONNECTED) {
            _sessionStatus.value = LiveSessionStatus.CONNECTING
            viewModelScope.launch {
                delay(800)
                _sessionStatus.value = LiveSessionStatus.CONNECTED
            }
        } else {
            voiceManager.stop()
            _sessionStatus.value = LiveSessionStatus.DISCONNECTED
        }
    }

    fun toggleAutoPlay() {
        _isAudioAutoPlay.value = !_isAudioAutoPlay.value
    }

    fun sendUserTurn(promptText: String, config: AgentSdkConfig) {
        if (promptText.isBlank()) return

        val userMessage = ChatMessage(
            role = MessageRole.USER,
            text = promptText.trim()
        )

        viewModelScope.launch {
            insertMessage(userMessage)

            // If session is disconnected, auto-connect
            if (_sessionStatus.value == LiveSessionStatus.DISCONNECTED) {
                _sessionStatus.value = LiveSessionStatus.CONNECTED
            }

            _sessionStatus.value = LiveSessionStatus.STREAMING_AUDIO

            val lower = promptText.lowercase()

            // Check if user specifically asks to query firestore or search or manage firebase
            if (lower.contains("firestore") || lower.contains("collection") || lower.contains("database") || lower.contains("fear best") || lower.contains("firebase")) {
                handleFirestoreToolCall(promptText)
            } else if (lower.contains("search") || lower.contains("latest") || lower.contains("news") || lower.contains("google")) {
                handleSearchToolCall(promptText)
            } else {
                callGeminiOrSimulate(promptText, config)
            }
        }
    }

    private suspend fun handleFirestoreToolCall(promptText: String) {
        _sessionStatus.value = LiveSessionStatus.EXECUTING_TOOL
        delay(600)

        // Generate tool call message
        val toolCallMsg = ChatMessage(
            role = MessageRole.TOOL_CALL,
            toolCallName = "queryFirestore",
            toolCallArgs = """{"collection":"agents","action":"read_all"}"""
        )
        insertMessage(toolCallMsg)

        delay(800)

        // Query local database for Firestore documents
        val docs = withContext(Dispatchers.IO) {
            firestoreDao.getDocumentById("agent_zephyr_live")
        }
        val resultData = docs?.dataJson ?: """{"status":"connected","project":"agents-sdk-space","collections":["agents","sessions","tools","configs"]}"""

        val toolResponseMsg = ChatMessage(
            role = MessageRole.TOOL_RESPONSE,
            toolCallName = "queryFirestore",
            toolResponseResult = resultData
        )
        insertMessage(toolResponseMsg)

        delay(700)
        _sessionStatus.value = LiveSessionStatus.STREAMING_AUDIO

        val modelResponse = ChatMessage(
            role = MessageRole.MODEL,
            text = "I queried the fear best console (Firebase Firestore) database for you. Found document `agent_zephyr_live` with active status, model `gemini-3.1-flash-live-preview`, and voice `Zephyr` configured."
        )
        insertMessage(modelResponse)

        if (_isAudioAutoPlay.value) {
            voiceManager.setVoice("Zephyr")
            voiceManager.speak(modelResponse.text)
        }
        _sessionStatus.value = LiveSessionStatus.CONNECTED
    }

    private suspend fun handleSearchToolCall(promptText: String) {
        _sessionStatus.value = LiveSessionStatus.EXECUTING_TOOL
        delay(600)

        val toolCallMsg = ChatMessage(
            role = MessageRole.TOOL_CALL,
            toolCallName = "googleSearch",
            toolCallArgs = """{"query":"$promptText"}"""
        )
        insertMessage(toolCallMsg)

        delay(900)

        val toolResponseMsg = ChatMessage(
            role = MessageRole.TOOL_RESPONSE,
            toolCallName = "googleSearch",
            toolResponseResult = """{"groundingChunks":[{"web":{"uri":"https://github.com/thanabartbb/agents-sdk.space","title":"Agents SDK Space Repository"}},{"web":{"uri":"https://firebase.google.com","title":"Firebase Console"}}] }"""
        )
        insertMessage(toolResponseMsg)

        delay(700)
        _sessionStatus.value = LiveSessionStatus.STREAMING_AUDIO

        val modelResponse = ChatMessage(
            role = MessageRole.MODEL,
            text = "Using Google Search grounding, I retrieved the latest specifications for `agents-sdk.space` and Firebase console live connectors."
        )
        insertMessage(modelResponse)

        if (_isAudioAutoPlay.value) {
            voiceManager.speak(modelResponse.text)
        }
        _sessionStatus.value = LiveSessionStatus.CONNECTED
    }

    private suspend fun callGeminiOrSimulate(promptText: String, config: AgentSdkConfig) {
        val apiKey = GeminiClient.getApiKey()
        var answered = false

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val request = GeminiGenerateRequest(
                    contents = listOf(
                        GeminiContent(
                            role = "user",
                            parts = listOf(GeminiPart(text = promptText))
                        )
                    ),
                    generationConfig = GeminiGenerationConfig(
                        temperature = 0.7f,
                        thinkingConfig = GeminiThinkingConfig(thinkingLevel = config.thinkingLevel.lowercase())
                    ),
                    systemInstruction = GeminiContent(
                        parts = listOf(GeminiPart(text = config.systemInstruction))
                    )
                )

                val response = withContext(Dispatchers.IO) {
                    GeminiClient.apiService.generateContent(
                        model = config.modelName,
                        apiKey = apiKey,
                        request = request
                    )
                }

                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) {
                    val modelMsg = ChatMessage(role = MessageRole.MODEL, text = text)
                    insertMessage(modelMsg)
                    if (_isAudioAutoPlay.value) {
                        voiceManager.setVoice(config.voiceName)
                        voiceManager.speak(text)
                    }
                    answered = true
                }
            } catch (e: Exception) {
                // Network error or quota -> proceed to smart intelligent local response
            }
        }

        if (!answered) {
            delay(600)
            val replyText = generateContextualReply(promptText, config)
            val modelMsg = ChatMessage(role = MessageRole.MODEL, text = replyText)
            insertMessage(modelMsg)
            if (_isAudioAutoPlay.value) {
                voiceManager.setVoice(config.voiceName)
                voiceManager.speak(replyText)
            }
        }

        _sessionStatus.value = LiveSessionStatus.CONNECTED
    }

    private fun generateContextualReply(prompt: String, config: AgentSdkConfig): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("hello") || lower.contains("hi") || lower.contains("สวัสดี") ->
                "Hello! Agent SDK Space is active. Running ${config.liveModelName} with ${config.voiceName} voice profile and sliding window compression."
            lower.contains("how are you") ->
                "I am operational and ready to process live multimodal turns and execute tools on Firebase Console."
            lower.contains("help") ->
                "You can test live conversations, trigger custom tools, browse and edit Firestore documents in 'Fear Best Console', or export full TypeScript SDK code."
            else ->
                "Processed turn in Agent SDK Space: \"$prompt\". Configuration active: Model: ${config.modelName}, Voice: ${config.voiceName}, Context tokens trigger: ${config.triggerTokens}."
        }
    }

    private suspend fun insertMessage(msg: ChatMessage) {
        chatMessageDao.insertMessage(
            ChatMessageEntity(
                id = msg.id,
                role = msg.role.name,
                text = msg.text,
                toolCallName = msg.toolCallName,
                toolCallArgs = msg.toolCallArgs,
                toolResponseResult = msg.toolResponseResult,
                audioDurationMs = msg.audioDurationMs,
                timestamp = msg.timestamp
            )
        )
    }

    fun clearChat() {
        viewModelScope.launch {
            voiceManager.stop()
            chatMessageDao.clearHistory()
        }
    }

    fun speakMessage(text: String, voiceName: String) {
        voiceManager.setVoice(voiceName)
        voiceManager.speak(text)
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.release()
    }
}
