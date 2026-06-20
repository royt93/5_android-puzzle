# Task 24 — Daily Puzzle

**Priority:** P1 — tăng retention/streak, không cần server (seed theo ngày)  
**Effort:** Medium

---

## Mô tả

Mỗi ngày 1 puzzle cố định (seed từ ngày tháng). User giải xong → lưu streak. Vào lại trong ngày → hiện "Already solved today! 🏆". Màn hình Board Options có banner **"Daily Puzzle"** ở đầu.

---

## S1 — Seed từ ngày

**`DailyPuzzleUtils.kt`** (file mới):
```kotlin
object DailyPuzzleUtils {
    // Seed = số ngày kể từ epoch (không dùng Date.now() trực tiếp — dùng Calendar)
    fun todaySeed(): Long {
        val cal = Calendar.getInstance()
        return cal.get(Calendar.YEAR) * 10000L +
               cal.get(Calendar.MONTH) * 100L +
               cal.get(Calendar.DAY_OF_MONTH)
    }

    fun dailyImageIndex(totalImages: Int): Int =
        (todaySeed() % totalImages).toInt()

    fun dailyShuffleSeed(): Long = todaySeed() * 31337L

    fun todayKey(): String = todaySeed().toString()
}
```

Shuffle board dùng `Collections.shuffle(tiles, Random(dailyShuffleSeed()))` → cùng seed = cùng trạng thái ban đầu.

---

## S2 — Banner trong Board Options

**`frm_board_options.xml`** — thêm card ở đầu list:
```xml
<com.google.android.material.card.MaterialCardView
    android:id="@+id/cardDailyPuzzle"
    ... neon_glass_card background ...>
    <TextView android:text="🗓 Daily Puzzle"
        android:textColor="@color/neon_cyan" ... />
    <TextView android:id="@+id/tvDailyStatus"
        android:text="Tap to play!" ... />
</com.google.android.material.card.MaterialCardView>
```

Nếu hôm nay đã solved → `tvDailyStatus.text = "✅ Solved today! Streak: {n} days"`.

---

## S3 — Launch với seed

**`BoardOptionsFrm.kt`**:
```kotlin
cardDailyPuzzle.setOnClickListener {
    if (DailyPuzzleUtils.isSolvedToday(this)) {
        Toast.makeText(ctx, "Already solved today! Come back tomorrow 🌙", SHORT).show()
        return@setOnClickListener
    }
    val imgIndex = DailyPuzzleUtils.dailyImageIndex(predefinedImages.size)
    val intent = Intent(this, GameAct::class.java).apply {
        putExtra(EXTRA_IMAGE_RES_ID, predefinedImages[imgIndex].first)
        putExtra(EXTRA_BOARD_WIDTH, 4); putExtra(EXTRA_BOARD_HEIGHT, 4)
        putExtra(GameAct.EXTRA_GAME_MODE, GameMode.DAILY.name)
        putExtra(GameAct.EXTRA_DAILY_SEED, DailyPuzzleUtils.dailyShuffleSeed())
    }
    startActivity(intent)
}
```

---

## S4 — Lưu solved + streak

**`DailyPuzzleUtils.kt`**:
```kotlin
fun markSolvedToday(ctx: Context) {
    val prefs = Prefs.get(ctx)
    val today = todayKey()
    val lastSolved = prefs.getString("daily_last_solved", "")
    val streak = prefs.getInt("daily_streak", 0)

    val yesterday = (todaySeed() - 1).toString()
    val newStreak = if (lastSolved == yesterday) streak + 1 else 1

    prefs.edit()
        .putString("daily_last_solved", today)
        .putInt("daily_streak", newStreak)
        .apply()
}

fun isSolvedToday(ctx: Context) =
    Prefs.get(ctx).getString("daily_last_solved", "") == todayKey()

fun getStreak(ctx: Context) = Prefs.get(ctx).getInt("daily_streak", 0)
```

**`GameAct.kt`** — khi win trong Daily mode: gọi `DailyPuzzleUtils.markSolvedToday(this)`. Win dialog hiện thêm "🔥 Streak: {n} days".

---

## Files ảnh hưởng

- `util/DailyPuzzleUtils.kt` — tạo mới
- `GameAct.kt` — daily mode: shuffle với seed cố định, gọi markSolved khi win
- `BoardOptionsFrm.kt` — card Daily Puzzle, check status
- `frm_board_options.xml` — card Daily ở đầu
- `Prefs.kt` — thêm keys: `daily_last_solved`, `daily_streak`

## Acceptance

- [ ] Mỗi ngày cùng puzzle (same image + same shuffle) cho mọi user
- [ ] Giải xong → streak +1, hiện trong Board Options
- [ ] Ngày tiếp theo không chơi → streak reset về 1
- [ ] Tap Daily khi đã solved hôm nay → toast, không launch game
- [ ] Win dialog Daily mode hiện streak
- [ ] Không phụ thuộc internet
