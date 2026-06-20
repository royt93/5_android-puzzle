# Task 18 — Settings: Accent Swatch Active Indicator

**Priority:** P1 — functional UX gap (user không biết theme nào đang chọn)

## Vấn đề hiện tại

- 4 swatch buttons (cyan/magenta/lime/violet) đều trông giống nhau
- Không có visual indicator nào cho biết swatch nào đang active/selected
- `app:strokeWidth="2dp"` được khai báo trong XML nhưng `strokeColor` mặc định → invisible
- User phải đoán màu nào đang chọn

## Mục tiêu

Swatch đang active có:
1. Viền trắng 3dp + neon glow (shadowLayer của nền)
2. Scale to 1.15x (nhấn mạnh selection)
3. Swatch không active: stroke transparent, scale 1.0x

## Thay đổi cần làm

### `SettingsActivity.kt`
```kotlin
private fun updateSwatchSelection(selectedKey: String) {
    val swatches = mapOf(
        "cyan" to binding.swatchCyan,
        "magenta" to binding.swatchMagenta,
        "lime" to binding.swatchLime,
        "violet" to binding.swatchViolet,
    )
    swatches.forEach { (key, btn) ->
        val isSelected = key == selectedKey
        btn.strokeWidth = if (isSelected) 3.dpToPx() else 0
        btn.strokeColor = ColorStateList.valueOf(Color.WHITE)
        btn.animate()
            .scaleX(if (isSelected) 1.15f else 1.0f)
            .scaleY(if (isSelected) 1.15f else 1.0f)
            .setDuration(150)
            .start()
        // Glow shadow cho swatch đang chọn
        if (isSelected) {
            btn.elevation = 12f
        } else {
            btn.elevation = 2f
        }
    }
}
```

Gọi `updateSwatchSelection(Prefs.getAccentKey(this))` trong `onCreate()`.
Gọi lại trong mỗi swatch click listener.

### `act_settings.xml` — swatch container
Thêm `android:clipChildren="false"` và `android:clipToPadding="false"` vào LinearLayout chứa 4 swatches → cho phép shadow/scale overflow.

## File ảnh hưởng
- `app/src/main/java/com/helpmepls/slidepuzzle/act/SettingsActivity.kt`
- `app/src/main/res/layout/act_settings.xml`

## Acceptance
- [ ] Active swatch: viền trắng + scale 1.15x + elevated
- [ ] Inactive swatches: no border, scale 1.0x
- [ ] State persist đúng khi reopen Settings
- [ ] Transition animation 150ms khi switch theme
