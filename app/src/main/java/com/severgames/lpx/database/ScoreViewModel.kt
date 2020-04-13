package com.severgames.lpx.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.severgames.lpx.models.ScoreModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScoreViewModel(application: Application): AndroidViewModel(application) {
    private val repository: ScoreRepository
    val allScore:LiveData<List<ScoreModel>>

    init {
        val scoreDao = ApplicationDatabase.getAppDataBase(
            application,
            viewModelScope
        ).scoreDao()
        repository = ScoreRepository(scoreDao)
        allScore = repository.allScore
    }

    fun insert(scoreModel: ScoreModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(scoreModel)
    }
}