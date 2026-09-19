# CODE INDEX — Garden Companion (رفيق الحديقة)

> **قاعدة الحجم:** جميع الملفات أقل من 300 سطر (الحد الأقصى المسموح به 300 - 500 سطر).
> **آخر تحديث:** 2026-09-19

---

## 1. Screens (`com.example.ui.screens`)
| File | Lines | Description & Responsibilities |
|---|---|---|
| `PlantsListScreen.kt` | 253 | شاشة الحديقة الرئيسية، الفلاتر (الكل / يحتاج عناية)، وقائمة النباتات |
| `PlantDetailScreen.kt` | 247 | شاشة تفاصيل النبتة والأصيص، الرعاية السريعة، وسجل العناية |
| `AddPlantScreen.kt` | 192 | شاشة إضافة نبتة جديدة من الكتالوج مع البحث وفلاتر التصنيفات |

---

## 2. Reusable UI Components (`com.example.ui.components`)
| File | Lines | Description & Responsibilities |
|---|---|---|
| `UserPlantCard.kt` | 177 | بطاقة النبتة في قائمة الحديقة مع تنبيه الري وزر السقاية السريعة |
| `PlantHeroCard.kt` | 167 | بطاقة الهيدر العلوية في شاشة التفاصيل (الاسم، الموقع، حالة الري، الضوء) |
| `AddPlantNicknameDialog.kt` | 163 | نافذة تحديد الاسم المستعار للأصيص ومكان وضعه وتأكيد الإضافة |
| `CareLogItemCard.kt` | 153 | عنصر بطاقة سجل العناية والتاريخ (ماء، سماد، صورة، ملاحظة) مع التنسيق الزمني |
| `CatalogPlantCard.kt` | 147 | بطاقة النبتة في كتالوج الإضافة مع دورة الري واحتياج الإضاءة وزر الاختيار |
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
| `GardenRepository.kt` | 114 | مستودع البيانات وربط الـ DAOs وعمليات الـ Flow والـ Transactions |
| `GardenDatabase.kt` | 57 | قاعدة بيانات Room المحلية مع التهيئة والتعبئة المسبقة |
| `dao/PlantDao.kt` | 29 | واجهة استعلامات كتالوج النباتات العامة |
| `dao/CareLogDao.kt` | 29 | واجهة استعلامات سجلات الرعاية |
| `dao/UserPlantDao.kt` | 27 | واجهة استعلامات نباتات المستخدم وأصص الحديقة |
| `model/CareLog.kt` | 28 | كيان سجل العناية (ماء، سماد، صورة، ملاحظة) |
| `model/UserPlant.kt` | 27 | كيان أصيص المستخدم في الحديقة |
| `model/UserPlantWithDetails.kt` | 25 | فئة الدمج العلائقية لحساب أيام الري وموعد السقاية |
| `model/Plant.kt` | 16 | كيان النبتة العامة في الكتالوج |

---

## 4. ViewModel & App Lifecycle (`com.example`)
| File | Lines | Description & Responsibilities |
|---|---|---|
| `updater/GitHubAppUpdater.kt` | 196 | محرك فحص وتنزيل وتثبيت التحديثات تلقائياً عبر GitHub Releases |
| `ui/viewmodel/GardenViewModel.kt` | 187 | نموذج العرض وإدارة حالة الشاشات والفلاتر وتدفق البيانات |
| `MainActivity.kt` | 86 | النشاط الرئيسي ونظام التنقل الموجه Compose Navigation |
| `ui/theme/Theme.kt` | 83 | ثيم Material 3 وألوان وخطوط الطبيعة والنباتات |
| `ui/theme/Color.kt` | 38 | لوحة ألوان الحديقة (الأخضر، الترابي، الأزرق المائي) |
| `ui/theme/Type.kt` | 36 | نمط وتدرجات الخطوط والطباعة |
| `ai/AiFeatureGuard.kt` | 25 | صمام أمان ميزات الذكاء الاصطناعي (معطلة افتراضياً) |
