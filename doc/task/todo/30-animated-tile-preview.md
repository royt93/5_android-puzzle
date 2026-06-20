# Task 30 — Animated Tile Preview (Board Options)

**Priority:** P1 — visual ấn tượng, không thay đổi game logic, small effort  
**Effort:** Small

---

## Mô tả

Trong Board Options, khi user select một image card, thumbnail của nó chạy animation "shuffle nhẹ" (2–3 tile hoán đổi vị trí rồi về lại) để preview cảm giác game. Dùng `ValueAnimator` + `Canvas` offset.

---

## S1 — Preview animation trên image card

**`ImageCardsAdt.kt`** — khi card được select:
```kotlin
fun playPreviewShuffle(holder: ViewHolder) {
    // Animate 2 tile ngẫu nhiên đổi chỗ nhau: dùng TranslateAnimation offset
    val anim = AnimatorSet()
    val tile1 = holder.previewOverlay  // overlay View trên image
    val moveRight = ObjectAnimator.ofFloat(tile1, "translationX", 0f, tileSize, 0f)
    val moveLeft  = ObjectAnimator.ofFloat(tile1, "translationX", 0f, -tileSize, 0f)
    anim.playSequentially(moveRight, moveLeft)
    anim.duration = 300
    anim.start()
}
```

**Đơn giản hơn:** dùng `animate().translationX().withEndAction {}` chain.

---

## S2 — Approach thực tế (nhẹ hơn)

Thay vì animate real tiles, vẽ preview static nhưng thêm **shimmer scan line** chạy từ trên xuống dưới trên thumbnail khi selected:

**`frm_titled_image_card.xml`** — thêm `View` overlay trong card:
```xml
<View
    android:id="@+id/shimmerLine"
    android:layout_width="match_parent"
    android:layout_height="3dp"
    android:background="@drawable/neon_shimmer_line"
    android:visibility="gone" />
```

**`ImageCardsAdt.kt`** — khi selected:
```kotlin
holder.shimmerLine.visibility = View.VISIBLE
val anim = ObjectAnimator.ofFloat(holder.shimmerLine, "translationY",
    -holder.card.height.toFloat(), holder.card.height.toFloat())
anim.duration = 800
anim.repeatCount = ValueAnimator.INFINITE
anim.start()
holder.shimmerLine.tag = anim
```

Khi deselect: cancel anim, `visibility = GONE`.

**`res/drawable/neon_shimmer_line.xml`**:
```xml
<shape>
    <gradient android:type="linear" android:angle="0"
        android:startColor="#0038F9E4"
        android:centerColor="#CC38F9E4"
        android:endColor="#0038F9E4" />
</shape>
```

---

## Files ảnh hưởng

- `ImageCardsAdt.kt` — shimmer start/stop khi select/deselect
- `frm_titled_image_card.xml` — thêm shimmerLine overlay
- `res/drawable/neon_shimmer_line.xml` — gradient drawable

## Acceptance

- [ ] Tap card → shimmer scan line chạy liên tục trên thumbnail
- [ ] Tap card khác → shimmer card cũ dừng, card mới bắt đầu
- [ ] `ANIMATOR_DURATION_SCALE == 0` → shimmer không chạy (respect anim settings)
- [ ] Không memory leak: anim cancel đúng khi card recycled (`onViewRecycled`)
- [ ] Không jank khi scroll grid nhanh
