package com.example.mind_set.data.model

data class JournalItem(
    val id: Int = 0,
    val date: Long = System.currentTimeMillis(),
    val mood: String = "",
    val triggerText: String = "",
    val reframingText: String = "",
    val tags: List<String> = emptyList(),
    val aiResponse: String = "",
    val status: String = "Refleksi Harian",
    val createdAt: Long = System.currentTimeMillis()
)
