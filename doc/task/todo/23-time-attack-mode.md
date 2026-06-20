# Task 23 — Time Attack Mode

**Priority:** P1 — tăng replayability và engagement, cần UI mới nhưng logic đơn giản  
**Effort:** Medium

---

## Mô tả

Chế độ **Time Attack**: đồng hồ đếm ngược (Easy 5 phút / Medium 3 phút / Hard 90s). Hết giờ → "Time's up!" dialog. Thắng trước khi hết giờ → win dialog thêm badge thời gian còn lại.

---

## S1 — Chọn chế độ trong Board Options

**`frm_board_options.xml`** — thêm `RadioGroup` hoặc `ChipGroup` dưới board size:
```xml
<com.google.android.material.chip.ChipGroup
    android:id="@+id/chipGroupMode"
    app:singleSelection="true">
    <com.google.android.material.chip.Chip android:text="Classic" />
    <com.google.android.material.chip.Chip android:text="Time Attack" />
</com.google.android.material.chip.ChipGroup>
```

Khi chọn Time Attack → hiện thêm `ChipGroup` difficulty (Easy / Medium / Hard).

---

## S2 — Pass chế độ qua Intent

**`BoardOptionsFrm.kt`**:
```kotlin
intent.putExtra(GameAct.EXTRA_GAME_MODE, GameMode.TIME_ATTACK.name)
intent.putExtra(GameAct.EXTRA_TIME_LIMIT_SECONDS, timeLimitSeconds)  // 300/180/90
```

---

## S3 — Timer đếm ngược trong GameAct

**`GameAct.kt`**:
```kotlin
private var timeRemaining = 0
private var countdownTimer: CountDownTimer? = null

private fun startCountdown(seconds: Int) {
    countdownTimer = object : CountDownTimer(seconds * 1000L, 1000L) {
        override fun onTick(ms: Long) {
            timeRemaining = (ms / 1000).toInt()
            updateTimerDisplay(timeRemaining)
            if (timeRemaining <= 30) tvTimer.setTextColor(neonMagenta)  // cảnh báo đỏ
        }
        override fun onFinish() { showTimeUpDialog() }
    }.start()
}
```

Timer hiện tại đang đếm lên — mode Classic giữ nguyên. Mode Time Attack dùng `CountDownTimer` thay thế.

---

## S4 — Time's Up dialog

**`DialogUtils.kt`** — thêm `showTimeUpDialog()`:
- Hiện "Time's Up! ⏱️" với neon magenta
- Nút: **Try Again** (shuffle lại, reset timer) | **Back**
- Không lưu score

---

## S5 — Win dialog với bonus

Khi thắng trong Time Attack: thêm dòng "⚡ {timeRemaining}s remaining" với neon lime glow.

---

## Files ảnh hưởng

- `frm_board_options.xml` — ChipGroup mode + difficulty
- `BoardOptionsFrm.kt` — xử lý selection, pass Intent
- `BoardOptionsVm.kt` — thêm `gameMode`, `timeLimitSeconds`
- `GameAct.kt` — khởi tạo CountDownTimer, handle time up
- `DialogUtils.kt` — `showTimeUpDialog()`
- `Prefs.kt` — thêm key `game_mode`, `time_limit`

## Acceptance

- [ ] Classic mode: timer đếm lên như cũ
- [ ] Time Attack Easy/Med/Hard: timer đếm ngược đúng
- [ ] ≤ 30s còn lại: timer chuyển màu magenta/đỏ
- [ ] Hết giờ → Time's Up dialog (không crash)
- [ ] Thắng Time Attack → win dialog hiện thời gian còn lại
- [ ] Try Again trong Time's Up reset board + timer mới
- [ ] `ANIMATOR_DURATION_SCALE == 0` → timer vẫn chạy đúng (timer không dùng animation)
