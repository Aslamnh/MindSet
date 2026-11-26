package com.example.mind_set.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Mencatat kehadiran harian pengguna ("Log-in Harian").
 * Satu baris per tanggal, sehingga membuka aplikasi berkali-kali
 * dalam sehari tidak menambah hitungan.
 */
@Entity(tableName = "daily_checkins")
data class DailyCheckInEntity(

    @PrimaryKey
    @ColumnInfo(name = "date_key")
    val dateKey: String, // format yyyy-MM-dd

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
