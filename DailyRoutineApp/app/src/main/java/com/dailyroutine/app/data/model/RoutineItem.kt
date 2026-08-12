package com.dailyroutine.app.data.model

/** Category a routine belongs to, mirroring the four sections on the Home screen. */
enum class RoutineCategory(val label: String) {
    MORNING("Morning"),
    AFTERNOON("Afternoon"),
    EVENING("Evening"),
    ANYTIME("Anytime");

    companion object {
        fun fromLabel(label: String) = entries.firstOrNull { it.label == label } ?: MORNING
    }
}

enum class RoutineType { HABIT, TASK }

enum class RoutineColor { TEAL, ORANGE }

/**
 * A single habit or task. [history] maps an ISO-8601 date string ("2026-08-12") to whether
 * the routine was completed that day; a missing entry means the routine did not apply/exist yet.
 */
data class RoutineItem(
    val id: String = "",
    val title: String = "",
    val category: RoutineCategory = RoutineCategory.MORNING,
    val type: RoutineType = RoutineType.HABIT,
    val time: String = "",
    val color: RoutineColor = RoutineColor.TEAL,
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val history: Map<String, Boolean> = emptyMap(),
    val createdAt: Long = System.currentTimeMillis(),
) {
    /** Firestore needs a no-arg constructor + plain map for automatic deserialization. */
    fun toFirestoreMap(): Map<String, Any?> = mapOf(
        "title" to title,
        "category" to category.label,
        "type" to type.name,
        "time" to time,
        "color" to color.name,
        "streak" to streak,
        "bestStreak" to bestStreak,
        "history" to history,
        "createdAt" to createdAt,
    )

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromFirestore(id: String, data: Map<String, Any?>): RoutineItem = RoutineItem(
            id = id,
            title = data["title"] as? String ?: "",
            category = RoutineCategory.fromLabel(data["category"] as? String ?: "Morning"),
            type = runCatching { RoutineType.valueOf(data["type"] as? String ?: "HABIT") }.getOrDefault(RoutineType.HABIT),
            time = data["time"] as? String ?: "",
            color = runCatching { RoutineColor.valueOf(data["color"] as? String ?: "TEAL") }.getOrDefault(RoutineColor.TEAL),
            streak = (data["streak"] as? Long)?.toInt() ?: 0,
            bestStreak = (data["bestStreak"] as? Long)?.toInt() ?: 0,
            history = (data["history"] as? Map<String, Boolean>) ?: emptyMap(),
            createdAt = (data["createdAt"] as? Long) ?: System.currentTimeMillis(),
        )
    }
}
