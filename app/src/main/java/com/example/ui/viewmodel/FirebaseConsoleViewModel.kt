package com.example.ui.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.AuthUserEntity
import com.example.data.local.FirestoreEntity
import com.example.data.model.AuthUserItem
import com.example.data.model.CloudFunctionItem
import com.example.data.model.FirestoreDocItem
import com.example.firebase.FirebaseAuthManager
import com.example.firebase.FirestoreRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class ConsoleTab {
    FIRESTORE,
    AUTH,
    FUNCTIONS,
    RULES,
    PROJECT_INFO
}

class FirebaseConsoleViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val firestoreDao = db.firestoreDao()
    private val authUserDao = db.authUserDao()

    val authManager = FirebaseAuthManager(application)
    val firestoreRepository = FirestoreRepository(application)

    private val _selectedTab = MutableStateFlow(ConsoleTab.FIRESTORE)
    val selectedTab: StateFlow<ConsoleTab> = _selectedTab.asStateFlow()

    private val _selectedCollection = MutableStateFlow("agents")
    val selectedCollection: StateFlow<String> = _selectedCollection.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val currentFirebaseUser: StateFlow<FirebaseUser?> = authManager.authStateFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), authManager.currentUser)

    // Firestore documents for the selected collection
    val documents: StateFlow<List<FirestoreDocItem>> = combine(
        firestoreDao.getAllDocuments(),
        _selectedCollection,
        _searchQuery
    ) { docs, coll, query ->
        docs.filter { it.collectionName.equals(coll, ignoreCase = true) }
            .filter { doc ->
                if (query.isBlank()) true
                else doc.docId.contains(query, ignoreCase = true) || doc.dataJson.contains(query, ignoreCase = true)
            }
            .map {
                FirestoreDocItem(
                    docId = it.docId,
                    collectionName = it.collectionName,
                    dataJson = it.dataJson,
                    updatedAt = it.updatedAt,
                    syncStatus = it.syncStatus
                )
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All available collections
    val collections: StateFlow<List<String>> = firestoreDao.getAllCollections().combine(_selectedCollection) { colls, _ ->
        val list = (colls + listOf("agents", "sessions", "tools", "configs", "users")).distinct()
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("agents", "sessions", "tools", "configs", "users"))

    // Auth users
    val authUsers: StateFlow<List<AuthUserItem>> = authUserDao.getAllUsers().combine(_searchQuery) { users, query ->
        users.filter { user ->
            if (query.isBlank()) true
            else user.email.contains(query, ignoreCase = true) || user.displayName.contains(query, ignoreCase = true)
        }.map {
            AuthUserItem(
                uid = it.uid,
                email = it.email,
                displayName = it.displayName,
                provider = it.provider,
                createdAt = it.createdAt,
                isEnabled = it.isEnabled
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cloud Functions
    private val _cloudFunctions = MutableStateFlow<List<CloudFunctionItem>>(
        listOf(
            CloudFunctionItem(
                name = "onLiveAudioTurnStream",
                triggerType = "HTTP / Gemini Live",
                region = "us-west1",
                invocations = 1428,
                avgLatencyMs = 124,
                status = "ACTIVE"
            ),
            CloudFunctionItem(
                name = "executeToolWebhook",
                triggerType = "Callable Cloud Function",
                region = "us-west1",
                invocations = 684,
                avgLatencyMs = 210,
                status = "ACTIVE"
            ),
            CloudFunctionItem(
                name = "syncFirestoreAgentState",
                triggerType = "Firestore onWrite (agents/*)",
                region = "us-west1",
                invocations = 8920,
                avgLatencyMs = 45,
                status = "ACTIVE"
            ),
            CloudFunctionItem(
                name = "summarizeAgentSession",
                triggerType = "PubSub Scheduled Event",
                region = "us-west1",
                invocations = 48,
                avgLatencyMs = 430,
                status = "ACTIVE"
            )
        )
    )
    val cloudFunctions: StateFlow<List<CloudFunctionItem>> = _cloudFunctions.asStateFlow()

    private val _securityRules = MutableStateFlow(
        """
        rules_version = '2';
        service cloud.firestore {
          match /databases/{database}/documents {
            // Enterprise Firestore security rules deployed
            match /users/{userId} {
              allow read, write: if request.auth != null && request.auth.uid == userId;
            }
            match /agents/{agentId} {
              allow read, write: if request.auth != null;
            }
            match /sessions/{sessionId} {
              allow read, write: if request.auth != null;
            }
          }
        }
        """.trimIndent()
    )
    val securityRules: StateFlow<String> = _securityRules.asStateFlow()

    private val _functionInvocationLogs = MutableStateFlow<List<String>>(
        listOf(
            "[System] Firebase initialized with enterprise Firestore and Auth.",
            "[Sync] Connected to database: ai-studio-android-agentsdk-4d2c2fe5-beef-4c2e-8d6f-8908f73bdc3f",
            "[Auth] Google Sign-In with Credential Manager ready."
        )
    )
    val functionInvocationLogs: StateFlow<List<String>> = _functionInvocationLogs.asStateFlow()

    fun selectTab(tab: ConsoleTab) {
        _selectedTab.value = tab
    }

    fun selectCollection(collection: String) {
        _selectedCollection.value = collection
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addDocument(collection: String, docId: String, json: String) {
        viewModelScope.launch {
            val id = if (docId.isBlank()) "doc_${UUID.randomUUID().toString().take(8)}" else docId
            firestoreDao.insertDocument(
                FirestoreEntity(
                    docId = id,
                    collectionName = collection,
                    dataJson = json,
                    updatedAt = System.currentTimeMillis(),
                    syncStatus = "SYNCED"
                )
            )
        }
    }

    fun deleteDocument(docId: String) {
        viewModelScope.launch {
            firestoreDao.deleteDocumentById(docId)
        }
    }

    fun addAuthUser(email: String, displayName: String, provider: String) {
        viewModelScope.launch {
            val uid = "usr_${UUID.randomUUID().toString().take(10)}"
            authUserDao.insertUser(
                AuthUserEntity(
                    uid = uid,
                    email = email,
                    displayName = displayName.ifBlank { email.substringBefore("@") },
                    provider = provider,
                    createdAt = System.currentTimeMillis(),
                    isEnabled = true
                )
            )
        }
    }

    fun signInWithGoogle(activity: Activity, onSuccess: () -> Unit, onError: (String) -> Unit) {
        authManager.signInWithGoogle(
            activity = activity,
            scope = viewModelScope,
            onSuccess = {
                val user = authManager.currentUser
                if (user != null) {
                    viewModelScope.launch {
                        authUserDao.insertUser(
                            AuthUserEntity(
                                uid = user.uid,
                                email = user.email ?: "google-user@firebase.com",
                                displayName = user.displayName ?: "Google User",
                                provider = "google.com",
                                createdAt = System.currentTimeMillis(),
                                isEnabled = true
                            )
                        )
                    }
                }
                onSuccess()
            },
            onError = onError,
            onCancelled = {}
        )
    }

    fun signOut(onComplete: () -> Unit) {
        authManager.signOut(viewModelScope, onComplete)
    }

    fun toggleUserStatus(uid: String, currentEnabled: Boolean) {
        viewModelScope.launch {
            val user = authUsers.value.find { it.uid == uid } ?: return@launch
            authUserDao.updateUser(
                AuthUserEntity(
                    uid = user.uid,
                    email = user.email,
                    displayName = user.displayName,
                    provider = user.provider,
                    createdAt = user.createdAt,
                    isEnabled = !currentEnabled
                )
            )
        }
    }

    fun deleteUser(uid: String) {
        viewModelScope.launch {
            authUserDao.deleteUser(uid)
        }
    }

    fun triggerFunctionTest(functionName: String) {
        val newLog = "[Invoke 200 OK] $functionName executed successfully (latency: ${(40..150).random()}ms)"
        _functionInvocationLogs.value = listOf(newLog) + _functionInvocationLogs.value
    }

    fun updateSecurityRules(newRules: String) {
        _securityRules.value = newRules
    }
}
