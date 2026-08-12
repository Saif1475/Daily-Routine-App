package com.dailyroutine.app.data.repository

import com.dailyroutine.app.data.model.RoutineItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Persists routines to Firestore under `users/{uid}/routines/{routineId}`, scoping every
 * document to the signed-in user so each account has its own private routine list.
 */
class RoutineRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) {
    private val uid: String? get() = auth.currentUser?.uid

    private fun collection() = uid?.let { db.collection("users").document(it).collection("routines") }

    /** Live stream of every routine belonging to the current user. */
    fun observeRoutines(): Flow<List<RoutineItem>> = callbackFlow {
        val col = collection()
        if (col == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val registration = col.addSnapshotListener { snapshot, _ ->
            val items = snapshot?.documents?.map { doc ->
                RoutineItem.fromFirestore(doc.id, doc.data ?: emptyMap())
            } ?: emptyList()
            trySend(items)
        }
        awaitClose { registration.remove() }
    }

    suspend fun addRoutine(item: RoutineItem): Result<Unit> = runCatching {
        val col = collection() ?: error("Not signed in")
        col.document().set(item.toFirestoreMap()).await()
    }

    suspend fun updateRoutine(item: RoutineItem): Result<Unit> = runCatching {
        val col = collection() ?: error("Not signed in")
        col.document(item.id).set(item.toFirestoreMap()).await()
    }

    suspend fun deleteRoutine(id: String): Result<Unit> = runCatching {
        val col = collection() ?: error("Not signed in")
        col.document(id).delete().await()
    }

    /** Flips today's completion state for [item] and recalculates its streak. */
    suspend fun toggleToday(item: RoutineItem, today: LocalDate = LocalDate.now()): Result<Unit> = runCatching {
        val key = today.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val newlyCompleted = item.history[key] != true
        val newHistory = item.history.toMutableMap().apply { put(key, newlyCompleted) }
        val newStreak = computeStreak(newHistory, today)
        val updated = item.copy(
            history = newHistory,
            streak = newStreak,
            bestStreak = maxOf(item.bestStreak, newStreak),
        )
        updateRoutine(updated).getOrThrow()
    }

    private fun computeStreak(history: Map<String, Boolean>, today: LocalDate): Int {
        var streak = 0
        var day = today
        while (history[day.format(DateTimeFormatter.ISO_LOCAL_DATE)] == true) {
            streak++
            day = day.minusDays(1)
        }
        return streak
    }
}
