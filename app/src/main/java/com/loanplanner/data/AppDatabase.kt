package com.loanplanner.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.loanplanner.data.dao.LoanDao
import com.loanplanner.data.dao.PurchaseDao
import com.loanplanner.data.entities.LoanEntity
import com.loanplanner.data.entities.PurchaseEntity

@Database(
    entities = [LoanEntity::class, PurchaseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun loanDao(): LoanDao
    abstract fun purchaseDao(): PurchaseDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "loan_planner_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
