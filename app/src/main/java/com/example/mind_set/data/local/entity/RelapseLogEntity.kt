package com.example.mind_set.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Catatan kambuh (relapse) yang diinput manual oleh pengguna
 * melalui tombol "Catat Evaluasi Kambuh" di layar Progress.
 */
@Entity(tableName = "relapse_logs")
data class RelapseLogEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "date_key")
    val dateKey: String, // format yyyy-MM-dd

    @ColumnInfo(name = "trigger_text")
    val triggerText: String,

    @ColumnInfo(name = "note")
    val note: String = "",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
