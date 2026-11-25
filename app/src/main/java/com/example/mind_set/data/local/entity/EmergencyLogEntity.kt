package com.example.mind_set.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_logs")
data class EmergencyLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "urge_level")
    val urgeLevel: Int,

    @ColumnInfo(name = "trigger_category")
    val triggerCategory: String,

    @ColumnInfo(name = "additional_note")
    val additionalNote: String = "",

    @ColumnInfo(name = "recommendation_given")
    val recommendationGiven: String,

    @ColumnInfo(name = "source_type")
    val sourceType: String,

    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)