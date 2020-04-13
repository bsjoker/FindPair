package com.severgames.lpx.database

import androidx.lifecycle.LiveData
import com.severgames.lpx.models.ScoreModel

class ScoreRepository(private val scoreDao: ScoreDao) {
    val allScore:LiveData<List<ScoreModel>> = scoreDao.getAll()

    suspend fun insert(scoreModel: ScoreModel){
        scoreDao.insert(scoreModel)
    }
}