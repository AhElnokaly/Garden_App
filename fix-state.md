## fix-state: Garden Companion - CI/CD Release Pipeline & In-App Update

**تاريخ:** 2026-09-20  **App:** Garden Companion  **Version:** 0.2.0

| # | المهمة | Status | QA Gate | Notes |
|---|--------|--------|---------|-------|
| 1 | تصحيح إحداثيات مستودع GitHub في `GitHubAppUpdater.kt` | done | PASS | تم التحديث من `yasserayasserh/garden-companion` إلى `AhElnokaly/Garden_App` |
| 2 | تحديث إصدار التطبيق `versionCode=2` و `versionName="0.2.0"` | done | PASS | تحديث `app/build.gradle.kts` ليتوافق مع v0.2 |
| 3 | تفعيل KSP2 ووضع Headless في `gradle.properties` | done | PASS | إضافة `ksp.use.ksp2=true` و `-Djava.awt.headless=true` |
| 4 | ضبط صلاحيات `permissions: contents: write` وإعداد JDK 21 في `android.yml` | done | PASS | حل مشكلة خطأ 403 وصلاحيات إنشاء الإصدار والتنزيل |
| 5 | التحقق من صحة البناء المحلي وتنفيذ الاختبارات | done | PASS | `./gradlew assembleDebug` و `./gradlew testDebugUnitTest` نجحا بنسبة 100% |
