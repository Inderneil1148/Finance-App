package com.example.aetherfinance.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.aetherfinance.data.model.CustomTagEntity
import com.example.aetherfinance.data.model.TransactionEntity
import com.example.aetherfinance.data.model.UserPreferenceEntity
import com.example.aetherfinance.data.util.FinanceConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TransactionEntity::class,
        CustomTagEntity::class,
        UserPreferenceEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun financeDao(): FinanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aether_finance.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate default seed data on first install
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getInstance(context).financeDao()
                                dao.insertTags(FinanceConstants.DEFAULT_TAGS)
                                dao.insertTransactions(FinanceConstants.generateSeedTransactions())
                                dao.saveUserPreferences(UserPreferenceEntity())
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
