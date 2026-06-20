# Task 20 — Win Celebration (particle · animated counters · tile reveal)

**Priority:** P0 — khoảnh khắc thắng là điểm nhấn cảm xúc cao nhất của game  
**Effort:** Medium (3 sub-tasks độc lập, có thể ship từng phần)

---

## Hiện trạng

`NeonParticleView` đã có sẵn trong `act_game.xml` (visibility=GONE) và code `GameBoard.playWinFeedback()` chạy halo lime 700ms — nhưng particle chưa được trigger, counters trong win dialog là static, và không có reveal sequence.

---

## W1 — Trigger NeonParticleView khi thắng

**File:** `app/src/main/java/com/helpmepls/slidepuzzle/act/GameAct.kt`

Trong callback `onMoveListener` khi `isSolved == true`:
```kotlin
// Sau playWinFeedback():
val particle = findViewById<NeonParticleView>(R.id.particleView)
particle?.burst()   // method này đã có sẵn trong NeonParticleView
```

Đảm bảo `particle.visibility = View.VISIBLE` trước khi gọi burst (NeonParticleView tự set GONE khi animation xong).

Nếu `fx_reduce_motion` bật → skip burst, chỉ giữ halo.

**Acceptance:**
- [ ] Confetti neon bùng khi giải xong, tắt sau ~1.3s
- [ ] Không chặn touch/click vào dialog bên trên
- [ ] Reduce motion = skip particle

---

## W2 — Animated counters trong win dialog

**File:** `app/src/main/java/com/helpmepls/slidepuzzle/DialogUtils.kt` (hoặc nơi show win dialog)

Tìm chỗ set `tvWinMoves` và `tvWinTime` — thay set trực tiếp bằng ValueAnimator:

```kotlin
fun animateCounter(textView: TextView, from: Int, to: Int, suffix: String = "") {
    ValueAnimator.ofInt(from, to).apply {
        duration = 600
        interpolator = DecelerateInterpolator()
        addUpdateListener { textView.text = "📊 ${it.animatedValue}$suffix" }
        start()
    }
}
```

Gọi trong `showWinDialog()`:
```kotlin
animateCounter(tvWinMoves, 0, actualMoves)
animateCounter(tvWinTime, 0, actualSeconds, "s")
```

Nếu là best mới: `tvBestBadge.visibility = VISIBLE` + bắt đầu `neon_pulse` animation trên badge.

**Acceptance:**
- [ ] Số moves đếm 0→actual trong 600ms khi dialog mở
- [ ] Badge "🏆 NEW BEST!" chỉ hiện khi thực sự phá record
- [ ] Reduce motion = set giá trị thẳng không animate

---

## W3 — Solved reveal sequence (tile sweep)

**File:** `GameBoard.kt`

Thêm function mới (không ảnh hưởng logic game):
```kotlin
fun playSolvedReveal(onComplete: () -> Unit) {
    // Quét sáng từng tile theo thứ tự đường chéo (i+j tăng dần)
    // Mỗi tile: flash lime alpha rồi fade về accent bình thường
    // Tổng ~400ms, sau đó gọi onComplete
    val totalTiles = grid.size.width * grid.size.height
    var completed = 0
    for (j in 0 until grid.size.height) {
        for (i in 0 until grid.size.width) {
            val delay = (i + j) * 35L  // diagonal stagger
            postDelayed({
                // trigger glow flash trên tile (i,j) — dùng tmpHighlight array
                flashTile(i, j)
                if (++completed == totalTiles) onComplete()
            }, delay)
        }
    }
}
```

Trong `GameAct.kt` — thay gọi `playWinFeedback()` + `showDialog()` trực tiếp bằng:
```kotlin
boardView.playSolvedReveal {
    boardView.playWinFeedback()
    particleView?.burst()
    showWinDialog(moves, timeSeconds)
}
```

**Acceptance:**
- [ ] Tile sweep diagonal chạy ~400ms trước khi dialog hiện
- [ ] Cancel an toàn khi back/finish trong lúc reveal
- [ ] Reduce motion = bỏ qua sequence, show dialog ngay

---

## W4 — Stars theo hiệu suất

Trong `dialog_win.xml`, `tvStar1/2/3` đã có. Chỉ cần logic trong GameAct:

```kotlin
val stars = when {
    moves <= optimalMoves * 1.2  -> 3
    moves <= optimalMoves * 2.0  -> 2
    else                          -> 1
}
// `optimalMoves` = grid.size.width * grid.size.height * 2 (heuristic đơn giản)
val starColors = listOf(tvStar1, tvStar2, tvStar3)
starColors.forEachIndexed { idx, tv ->
    if (idx < stars) {
        tv.setTextColor(resolveAccentColor())
        tv.setShadowLayer(12f, 0f, 0f, resolveAccentGlowColor())
    } else {
        tv.setTextColor(0xFF333355.toInt())  // dim star
        tv.clearShadowLayer()
    }
}
```

**Acceptance:**
- [ ] 1–3 sao glow theo số moves tương đối
- [ ] Sao dim (không xóa) khi không đủ điểm

---

## Files ảnh hưởng
- `GameAct.kt` — trigger sequence, particle, dialog
- `GameBoard.kt` — thêm `playSolvedReveal()`, `flashTile()`
- `DialogUtils.kt` / nơi show win dialog — animated counters, stars
- `NeonParticleView.kt` — verify `burst()` method tồn tại
