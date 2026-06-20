# Task 29 — Achievement Badges

**Priority:** P2 — tăng long-term engagement, cần persistence layer nhỏ  
**Effort:** Medium

---

## Mô tả

Hệ thống huy hiệu local. 8 badge đầu tiên, hiện trong Settings hoặc About dialog. Badge mới → toast neon khi unlock lần đầu.

---

## S1 — Định nghĩa badges

**`Achievements.kt`** (file mới):
```kotlin
enum class Achievement(val id: String, val title: String, val desc: String, val icon: String) {
    FIRST_WIN      ("first_win",    "First Win!",      "Complete your first puzzle",       "🧩"),
    SPEED_DEMON    ("speed_demon",  "Speed Demon",     "Solve a puzzle in under 60 seconds","⚡"),
    MINIMALIST     ("minimalist",   "Minimalist",      "Solve 4×4 in under 50 moves",      "🎯"),
    DAILY_PLAYER   ("daily_player", "Daily Player",    "Complete a Daily Puzzle",           "🗓"),
    STREAK_3       ("streak_3",     "Hat Trick",       "3-day Daily Puzzle streak",         "🔥"),
    STREAK_7       ("streak_7",     "Week Warrior",    "7-day Daily Puzzle streak",         "🏆"),
    EXPLORER       ("explorer",     "Explorer",        "Play all 18 built-in images",       "🗺"),
    NO_HINTS       ("no_hints",     "Purist",          "Solve 10 puzzles without hints",    "💎"),
}
```

---

## S2 — Check & unlock

**`AchievementUtils.kt`** (file mới):
```kotlin
object AchievementUtils {
    fun isUnlocked(ctx: Context, a: Achievement) =
        Prefs.get(ctx).getBoolean("ach_${a.id}", false)

    fun unlock(ctx: Context, a: Achievement): Boolean {
        if (isUnlocked(ctx, a)) return false
        Prefs.get(ctx).edit().putBoolean("ach_${a.id}", true).apply()
        return true  // newly unlocked
    }

    fun checkAndUnlock(ctx: Context, a: Achievement, condition: Boolean): Boolean =
        if (condition && !isUnlocked(ctx, a)) unlock(ctx, a) else false
}
```

---

## S3 — Trigger points

**`GameAct.kt`** — trong `onWin()`:
```kotlin
AchievementUtils.checkAndUnlock(ctx, FIRST_WIN, true)
    .also { if (it) showAchievementToast(FIRST_WIN) }
AchievementUtils.checkAndUnlock(ctx, SPEED_DEMON, timerSeconds < 60)
    .also { if (it) showAchievementToast(SPEED_DEMON) }
AchievementUtils.checkAndUnlock(ctx, MINIMALIST, boardSize == 4 && moveCount < 50)
    .also { if (it) showAchievementToast(MINIMALIST) }
// ... các badge khác
```

---

## S4 — Toast unlock

```kotlin
private fun showAchievementToast(a: Achievement) {
    val toast = Toast.makeText(this,
        "${a.icon} Achievement unlocked: ${a.title}", Toast.LENGTH_LONG)
    toast.show()
}
```

Hoặc custom Snackbar với neon styling.

---

## S5 — Màn hình xem badges (trong Settings)

**`SettingsActivity.kt`** — section "Achievements":
RecyclerView đơn giản, mỗi row: icon + title + desc + checkmark neon nếu unlocked, mờ nếu chưa.

---

## Files ảnh hưởng

- `util/Achievements.kt` — enum
- `util/AchievementUtils.kt` — unlock logic
- `GameAct.kt` — trigger check sau win
- `SettingsActivity.kt` — section hiển thị badges
- `Prefs.kt` — keys `ach_*`

## Acceptance

- [ ] Thắng lần đầu → "First Win!" toast
- [ ] Giải < 60s → "Speed Demon" toast (chỉ 1 lần)
- [ ] Daily puzzle → "Daily Player" badge
- [ ] Streak 3/7 ngày → badge tương ứng
- [ ] Settings hiện đủ 8 badge, greyed out nếu chưa unlock
- [ ] Badge không bị grant lại nếu đã có
