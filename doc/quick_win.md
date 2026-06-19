# Quick Wins (Don dep & Toi uu nho)

Cap nhat audit: 2026-06-18. Trang thai hien tai: `./gradlew lintDebug` PASS, `./gradlew testDebugUnitTest` PASS.

## 0. Picked / can lam ngay

- [x] Fix lint error `UseAppTint`: doi `android:tint` sang `app:tint` cho `btUndo` trong `act_game.xml`.
- [x] Doi `BoardOptionsAct` va `GameAct` sang `android:exported="false"` vi la Activity noi bo.
- [x] Doi `String.format(...)` trong `GameAct.kt` sang format co `Locale`.
- [x] Chuyen setup cua `BoardOptionsFrm` tu `onActivityCreated` sang `onViewCreated`.
- [x] Doi `android:drawableTop` sang `app:drawableTopCompat` trong `frm_board_options.xml`.
- [x] Refactor `ImageCardsAdt`/`BoardOptionsFrm`: adapter giu drawable resource id, khong decode tat ca anh thanh `Bitmap` ngay tu dau.
- [x] Sua touch handling trong `GameBoard`: chi xu ly gesture hop le, goi `performClick()`, them override `performClick()`.

## 1. Unused Resources (Tài nguyên không sử dụng)

> [DONE 2026-06-19] Đã verify reference và xóa: 21 file anim, 4 drawable (`btn_red_blue_selector/shape`, `shape_board`, `drawable/ic_launcher_background`), 9 color + 12 dimen thừa. Giữ lại `ic_launcher_foreground` + `@color/ic_launcher_background` (cần cho adaptive icon) và các `md_theme_*`/style dùng qua theme. `lintDebug` PASS.

*Danh sách gốc (tham khảo):*

### Animation & Transitions

- `bounce_in`, `bounce_in.xml`
- `fade_in.xml`, `fade_out.xml`
- `slide_in_left.xml`, `slide_in_right.xml`
- `slide_out_left.xml`, `slide_out_right.xml`
- `scale_in.xml`, `scale_out.xml`
- `puzzle_piece_highlight.xml`, `puzzle_piece_move.xml`, `puzzle_piece_settle.xml`
- `puzzle_piece_shuffle.xml`, `puzzle_piece_unhighlight.xml`
- `puzzle_success_celebration.xml`
- `shuffle_chaos_effect.xml`
- `splash_logo_animation.xml`
- `ultra_smooth_fade_in.xml`
- `professional_button_hover.xml`
- `shared_element_enter.xml`, `shared_element_exit.xml`

### Drawable & Shapes

- `btn_red_blue_selector.xml`, `btn_red_blue_shape.xml`
- `shape_board.xml`
- `ic_launcher_background.xml`, `ic_launcher_foreground.xml`

### Colors

- `md_theme_*` (shadow, surfaceTint, etc.)
- `colorAccent`, `colorPrimaryDark`
- `board_background`
- `success`, `error`, `warning`, `info`

### Dimens

- `spacing_*` (unit, xs, sm, md, xl, xxl, xxxl)
- `small_corner_radius`, `elevation_dialog`
- `toolbar_height`, `card_image_height`

### Styles

- `Widget.App.*` (Button, GameButton, Outlined)
- `TextAppearance.App.*` (Headline, Title, Body, Label)

## 2. Code Cleanup

- **Namespaces**: Xóa khai báo `xmlns:tools` thừa trong `frm_board_options.xml`.
- **Release config**: Thay AdMob placeholder `"~"` bang gia tri tu secret/local config hoac fail fast khi build release.
- **High score**: `saveHighScore(moves, time)` dang khong dung `time`; can luu score theo moves/time hoac bo tham so.
- **Undo**: them test cho move -> undo -> shuffle/reset de khoa logic toa do.

## 3. TODOs (Các task tồn đọng)

Các task cần làm trong `MyApplication.kt`:

- [ ] Admob integration
- [ ] Ad Applovin integration
- [ ] Review in app feature
- [ ] Font scale handling
- [ ] 120hz support
- [ ] Rate, more app, share app features
- [ ] Github integration
- [ ] License info

## 4. Proposed Quick Win Features (Top 10)

1. [DONE] **Undo Functionality**: Cho phép người chơi hoàn tác nước đi sai (giới hạn 3 lần/game).
2. **Sound Effects (SFX)**: [Pending System] Thêm âm thanh khi di chuyển mảnh ghép và khi chiến thắng.
3. [DONE] **Haptic Feedback**: Rung nhẹ khi di chuyển mảnh ghép thành công.
4. **Dark Mode Support**: [Already Supported by Theme] Tối ưu giao diện cho chế độ tối.
5. [DONE] **Timer & Move Counter Visibility**: Hiển thị đồng hồ và số bước đi rõ ràng hơn trên màn hình chơi.
6. [DONE] **High Score System**: Lưu lại kỷ lục (thời gian/số bước).
7. [DONE] **Show Tile Numbers**: Toggle hiển thị/ẩn số thứ tự qua menu game, lưu `show_numbers` trong SharedPreferences.
8. [DONE] **Congratulation Dialog**: Hiển thị popup chúc mừng đẹp mắt.
9. [DONE] **Share Achievement**: Nút chia sẻ kết quả.
10. [DONE] **Board Preview**: Nút giữ để xem nhanh hình gốc.
