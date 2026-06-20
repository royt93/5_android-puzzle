# Task 28 — Unlock Image Packs

**Priority:** P2 — tăng retention loop, nhưng cần thêm ảnh content trước  
**Effort:** Medium

---

## Mô tả

Ảnh được chia thành packs. Pack đầu (18 ảnh hiện tại) mở ngay. Pack 2+ unlock khi giải đủ N puzzle. UI: card ảnh chưa unlock hiện lock icon + "Play {N} more to unlock".

---

## Prerequisite

Cần thêm ảnh mới (bộ b1–b9, c1–c9) — tham chiếu Task 21. Không thể implement nếu chưa có ảnh pack 2+.

---

## S1 — Định nghĩa packs

**`ImagePacks.kt`** (file mới):
```kotlin
enum class ImagePack(val packId: String, val unlockAtSolvedCount: Int, val images: List<Int>) {
    STARTER("starter", 0, listOf(R.drawable.i1, ..., R.drawable.i9, R.drawable.a1, ..., R.drawable.a9)),
    NATURE("nature", 10, listOf(R.drawable.b1, ..., R.drawable.b9)),
    ABSTRACT("abstract", 25, listOf(R.drawable.c1, ..., R.drawable.c9)),
}
```

---

## S2 — Track total solved count

**`Prefs.kt`** — thêm key `total_solved_count`. Tăng trong `GameAct.onWin()`.

**`UnlockUtils.kt`**:
```kotlin
fun isPackUnlocked(ctx: Context, pack: ImagePack): Boolean =
    pack.unlockAtSolvedCount == 0 ||
    Prefs.get(ctx).getInt("total_solved_count", 0) >= pack.unlockAtSolvedCount

fun solvedCountToNextUnlock(ctx: Context): Int {
    val solved = Prefs.get(ctx).getInt("total_solved_count", 0)
    return ImagePack.values()
        .filter { !isPackUnlocked(ctx, it) }
        .minOfOrNull { it.unlockAtSolvedCount - solved } ?: 0
}
```

---

## S3 — UI locked card

**`frm_titled_image_card.xml`** — thêm lock overlay:
```xml
<FrameLayout android:id="@+id/lockOverlay" android:visibility="gone"
    android:background="#CC0E1230">
    <ImageView android:src="@drawable/ic_lock_neon" android:layout_gravity="center" />
    <TextView android:id="@+id/tvUnlockHint" android:layout_gravity="bottom|center"
        android:text="10 more to unlock" android:textColor="@color/neon_text_secondary" />
</FrameLayout>
```

**`ImageCardsAdt.kt`**:
```kotlin
val locked = !UnlockUtils.isPackUnlocked(ctx, card.pack)
holder.lockOverlay.visibility = if (locked) View.VISIBLE else View.GONE
holder.root.isEnabled = !locked
```

---

## S4 — Unlock celebration

Khi `total_solved_count` đạt ngưỡng unlock pack mới → hiển thị snackbar/toast:
"🎉 New pack unlocked: Nature Pack!"

---

## Files ảnh hưởng

- `util/ImagePacks.kt` — enum packs + unlock thresholds
- `util/UnlockUtils.kt` — check + track
- `BoardOptionsVm.kt` — build image list từ packs đã unlock
- `ImageCardsAdt.kt` — render lock overlay
- `frm_titled_image_card.xml` — lock overlay
- `GameAct.kt` — tăng `total_solved_count` khi win
- `Prefs.kt` — key `total_solved_count`

## Acceptance

- [ ] Starter pack (18 ảnh) luôn mở ngay
- [ ] Pack 2 lock đúng cho đến khi solved ≥ 10
- [ ] Locked card: click không được, hiện "N more to unlock"
- [ ] Khi đủ điều kiện → unlock toast + card chuyển normal
- [ ] `total_solved_count` chỉ tăng khi win, không tăng khi shuffle/reset
