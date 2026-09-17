package com.lectostart.app.progress.data

/**
 * Hitos de racha (brainstorming 2026-09-16, ideas 1-3 del pedido del usuario): dan interés visual
 * a la racha de US-12 sin agregar un sistema de gamificación completo (eso sigue fuera del MVP,
 * ver `docs/MVP.md` §4). Los hitos son puramente derivados de `StreakCalculator.calculate` — no
 * hay estado de "desbloqueado" propio, cualquier racha que alcance el umbral lo cumple.
 */
data class StreakMilestone(val days: Int, val emoji: String, val label: String, val celebration: String)

val STREAK_MILESTONES =
  listOf(
    StreakMilestone(days = 3, emoji = "🔥", label = "3 días", celebration = "¡3 días seguidos! Ya vas agarrando el ritmo."),
    StreakMilestone(days = 7, emoji = "⚡", label = "1 semana", celebration = "¡Una semana completa! Eso ya es un hábito."),
    StreakMilestone(days = 14, emoji = "🌟", label = "2 semanas", celebration = "¡Dos semanas seguidas! Vas muy en serio con esto."),
    StreakMilestone(days = 30, emoji = "🏆", label = "1 mes", celebration = "¡Un mes de constancia! Impresionante."),
  )

/** El hito más alto ya alcanzado por [streakDays], o null si la racha todavía no llega a 3 días. */
fun highestMilestoneReached(streakDays: Int): StreakMilestone? = STREAK_MILESTONES.lastOrNull { streakDays >= it.days }

/** Emoji a mostrar junto al número de racha: escala con el hito alcanzado; 🔥 por defecto si hay racha. */
fun streakEmoji(streakDays: Int): String = if (streakDays <= 0) "📖" else highestMilestoneReached(streakDays)?.emoji ?: "🔥"
