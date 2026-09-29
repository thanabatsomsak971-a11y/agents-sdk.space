package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "firestore_documents")
data class FirestoreEntity(
    @PrimaryKey
    val docId: String,
    val collectionName: String,
    val dataJson: String,
    val updatedAt: Long,
    val syncStatus: String
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val role: String,
    val text: String,
    val toolCallName: String?,
    val toolCallArgs: String?,
    val toolResponseResult: String?,
    val audioDurationMs: Long?,
    val timestamp: Long
)

@Entity(tableName = "tool_definitions")
data class ToolDefinitionEntity(
    @PrimaryKey
    val name: String,
    val description: String,
    val parametersJson: String,
    val isEnabled: Boolean,
    val isBuiltIn: Boolean
)

@Entity(tableName = "auth_users")
data class AuthUserEntity(
    @PrimaryKey
    val uid: String,
    val email: String,
    val displayName: String,
    val provider: String,
    val createdAt: Long,
    val isEnabled: Boolean
)
