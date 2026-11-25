package com.example.mind_set.data.progress

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ProgressDbHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "mindset_progress.db"
        private const val DB_VERSION = 1
        const val T_CHECKINS = "daily_checkins"
        const val T_STATE = "progress_state"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE $T_CHECKINS (
                date TEXT PRIMARY KEY,
                status TEXT NOT NULL,
                intensity INTEGER,
                triggers TEXT,
                note TEXT,
                created_at INTEGER NOT NULL
            )""".trimMargin()
        )
        db.execSQL(
            """CREATE TABLE $T_STATE (
                id INTEGER PRIMARY KEY,
                current_streak INTEGER NOT NULL,
                longest_streak INTEGER NOT NULL,
                total_days INTEGER NOT NULL,
                success_days INTEGER NOT NULL,
                relapse_days INTEGER NOT NULL,
                last_check_in_date TEXT,
                updated_at INTEGER NOT NULL
            )""".trimMargin()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $T_CHECKINS")
        db.execSQL("DROP TABLE IF EXISTS $T_STATE")
        onCreate(db)
    }

    // ---------- Check-ins ----------

    fun getAllCheckIns(): List<DailyCheckIn> {
        val list = mutableListOf<DailyCheckIn>()
        readableDatabase.rawQuery(
            "SELECT date, status, intensity, triggers, note, created_at FROM $T_CHECKINS ORDER BY date ASC",
            null
        ).use { c ->
            while (c.moveToNext()) {
                list.add(
                    DailyCheckIn(
                        date = c.getString(0),
                        status = runCatching { CheckInStatus.valueOf(c.getString(1)) }
                            .getOrDefault(CheckInStatus.SKIP),
                        intensity = if (c.isNull(2)) null else c.getInt(2),
                        triggers = c.getString(3),
                        note = c.getString(4),
                        createdAt = c.getLong(5)
                    )
                )
            }
        }
        return list
    }

    fun getCheckIn(date: String): DailyCheckIn? {
        readableDatabase.rawQuery(
            "SELECT date, status, intensity, triggers, note, created_at FROM $T_CHECKINS WHERE date = ?",
            arrayOf(date)
        ).use { c ->
            if (c.moveToFirst()) {
                return DailyCheckIn(
                    date = c.getString(0),
                    status = runCatching { CheckInStatus.valueOf(c.getString(1)) }
                        .getOrDefault(CheckInStatus.SKIP),
                    intensity = if (c.isNull(2)) null else c.getInt(2),
                    triggers = c.getString(3),
                    note = c.getString(4),
                    createdAt = c.getLong(5)
                )
            }
        }
        return null
    }

    fun upsertCheckIn(ci: DailyCheckIn) {
        val db = writableDatabase
        val cv = android.content.ContentValues().apply {
            put("date", ci.date)
            put("status", ci.status.name)
            if (ci.intensity == null) putNull("intensity") else put("intensity", ci.intensity)
            if (ci.triggers == null) putNull("triggers") else put("triggers", ci.triggers)
            if (ci.note == null) putNull("note") else put("note", ci.note)
            put("created_at", ci.createdAt)
        }
        db.insertWithOnConflict(T_CHECKINS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun deleteCheckIn(date: String) {
        writableDatabase.delete(T_CHECKINS, "date = ?", arrayOf(date))
    }

    fun clearAll() {
        val db = writableDatabase
        db.delete(T_CHECKINS, null, null)
        db.delete(T_STATE, null, null)
    }
}