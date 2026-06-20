# Task 26 — Move Challenge

**Priority:** P2 — tăng skill ceiling, nhỏ, dùng lại toàn bộ logic hiện có  
**Effort:** Small

---

## Mô tả

Chế độ **Move Challenge**: giải puzzle trong ≤ N nước (N = optimal + buffer). Badge "Perfect 🎯" nếu đạt đúng N optimal. Hiện move budget countdown ngay trên board.

---

## S1 — Tính N

Không tính optimal chính xác (NP-hard) — dùng ước lượng:
```kotlin
fun moveBudget(boardSize: Int, shuffleMoves: Int): Int =
    (shuffleMoves * 1.5).toInt().coerceAtLeast(boardSize * boardSize * 2)
```

Pass `shuffleCount` vào GameAct qua Intent (`EXTRA_SHUFFLE_COUNT`).

---

## S2 — Hiển thị budget

**`act_game.xml`** — thêm `tvMoveBudget` dưới `tvMoveCount`:
```xml
<TextView
    android:id="@+id/tvMoveBudget"
    android:text="Budget: 30"
    android:textColor="@color/neon_lime"
    android:visibility="gone" />
```

**`GameAct.kt`** — update sau mỗi move:
```kotlin
val remaining = moveBudget - moveCount
tvMoveBudget.text = "Budget: $remaining"
if (remaining <= 5) tvMoveBudget.setTextColor(neonMagenta)
if (remaining <= 0) showBudgetExceededDialog()
```

---

## S3 — Kết quả

- `remaining > 0` khi win → badge "✅ Under budget!"  
- `remaining == 0` khi win → badge "🎯 Perfect!"  
- `remaining < 0` khi win → không badge (vẫn win, không chặn)

---

## S4 — Dialog hết budget

Khi `moveCount > moveBudget + 10` (buffer nhỏ): hỏi "Continue anyway?" | "Try Again". Không force fail — user tự quyết.

---

## Files ảnh hưởng

- `GameAct.kt` — đọc `EXTRA_MOVE_BUDGET`, hiển thị, badge win
- `act_game.xml` — `tvMoveBudget`
- `BoardOptionsFrm.kt` — pass `moveBudget` qua Intent khi chọn Move Challenge mode

## Acceptance

- [ ] Classic mode: `tvMoveBudget` ẩn hoàn toàn
- [ ] Move Challenge: hiện budget countdown
- [ ] ≤ 5 moves remaining → đổi màu magenta/đỏ
- [ ] Win đúng hạn → badge phù hợp trong win dialog
- [ ] Không chặn user hoàn thành nếu vượt budget
