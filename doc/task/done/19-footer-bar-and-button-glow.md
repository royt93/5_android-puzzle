# Task 19 — Footer Bar & Button Glow Upgrade

**Priority:** P2 — polish, không blocking nhưng tăng visual impact đáng kể

## Vấn đề hiện tại

### Board Options footer bar:
- Icon + text buttons dùng `textColor="@color/neon_text_secondary"` (màu mờ)
- `shadowRadius="5"` quá nhỏ → glow gần như không thấy
- Không có active/pressed indicator rõ (chỉ `selectableItemBackgroundBorderless` mặc định)

### Game bottom dock (SHUFFLE/UNDO/RESET):
- UNDO button dùng `@android:drawable/ic_menu_revert` → icon hệ thống, không phải neon custom
- Button height chỉ 48dp → hơi nhỏ cho Pixel 7 Pro (màn 6.7")
- `neon_btn_primary` và `neon_btn_danger` — cần kiểm tra có glow shadow stroke không

## Mục tiêu

### Footer bar (Board Options):
1. Icon size tăng từ 24dp → 28dp
2. `shadowRadius` tăng từ 5 → 10 cho tất cả icon labels
3. Pressed state: icon + text tạm thời đổi sang `colorPrimary` (full brightness)

### Game dock:
1. Thay UNDO icon bằng custom `ic_undo_neon.xml` (vector neon)
2. Button height 48dp → 52dp
3. `neon_btn_primary.xml`: kiểm tra đủ stroke 2dp + glow tint

### `neon_btn_primary.xml` (drawable)
Đảm bảo có:
```xml
<stroke android:width="2dp" android:color="?attr/colorPrimary" />
<solid android:color="@color/neon_bg_elevated" />
```
Thêm selector state `pressed` với `solid android:color="?attr/colorPrimary"` + text đổi sang dark.

## File ảnh hưởng
- `app/src/main/res/layout/frm_board_options.xml` (footer icon shadow)
- `app/src/main/res/layout/act_game.xml` (button height)
- `app/src/main/res/drawable/neon_btn_primary.xml`
- `app/src/main/res/drawable/neon_btn_danger.xml`
- `app/src/main/res/drawable/ic_undo_neon.xml` (tạo mới — vector)

## Acceptance
- [ ] Footer icons sáng hơn, shadow glow rõ hơn
- [ ] UNDO button có icon tùy chỉnh (không phải system icon)
- [ ] Button pressed state có visual feedback neon
- [ ] Không regression layout trên màn hình nhỏ (≤5.5")
