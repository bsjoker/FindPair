package com.severgames.lpx.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.severgames.lpx.models.ScoreModel

@Dao
interface ScoreDao {
    @Query("SELECT * FROM scoreData")
    fun getAll(): LiveData<List<ScoreModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(scoreModel: ScoreModel)
}