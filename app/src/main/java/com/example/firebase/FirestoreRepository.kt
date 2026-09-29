package com.example.firebase

import android.content.Context
import android.util.Log
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

private const val TAG = "FirestoreRepo"

class FirestoreRepository(context: Context) {
    // CRITICAL: Always use named database ID from R.string.firestore_database_id
    private val databaseId = context.getString(R.string.firestore_database_id)
    val db: FirebaseFirestore = FirebaseFirestore.getInstance(databaseId)
    private val auth = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid ?: "unauthenticated"
    }

    suspend fun saveAgent(agentId: String, name: String, model: String, voice: String, modality: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: "anonymous"
            val payload = hashMapOf<String, Any>(
                "id" to agentId,
                "userId" to uid,
                "name" to name,
                "model" to model,
                "voice" to voice,
                "modality" to modality,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("agents").document(agentId).set(payload).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving agent to Firestore", e)
            Result.failure(e)
        }
    }

    suspend fun saveSession(sessionId: String, model: String, turnsCount: Int): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: "anonymous"
            val payload = hashMapOf<String, Any>(
                "id" to sessionId,
                "userId" to uid,
                "sessionId" to sessionId,
                "model" to model,
                "turnsCount" to turnsCount,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("sessions").document(sessionId).set(payload).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving session to Firestore", e)
            Result.failure(e)
        }
    }

    suspend fun deleteDocument(collection: String, docId: String): Result<Unit> {
        return try {
            db.collection(collection).document(docId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting document $docId from $collection", e)
            Result.failure(e)
        }
    }
}
