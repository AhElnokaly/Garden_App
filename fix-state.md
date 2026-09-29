## fix-state: Garden Companion - v0.3 Garden Domain Foundation

**تاريخ:** 2026-09-28  **App:** Garden Companion  **Version:** 0.3.0

| # | المهمة | Status | QA Gate | Notes |
|---|--------|--------|---------|-------|
| 1 | إضافة كيانات وتعدادات النطاق الجديدة (Garden, Container, GrowingMethod, SoilProfile, DataSource) | done | PASS | ContainerType.kt, GrowingMethod.kt, DataSource.kt, Garden.kt, PlantContainer.kt, SoilProfile.kt, SoilComponent.kt |
| 2 | إنشاء محولات الأنواع وقاعدة البيانات Room ومسار الترحيل الآمن (MIGRATION_2_3) | done | PASS | GardenDatabase.kt (v3), GardenConverters.kt, MIGRATION_2_3 بدون أي فقدان بيانات |
| 3 | واجهات الاستعلامات الجديدة (DAOs: Garden, Container, SoilProfile, SoilComponent) | done | PASS | GardenDao, ContainerDao, SoilProfileDao, SoilComponentDao, PlaceDao, UserPlantDao |
| 4 | تحديث طبقة المستودع (GardenRepository) لربط التسلسل الهرمي الجديد | done | PASS | دعم الحدائق، الأوعية، الأماكن، وربط UserPlantWithDetails |
| 5 | تحديث نموذج العرض (GardenViewModel) والشاشات المتأثرة دون كسر المراجع | done | PASS | GardenViewModel.kt, PlacesScreen.kt, PlantsListScreen.kt, PlantDetailScreen.kt, PlantHeroCard.kt |
| 6 | ضبط مصادر البيانات (DataSource metadata) وتنظيف القيم الوهمية | done | PASS | GardenWeatherCard.kt, PlantGrowthGauge.kt تمييز البيانات النموذجية والمحسوبة |
| 7 | رفع إصدار التطبيق في build.gradle.kts إلى v0.3.0 | done | PASS | versionCode = 7, versionName = "0.3.0" |
| 8 | اختبارات الترحيل الآلي واختبارات التسلسل الهرمي (Migration & Unit Tests) | done | PASS | MigrationTest.kt (6/6 tests PASS), ExampleRobolectricTest.kt, build succeeded |



