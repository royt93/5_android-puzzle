# Task 25 — Hint System

**Priority:** P0 — giảm friction cho người mới, dễ impl, không thay đổi game logic  
**Effort:** Small

---

## Mô tả

Nút **Hint** trong game: highlight tile sai vị trí hoặc flash vị trí đúng của tile blank. Cost: +10 moves hoặc +30s vào kết quả cuối.

---

## S1 — Nút Hint trong toolbar/footer

**`act_game.xml`** — thêm `btnHint` cạnh `btnUndo`:
```xml
<ImageButton
    android:id="@+id/btnHint"
    android:src="@drawable/ic_hint_neon"
    android:contentDescription="@string/hint"
    ... />
```

---

## S2 — Logic hint

**`GameAct.kt`**:
```kotlin
btnHint.setOnClickListener {
    val wrongTiles = boardView.getWrongPositionTiles()  // trả list index tile sai chỗ
    if (wrongTiles.isEmpty()) return@setOnClickListener
    val target = wrongTiles.first()
    boardView.flashTile(target, color = colorPrimary, durationMs = 800)
    hintPenaltyMoves += 10  // cộng vào moveCount khi win
    btnHint.isEnabled = false
    handler.postDelayed({ btnHint.isEnabled = true }, 3000)  // cooldown 3s
}
```

**`GameBoard.kt`** — thêm:
```kotlin
fun getWrongPositionTiles(): List<Int> =
    state.tiles.indices.filter { i -> state.tiles[i] != i + 1 && state.tiles[i] != 0 }

fun flashTile(index: Int, color: Int, durationMs: Long) {
    highlightedTile = index
    highlightColor = color
    invalidate()
    handler.postDelayed({ highlightedTile = -1; invalidate() }, durationMs)
}
```

Trong `onDraw()`: nếu `highlightedTile >= 0`, vẽ overlay rect với `highlightColor` alpha 80 lên tile đó.

---

## S3 — Hiển thị penalty trong win dialog

**`DialogUtils.kt`** / win dialog: nếu `hintPenaltyMoves > 0`, hiển thị "(+{n} hint)" cạnh move count.

---

## Files ảnh hưởng

- `GameBoard.kt` — `getWrongPositionTiles()`, `flashTile()`, render highlight trong `onDraw()`
- `GameAct.kt` — wiring nút, penalty counter
- `act_game.xml` — thêm `btnHint`
- `DialogUtils.kt` — hiện penalty nếu có
- `res/drawable/ic_hint_neon.xml` — vector icon bóng đèn neon

## Acceptance

- [ ] Tap Hint → tile sai vị trí đầu tiên flash sáng 0.8s
- [ ] Cooldown 3s — không spam được
- [ ] `+10 moves` cộng vào kết quả, hiện trong win dialog
- [ ] Khi puzzle đã solved (không có tile sai) → nút Hint disabled
- [ ] Không regression logic undo/shuffle
