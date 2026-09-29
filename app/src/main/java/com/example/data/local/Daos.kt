package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FirestoreDao {
    @Query("SELECT * FROM firestore_documents ORDER BY updatedAt DESC")
    fun getAllDocuments(): Flow<List<FirestoreEntity>>

    @Query("SELECT * FROM firestore_documents WHERE collectionName = :collection ORDER BY updatedAt DESC")
    fun getDocumentsByCollection(collection: String): Flow<List<FirestoreEntity>>

    @Query("SELECT DISTINCT collectionName FROM firestore_documents")
    fun getAllCollections(): Flow<List<String>>

    @Query("SELECT * FROM firestore_documents WHERE docId = :docId LIMIT 1")
    suspend fun getDocumentById(docId: String): FirestoreEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(entity: FirestoreEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<FirestoreEntity>)

    @Update
    suspend fun updateDocument(entity: FirestoreEntity)

    @Delete
    suspend fun deleteDocument(entity: FirestoreEntity)

    @Query("DELETE FROM firestore_documents WHERE docId = :docId")
    suspend fun deleteDocumentById(docId: String)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearHistory()
}

@Dao
interface ToolDao {
    @Query("SELECT * FROM tool_definitions")
    fun getAllTools(): Flow<List<ToolDefinitionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTool(tool: ToolDefinitionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTools(tools: List<ToolDefinitionEntity>)

    @Update
    suspend fun updateTool(tool: ToolDefinitionEntity)

    @Delete
    suspend fun deleteTool(tool: ToolDefinitionEntity)
}

@Dao
interface AuthUserDao {
    @Query("SELECT * FROM auth_users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<AuthUserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: AuthUserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<AuthUserEntity>)

    @Update
    suspend fun updateUser(user: AuthUserEntity)

    @Query("DELETE FROM auth_users WHERE uid = :uid")
    suspend fun deleteUser(uid: String)
}
