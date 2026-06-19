# 08 — Engagement features (Rate / Share / More / About)

> **Status:** ✅ Done · **Depends:** — · **Verify:** lint + unit + **25/25 instrumented PASS** (Pixel 7 Pro, anim scale = 0). Spec: [`../00-overview.md`](../00-overview.md)
>
> Tăng tương tác người dùng. **Không cần SDK quảng cáo.**
>
> Khi bắt tay phát hiện **Rate / More apps / Share đã có sẵn** trong `BoardOptionsFrm` (footer `btnRate`/`btnMore`/`btnShare`). Phần thực sự thiếu = **About/License/GitHub**, đã bổ sung.

## E1 — Rate app
- [x] `btnRate` (footer): `market://details?id=<pkg>` + fallback `https://play.google.com/...`. **Đã có sẵn từ trước.**
- [-] In-App Review API: **bỏ chủ ý** wave này — tránh thêm dependency `play:review`; deep-link Rate đã đủ. TODO `review in app` giữ lại (optional).
- **Acceptance:** ✅ Rate mở store/web, fallback an toàn.

## E2 — More apps
- [x] `btnMore` (footer): `market://search?q=pub:SAIGON PHANTOM LABS` + fallback. **Đã có sẵn từ trước.**
- **Acceptance:** ✅ mở trang developer, có fallback.

## E3 — About / License / GitHub
- [x] Thêm cột **About** (cột 4) vào footer `frm_board_options.xml` + `ic_about.xml` (neon icon trắng) + string `about`.
- [x] `BoardOptionsFrm.showAboutDialog()` reuse `DialogUtils.showGameDialog`: hiển thị tên app + `BuildConfig.VERSION_NAME` + developer + **license OSS** (AppCompat, Material, Glide, grid-view, LeakCanary).
- [x] Nút **GitHub** (yesText) mở `https://github.com/tplloi/android-puzzle` qua `ACTION_VIEW`, try/catch không crash khi thiếu browser. Gỡ `//TODO roy93~ github` + `license` trong `MyApplication.kt`.
- **Acceptance:** ✅ About hiển thị version đúng, GitHub/license mở được.

## E4 — Điểm vào & UI
- [x] Điểm vào: footer màn chính (`BoardOptionsAct`/`BoardOptionsFrm`) — đồng nhất với Rate/More/Share sẵn có.
- [x] UI neon: cột About dùng cùng style token (`neon_text_secondary`, `glass_stroke` divider) như 3 cột cũ; dialog dùng `dialog_game_custom` neon.
- [x] String i18n hoá toàn bộ (`about*`, url cũng đặt trong strings).
- **Acceptance:** ✅ đúng design neon, không hard-code text.

## E5 — Test & verify
- [x] Instrumented `AboutDialogTest`: bấm `btnAbout` → dialog About hiện nút GitHub (`inRoot(isDialog())`).
- [x] `./gradlew lintDebug` + `testDebugUnitTest` + `connectedDebugAndroidTest` **25/25 PASS** (anim scale = 0, Pixel 7 Pro).
- [x] Cập nhật `MyApplication.kt`, `doc/quick_win.md` §3, `doc/tasks.md`, README dashboard.
- **Acceptance:** ✅ build xanh, intent đúng, tài liệu đồng bộ.

**Tests bổ sung (1):** `AboutDialogTest`. Tổng **25 instrumented + unit PASS**.
