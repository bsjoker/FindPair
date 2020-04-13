package com.severgames.lpx.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.severgames.lpx.models.ScoreModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(  entities = [ScoreModel::class], version = 1, exportSchema = false)
abstract class ApplicationDatabase: RoomDatabase() {
    abstract fun scoreDao(): ScoreDao

    private class ScoreDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    var scoreDao = database.scoreDao()
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ApplicationDatabase? = null

        fun getAppDataBase(context: Context,
                           scope: CoroutineScope): ApplicationDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(ApplicationDatabase::class) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ApplicationDatabase::class.java,
                    "myDB"
                )
                    .addCallback(ScoreDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        fun destroyDataBase() {
            INSTANCE = null
        }
    }
}