package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        FirestoreEntity::class,
        ChatMessageEntity::class,
        ToolDefinitionEntity::class,
        AuthUserEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun firestoreDao(): FirestoreDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun toolDao(): ToolDao
    abstract fun authUserDao(): AuthUserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agents_sdk_space_db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed initial data
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getInstance(context)
                            seedInitialData(database)
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedInitialData(db: AppDatabase) {
            val now = System.currentTimeMillis()

            // Seed initial Firebase Auth users (including user from metadata)
            db.authUserDao().insertUsers(
                listOf(
                    AuthUserEntity(
                        uid = "usr_thanabat_01",
                        email = "thanabatsomsak971@gmail.com",
                        displayName = "Thanabat Somsak (Admin)",
                        provider = "google.com",
                        createdAt = now - 86400000 * 7,
                        isEnabled = true
                    ),
                    AuthUserEntity(
                        uid = "usr_agent_live_svc",
                        email = "agent-runner@agents-sdk.space",
                        displayName = "Agents SDK Live Runner Service",
                        provider = "service_account",
                        createdAt = now - 86400000 * 5,
                        isEnabled = true
                    ),
                    AuthUserEntity(
                        uid = "usr_demo_user",
                        email = "developer@space.ai",
                        displayName = "Developer Test Account",
                        provider = "password",
                        createdAt = now - 86400000 * 2,
                        isEnabled = true
                    )
                )
            )

            // Seed initial Firestore collections
            db.firestoreDao().insertAll(
                listOf(
                    FirestoreEntity(
                        docId = "agent_zephyr_live",
                        collectionName = "agents",
                        dataJson = """
                        {
                          "id": "agent_zephyr_live",
                          "name": "Zephyr Live Voice Agent",
                          "model": "gemini-3.1-flash-live-preview",
                          "voice": "Zephyr",
                          "modality": "AUDIO",
                          "thinkingLevel": "MINIMAL",
                          "status": "ready",
                          "toolsEnabled": ["googleSearch", "queryFirestore"]
                        }
                        """.trimIndent(),
                        updatedAt = now,
                        syncStatus = "SYNCED"
                    ),
                    FirestoreEntity(
                        docId = "config_context_compression",
                        collectionName = "configs",
                        dataJson = """
                        {
                          "triggerTokens": 104857,
                          "slidingWindowTokens": 52428,
                          "mediaResolution": "MEDIA_RESOLUTION_MEDIUM",
                          "maxDurationSeconds": 3600,
                          "autoDisconnectIdleMs": 300000
                        }
                        """.trimIndent(),
                        updatedAt = now - 3600000,
                        syncStatus = "SYNCED"
                    ),
                    FirestoreEntity(
                        docId = "session_live_001",
                        collectionName = "sessions",
                        dataJson = """
                        {
                          "sessionId": "sess_live_834871928505",
                          "model": "models/gemini-3.1-flash-live-preview",
                          "turnsCount": 12,
                          "status": "active",
                          "appUrl": "https://ais-dev-e5or6cqxg5gdxqdjlfivyj-834871928505.asia-east1.run.app"
                        }
                        """.trimIndent(),
                        updatedAt = now - 1800000,
                        syncStatus = "SYNCED"
                    ),
                    FirestoreEntity(
                        docId = "tool_firebase_sync",
                        collectionName = "tools",
                        dataJson = """
                        {
                          "toolName": "queryFirestore",
                          "type": "database_accessor",
                          "timeoutMs": 5000,
                          "allowWrite": false,
                          "description": "Reads data collections from fear best console Firestore database."
                        }
                        """.trimIndent(),
                        updatedAt = now - 7200000,
                        syncStatus = "SYNCED"
                    )
                )
            )

            // Seed initial Tool Definitions
            db.toolDao().insertTools(
                listOf(
                    ToolDefinitionEntity(
                        name = "googleSearch",
                        description = "Enables real-time Google Search grounding to retrieve up-to-date web knowledge.",
                        parametersJson = "{}",
                        isEnabled = true,
                        isBuiltIn = true
                    ),
                    ToolDefinitionEntity(
                        name = "queryFirestore",
                        description = "Query documents and collections from the Firebase Firestore console database.",
                        parametersJson = """{"type":"object","properties":{"collection":{"type":"string","description":"Collection name e.g. agents, sessions, tools"},"docId":{"type":"string","description":"Optional document ID"}},"required":["collection"]}""",
                        isEnabled = true,
                        isBuiltIn = true
                    ),
                    ToolDefinitionEntity(
                        name = "getFirebaseProjectStats",
                        description = "Retrieves live Firebase project status, active region, active auth users count, and latency.",
                        parametersJson = """{"type":"object","properties":{"metrics":{"type":"array","items":{"type":"string"}}}}""",
                        isEnabled = true,
                        isBuiltIn = true
                    ),
                    ToolDefinitionEntity(
                        name = "runAgentSdkWebhook",
                        description = "Dispatches a webhook payload to external Agent SDK Space microservices.",
                        parametersJson = """{"type":"object","properties":{"endpoint":{"type":"string"},"payload":{"type":"object"}},"required":["endpoint"]}""",
                        isEnabled = true,
                        isBuiltIn = false
                    )
                )
            )
        }
    }
}
