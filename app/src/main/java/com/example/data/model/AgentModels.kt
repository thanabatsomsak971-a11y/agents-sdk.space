package com.example.data.model

import java.util.UUID

enum class MessageRole {
    USER,
    MODEL,
    TOOL_CALL,
    TOOL_RESPONSE,
    SYSTEM
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: MessageRole,
    val text: String = "",
    val toolCallName: String? = null,
    val toolCallArgs: String? = null,
    val toolResponseResult: String? = null,
    val audioDurationMs: Long? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class ToolDefinition(
    val name: String,
    val description: String,
    val parametersJson: String,
    val isEnabled: Boolean = true,
    val isBuiltIn: Boolean = false
)

data class FirestoreDocItem(
    val docId: String,
    val collectionName: String,
    val dataJson: String,
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: String = "SYNCED" // "SYNCED", "PENDING", "LOCAL"
)

data class AuthUserItem(
    val uid: String,
    val email: String,
    val displayName: String,
    val provider: String = "password", // "google.com", "password", "anonymous"
    val createdAt: Long = System.currentTimeMillis(),
    val isEnabled: Boolean = true
)

data class CloudFunctionItem(
    val name: String,
    val triggerType: String, // "HTTP / Agent SDK", "Firestore onWrite", "Auth onCreate"
    val region: String = "asia-east1",
    val invocations: Long,
    val avgLatencyMs: Int,
    val status: String = "ACTIVE",
    val lastExecutedAt: Long = System.currentTimeMillis()
)

data class AgentSdkConfig(
    val modelName: String = "gemini-3.5-flash",
    val liveModelName: String = "gemini-3.1-flash-live-preview",
    val voiceName: String = "Zephyr",
    val thinkingLevel: String = "MINIMAL",
    val mediaResolution: String = "MEDIUM",
    val triggerTokens: String = "104857",
    val slidingWindowTokens: String = "52428",
    val systemInstruction: String = "You are an intelligent multimodal AI Agent orchestrator for Agent SDK Space. You have access to real-time tools, Firebase Firestore data, and Google Search.",
    val isGoogleSearchEnabled: Boolean = true,
    val responseModality: String = "AUDIO_AND_TEXT" // "AUDIO", "TEXT", "AUDIO_AND_TEXT"
)
