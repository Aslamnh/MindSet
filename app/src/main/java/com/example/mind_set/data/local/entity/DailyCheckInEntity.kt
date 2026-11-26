package com.example.mind_set.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_checkins",
    indices = [Index(value = ["date_key", "username"], unique = true)]
)
data class DailyCheckInEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "date_key")
    val dateKey: String, // format yyyy-MM-dd

    @ColumnInfo(name = "username")
    val username: String = "default_user",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
