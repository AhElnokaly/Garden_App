# CODE INDEX — Garden Companion (رفيق الحديقة)

> **قاعدة الحجم:** جميع الملفات أقل من 300 سطر (الحد الأقصى المسموح به 300 - 500 سطر).
> **آخر تحديث:** 2026-09-20 (إصدار v0.2.0)

---

## 1. Screens (`com.example.ui.screens`)
| File | Lines | Description & Responsibilities |
|---|---|---|
| `PlantsListScreen.kt` | 275 | شاشة الحديقة الرئيسية، فلاتر الرعاية (الكل / يحتاج عناية)، وفلاتر الأماكن المتعددة |
| `PlantDetailScreen.kt` | 248 | شاشة تفاصيل النبتة والأصيص، الرعاية السريعة، وعرض المكان وسجل العناية |
| `AddPlantScreen.kt` | 195 | شاشة إضافة نبتة جديدة مع تحديد المكان (مكان حالي أو إضافة مكان جديد) |

---

## 2. Reusable UI Components (`com.example.ui.components`)
| File | Lines | Description & Responsibilities |
|---|---|---|
| `AddPlantNicknameDialog.kt` | 205 | نافذة تحديد اسم الأصيص واختيار المكان أو إضافة مكان جديد وتأكيد الحفظ |
| `PlantHeroCard.kt` | 198 | بطاقة الهيدر العلوية في شاشة التفاصيل (الاسم، المكان، دورة الري، الضوء) |
| `UserPlantCard.kt` | 185 | بطاقة النبتة في الحديقة مع شارة المكان وتنبيه الري وزر السقاية السريعة |
| `CareLogItemCard.kt` | 153 | عنصر بطاقة سجل العناية والتاريخ (ماء، سماد، صورة، ملاحظة) |
| `CatalogPlantCard.kt` | 147 | بطاقة النبتة في كتالوج الإضافة مع دورة الري واحتياج الإضاءة |
| `QuickCareActionsGrid.kt` | 120 | شبكة أزرار الرعاية السريعة الأربعة (سقيت، سمّدت، صوّرت، ملاحظة) |
| `UpdateDialog.kt` | 107 | نافذة فحص وتنزيل وتثبيت تحديثات التطبيق عبر GitHub Releases |
| `PlantDetailDialogs.kt` | 98 | نوافذ تأكيد حذف الأصيص وإضافة ملاحظة عناية جديدة |
| `EmptyPlantsState.kt` | 97 | واجهة الحالة الفارغة للحديقة وقائمة النباتات المحتاجة للري |
| `PlantAvatar.kt` | 95 | الأيقونة الرمزية للنباتات مع شارات التصنيف (أعشاب، خضار، زينة) |

---

## 3. Data & Persistence (`com.example.data`)
| File | Lines | Description & Responsibilities |
|---|---|---|
| `InitialPlantData.kt` | 134 | كتالوج البيانات الأولية (14 نبتة عربية/إنجليزية مع تردد الري والضوء) |
| `GardenRepository.kt` | 135 | مستودع البيانات وربط الـ DAOs وعمليات الأماكن والري والرعاية |
| `GardenDatabase.kt` | 118 | قاعدة بيانات Room المحلية مع Migration 1->2 للأماكن و Place entity |
| `dao/PlaceDao.kt` | 33 | واجهة استعلامات وإدارة الأماكن (Places) |
| `dao/PlantDao.kt` | 29 | واجهة استعلامات كتالوج النباتات العامة |
| `dao/CareLogDao.kt` | 29 | واجهة استعلامات سجلات الرعاية |
| `dao/UserPlantDao.kt` | 27 | واجهة استعلامات نباتات المستخدم وأصص الحديقة |
| `model/Place.kt` | 13 | كيان جدول الأماكن (places) |
| `model/UserPlant.kt` | 35 | كيان أصيص المستخدم في الحديقة مع المفتاح الأجنبي place_id |
| `model/UserPlantWithDetails.kt` | 33 | فئة الدمج العلائقية لحساب أيام الري وموعد السقاية واسم المكان |
| `model/CareLog.kt` | 28 | كيان سجل العناية (ماء، سماد، صورة، ملاحظة) |
| `model/Plant.kt` | 17 | كيان النبتة العامة في الكتالوج |

---

## 4. Notifications & Background Work (`com.example.notifications`)
| File | Lines | Description & Responsibilities |
|---|---|---|
| `DailyWaterCheckWorker.kt` | 148 | عامل WorkManager اليومي (8:00 صباحاً) لفحص النباتات وتوليد إشعار مجمّع |
| `WaterCareReceiver.kt` | 52 | مستقبل البث (BroadcastReceiver) لزر "تم السقي" السريع من الإشعار |
| `NotificationScheduler.kt` | 50 | جدولة الفحص الدوري اليومي عبر WorkManager |

---

## 5. ViewModel & App Lifecycle (`com.example`)
| File | Lines | Description & Responsibilities |
|---|---|---|
| `ui/viewmodel/GardenViewModel.kt` | 198 | نموذج العرض وإدارة حالة الشاشات، فلاتر الأماكن والري، وجدولة التنبيهات |
| `updater/GitHubAppUpdater.kt` | 196 | محرك فحص وتنزيل وتثبيت التحديثات تلقائياً عبر GitHub Releases |
| `MainActivity.kt` | 108 | النشاط الرئيسي، طلب صلاحية POST_NOTIFICATIONS، ونظام التنقل |
| `ui/theme/Theme.kt` | 83 | ثيم Material 3 وألوان وخطوط الطبيعة والنباتات |
| `ui/theme/Color.kt` | 44 | لوحة ألوان الحديقة وتنبيهات الري (AlertRed, SkyBlue, Green) |
| `ui/theme/Type.kt` | 36 | نمط وتدرجات الخطوط والطباعة |
| `ai/AiFeatureGuard.kt` | 25 | صمام أمان ميزات الذكاء الاصطناعي (معطلة افتراضياً) |
