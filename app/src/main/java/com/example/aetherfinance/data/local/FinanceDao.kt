package com.example.aetherfinance.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aetherfinance.data.model.CustomTagEntity
import com.example.aetherfinance.data.model.TransactionEntity
import com.example.aetherfinance.data.model.UserPreferenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {

    // Transactions
    @Query("SELECT * FROM transactions ORDER BY date DESC, createdAt DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: String)

    @Query("DELETE FROM transactions")
    suspend fun clearAllTransactions()

    // Tags
    @Query("SELECT * FROM custom_tags ORDER BY createdAt ASC")
    fun getAllTags(): Flow<List<CustomTagEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: CustomTagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<CustomTagEntity>)

    @Update
    suspend fun updateTag(tag: CustomTagEntity)

    @Query("DELETE FROM custom_tags WHERE id = :id")
    suspend fun deleteTagById(id: String)

    @Query("DELETE FROM custom_tags")
    suspend fun clearAllTags()

    // User Preferences
    @Query("SELECT * FROM user_preferences WHERE id = 1 LIMIT 1")
    fun getUserPreferences(): Flow<UserPreferenceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserPreferences(prefs: UserPreferenceEntity)
}
