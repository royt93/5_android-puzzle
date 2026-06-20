# Task 16 — Game Screen: Ambient Board Background Bloom

**Priority:** P1 — bổ sung chiều sâu cho màn game

## Vấn đề hiện tại

- Màn `act_game.xml`: `vgBoardContainer` không có background riêng → board nằm trên nền tối đồng nhất, không có cảm giác "neon glow tỏa ra từ board"
- Không có hiệu ứng "ambient light" — khi nhìn board, ánh sáng neon không toả ra nền xung quanh
- Thanh bottom dock (SHUFFLE/UNDO/RESET) và stats panel thiếu shadow neon dưới (elevation đang set nhưng colored shadow chưa kích hoạt)

## Mục tiêu

1. Thêm `GlowAmbientView` (hoặc drawable radial gradient) làm background của `vgBoardContainer`
2. Hiệu ứng: radial gradient từ tâm board tỏa ra, màu `neon_cyan_bloom` (#3338F9E4 → #0038F9E4)
3. Bottom dock và stats panel: NeonGlow elevation shadow visible và đúng màu accent

## Thay đổi cần làm

### Phương án A (đơn giản, không cần custom view)
Thêm `ImageView` làm ambient background trong `vgBoardContainer`:
```xml
<ImageView
    android:id="@+id/boardAmbientGlow"
    android:layout_width="0dp"
    android:layout_height="0dp"
    android:src="@drawable/neon_ambient_bloom"
    android:scaleType="fitXY"
    android:importantForAccessibility="no"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent" />
```

### Tạo `drawable/neon_ambient_bloom.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval">
    <gradient
        android:type="radial"
        android:gradientRadius="70%"
        android:startColor="@color/neon_cyan_bloom"
        android:endColor="@color/neon_cyan_bloom_out" />
</shape>
```
→ Đặt view này TRƯỚC `boardView` trong z-order (hoặc set elevation thấp hơn)

### `GameAct.kt` — colored elevation shadow
Material3 elevation shadow mặc định là xám. Để có colored shadow:
```kotlin
// Trong setupBoard() hoặc onCreate():
ViewCompat.setElevation(binding.statsPanel, 8f)
ViewCompat.setElevation(binding.layoutBottom, 8f)
// Outlines:
binding.statsPanel.outlineProvider = ViewOutlineProvider.BACKGROUND
binding.layoutBottom.outlineProvider = ViewOutlineProvider.BACKGROUND
```

### `neon_bg_ambient.xml` — kiểm tra và dùng trong theme
Drawable này đã tồn tại — đảm bảo nó được set làm `android:background` cho `layoutRoot` trong `act_game.xml`.

## File ảnh hưởng
- `app/src/main/res/layout/act_game.xml`
- `app/src/main/res/drawable/neon_ambient_bloom.xml` (tạo mới)
- `app/src/main/java/com/helpmepls/slidepuzzle/act/GameAct.kt`

## Acceptance
- [ ] Nhìn game screen thấy vầng sáng tỏa ra từ phía board
- [ ] Stats panel và bottom dock có shadow màu accent (không chỉ xám)
- [ ] Không ảnh hưởng touch/slide của board
