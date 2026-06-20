# Task 22 — Best Score per Puzzle (local leaderboard)

**Priority:** P1 — tăng motivation chơi lại, gần như zero risk (local only)  
**Effort:** Small-Medium

---

## Hiện trạng

`ScoreUtils.kt` có `saveHighScore()`/`getHighScore()` nhưng chỉ lưu **1 record toàn cục** — không phân biệt theo ảnh hay board size. Win dialog có `tvBestBadge` nhưng chưa wired up đúng.

---

## S1 — Lưu best score theo key (ảnh × board size)

**File:** `app/src/main/java/com/helpmepls/slidepuzzle/util/ScoreUtils.kt`

Refactor key để encode cả image + size:

```kotlin
object ScoreUtils {
    private fun key(imageResId: Int, width: Int, height: Int) =
        "best_${imageResId}_${width}x${height}"

    fun saveBestMoves(ctx: Context, imageResId: Int, w: Int, h: Int, moves: Int) {
        val k = key(imageResId, w, h) + "_moves"
        val current = Prefs.get(ctx).getInt(k, Int.MAX_VALUE)
        if (moves < current) Prefs.get(ctx).edit().putInt(k, moves).apply()
    }

    fun getBestMoves(ctx: Context, imageResId: Int, w: Int, h: Int): Int =
        Prefs.get(ctx).getInt(key(imageResId, w, h) + "_moves", Int.MAX_VALUE)

    fun saveBestTime(ctx: Context, imageResId: Int, w: Int, h: Int, seconds: Int) {
        val k = key(imageResId, w, h) + "_time"
        val current = Prefs.get(ctx).getInt(k, Int.MAX_VALUE)
        if (seconds < current) Prefs.get(ctx).edit().putInt(k, seconds).apply()
    }

    fun getBestTime(ctx: Context, imageResId: Int, w: Int, h: Int): Int =
        Prefs.get(ctx).getInt(key(imageResId, w, h) + "_time", Int.MAX_VALUE)

    fun isNewBestMoves(ctx: Context, imageResId: Int, w: Int, h: Int, moves: Int): Boolean =
        moves < getBestMoves(ctx, imageResId, w, h)

    fun isNewBestTime(ctx: Context, imageResId: Int, w: Int, h: Int, seconds: Int): Boolean =
        seconds < getBestTime(ctx, imageResId, w, h)
}
```

---

## S2 — Gọi save khi thắng

**File:** `GameAct.kt` — trong `onWin()` hoặc `onMoveListener` khi `isSolved`:

```kotlin
val imageResId = intent.getIntExtra(EXTRA_IMAGE_RES_ID, 0)
val isNewMoves = ScoreUtils.isNewBestMoves(this, imageResId, boardWidth, boardHeight, moveCount)
val isNewTime  = ScoreUtils.isNewBestTime(this, imageResId, boardWidth, boardHeight, timerSeconds)

ScoreUtils.saveBestMoves(this, imageResId, boardWidth, boardHeight, moveCount)
ScoreUtils.saveBestTime(this, imageResId, boardWidth, boardHeight, timerSeconds)

showWinDialog(moveCount, timerSeconds, isNewMoves || isNewTime)
```

---

## S3 — Hiển thị trong win dialog

**`dialog_win.xml`** — `tvBestBadge` đã có với text "🏆 NEW BEST!". Chỉ cần wiring:

```kotlin
// Trong showWinDialog():
tvBestBadge.visibility = if (isNewBest) View.VISIBLE else View.GONE
if (isNewBest) {
    // Pulse animation trên badge
    tvBestBadge.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.neon_pulse))
}
```

---

## S4 — Hiển thị best trên card ảnh (Board Options)

Trên mỗi image card, hiển thị best score nhỏ ở góc nếu đã từng chơi:

**`frm_titled_image_card.xml`** — thêm TextView overlay góc phải dưới:
```xml
<TextView
    android:id="@+id/tvBestScore"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom|end"
    android:layout_margin="6dp"
    android:textSize="10sp"
    android:textColor="@color/neon_lime"
    android:shadowColor="@color/neon_lime_glow"
    android:shadowRadius="6"
    android:background="@drawable/neon_glass_card"
    android:paddingHorizontal="4dp"
    android:paddingVertical="2dp"
    android:visibility="gone" />
```

**`ImageCardsAdt.kt`** — bind score:
```kotlin
val best = ScoreUtils.getBestMoves(ctx, card.imageResId, currentBoardSize.width, currentBoardSize.height)
if (best != Int.MAX_VALUE) {
    holder.bestScore.text = "⭐ $best"
    holder.bestScore.visibility = View.VISIBLE
} else {
    holder.bestScore.visibility = View.GONE
}
```

Adapter cần nhận `currentBoardSize` — pass từ `BoardOptionsFrm` khi khởi tạo.

---

## S5 — Stats screen (optional, P2)

Nếu muốn thêm màn "Stats" trong tương lai: mọi data đã có trong SharedPreferences với key chuẩn. Chỉ cần iterate các key có prefix `best_`.

---

## Files ảnh hưởng

- `util/ScoreUtils.kt` — refactor key, add per-puzzle methods
- `act/GameAct.kt` — gọi save khi win, pass isNewBest vào dialog
- `DialogUtils.kt` — show tvBestBadge + pulse animation
- `res/layout/frm_titled_image_card.xml` — thêm tvBestScore overlay
- `adt/ImageCardsAdt.kt` — bind best score, nhận currentBoardSize

## Acceptance

- [ ] Thắng lần đầu → badge "🏆 NEW BEST!" glow hiện
- [ ] Thắng lần sau với moves ít hơn → badge hiện lại
- [ ] Thắng với moves nhiều hơn → badge KHÔNG hiện
- [ ] Card ảnh hiển thị best score nhỏ sau khi đã chơi ít nhất 1 lần
- [ ] Best score đúng theo từng (ảnh × board size) — 4×4 và 3×3 tách riêng
- [ ] Không có regression logic game
