## fix-state: Garden Companion - In-App Update UX & CI Signing Fix (v0.2.2)

**تاريخ:** 2026-09-25  **App:** Garden Companion  **Version:** 0.2.2

| # | المهمة | Status | QA Gate | Notes |
|---|--------|--------|---------|-------|
| 1 | استبدال containerColor والأشكال في `QuickCareActionsGrid.kt` بتوكنز التصميم | done | PASS | استخدام SkyBlueWater, SoilBrownSecondary, GreenPrimary, Terracotta و MaterialTheme.shapes.small |
| 2 | استبدال الألوان الهاردكود والأشكال في `CareLogItemCard.kt` بتوكنز التصميم | done | PASS | استخدام GreenPrimary/Container, Terracotta/Container و MaterialTheme.shapes (medium & extraSmall) |
| 3 | تحسين شاشة التنزيل ومنع إغلاقها العرضي مع دعم الإلغاء والتثبيت المباشر | done | PASS | `DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = false)` أثناء التحميل، وزر إلغاء صريح، وحالة التثبيت المباشر |
| 4 | دعم إلغاء التحميل وتنظيف الموارد في `GitHubAppUpdater` و `GardenViewModel` | done | PASS | إضافة `cancelDownload()`، حذف الملف المؤقت غير المكتمل، تتبع `updateDownloadJob` في الـ ViewModel |
| 5 | تثبيت مفتاح التوقيع الرقمي الموحد في سير عمل GitHub Actions | done | PASS | حقن keystore ثابت ومحدد في `~/.android/debug.keystore` في `android.yml` لتوحيد بصمة SHA-256 للملفات |
| 6 | رفع رقم الإصدار إلى `versionCode=4` و `versionName="0.2.2"` | done | PASS | تحديث `app/build.gradle.kts` ليكون v0.2.2 متوافق مع Release القادم |
| 7 | التحقق من صحة البناء المحلي وتنفيذ الاختبارات | done | PASS | تم التحقق بنجاح محلياً |
