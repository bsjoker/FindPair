package com.severgames.lpx.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scoreData")
data class ScoreModel (
    @PrimaryKey(autoGenerate = true) var id: Long? = 0,
    @ColumnInfo(name = "value") val value: Int = 0
)