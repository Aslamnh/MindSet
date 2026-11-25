package com.example.mind_set.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journals")
data class JournalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "date")
    val date: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "mood")
    val mood: String,

    @ColumnInfo(name = "trigger_text")
    val triggerText: String,

    @ColumnInfo(name = "reframing_text")
    val reframingText: String,

    @ColumnInfo(name = "tags")
    val tags: String, // comma-separated tags

    @ColumnInfo(name = "ai_response")
    val aiResponse: String,

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
