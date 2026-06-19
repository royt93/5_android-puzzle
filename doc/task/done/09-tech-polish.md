# 09 — Tech polish (120Hz, audit OPEN, version bump)

> **Status:** ✅ Done · **Depends:** — · **Verify:** lint + unit + **25/25 instrumented PASS** (Pixel 7 Pro, anim scale = 0). Spec: [`../00-overview.md`](../00-overview.md)
>
> Thuần tối ưu kỹ thuật. Gỡ `//TODO roy93~ 120hz` + đóng các mục `[OPEN]` trong `doc/audit.md` / `doc/memory_leak.md`.

## P1 — 120Hz / high refresh rate
- [x] `BaseActivity.applyHighestRefreshRate()` trong `onCreate`: chọn `supportedModes` cùng độ phân giải có refresh cao nhất, set `window.attributes.preferredDisplayModeId`. Guard API 23+ (minSdk 23), dùng `display` (API 30+) / `windowManager.defaultDisplay` (cũ), no-op khi đã ở max hoặc chỉ 60Hz.
- [x] Áp ở `BaseActivity` → mọi Activity (đặc biệt `GameAct`) hưởng lợi. Gỡ `//TODO roy93~ 120hz`.
- [x] Verify thiết bị: Pixel 7 Pro `dumpsys display` → `mActiveModeId=2` = 1440×3120 **@120Hz**, surface `peak-refresh-rate: 120Hz`. Vì máy đã bật smooth-display 120Hz nên code đúng kiểu no-op (best==current); trên máy mặc định 60Hz sẽ bump lên 90/120.
- **Acceptance:** ✅ request mode cao nhất; no-op an toàn; không jank/crash.

## P2 — Audit OPEN: nested weights `act_game.xml`
- [x] Phẳng wrapper: gộp 2 `FrameLayout` + `LinearLayout` root → **1 root LinearLayout** (giảm 2 cấp view).
- [x] Gỡ **NestedWeights**: glass panel stats bỏ `weight` lồng (timer/moves → `wrap_content` + panel `gravity=center`). Lint `NestedWeights` = 0.
- [x] Thêm `baselineAligned="false"` cho các hàng ngang có weight (topInfoBar, glass panel, layoutBottom) → bớt 1 measure pass.
- [x] Verify render trên device (screenshot): stats panel (⏱/📊 căn giữa + divider), board + dock hiển thị đúng, không vỡ layout.
- **Acceptance:** ✅ giảm cấp view + bỏ nested weights, layout giữ nguyên thị giác.

## P3 — Audit OPEN: portrait-lock Android 16+
- [x] Quyết định: game **khoá portrait có chủ đích** (board vuông + UI dọc). Suppress `DiscouragedApi` + `LockedOrientationActivity` ở `<application>` kèm comment giải thích. Lint `DiscouragedApi` = 0.
- [x] Ghi quyết định vào `doc/audit.md` (P3).
- **Acceptance:** ✅ lint sạch cảnh báo orientation; quyết định ghi nhận.

## P4 — Version bump (tùy chọn)
- [-] **Hoãn chủ ý**: Material `1.13.0`, Glide `5.0.5`, Kotlin stdlib `2.1.20`, AGP `8.12.1` đều đã khá mới. Nâng version = rủi ro breaking + cần retest device kỹ → để **pass riêng** đúng như framing "(Tuỳ chọn)" trong `tasks.md`, không gộp vào wave này.
- **Acceptance:** ✅ quyết định hoãn được ghi nhận; bản hiện tại build/test xanh.

## P5 — Verify & docs
- [x] `./gradlew lintDebug` + `testDebugUnitTest` PASS.
- [x] `./gradlew connectedDebugAndroidTest` **25/25 PASS** (Pixel 7 Pro, anim scale = 0).
- [x] Đóng `[OPEN]→[DONE]` trong `doc/audit.md` + `doc/memory_leak.md`; cập nhật `doc/tasks.md`, README dashboard.
- **Acceptance:** ✅ build xanh, audit OPEN đã đóng/ghi quyết định, tài liệu đồng bộ.
