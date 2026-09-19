package com.example.data

import com.example.data.model.Plant

object InitialPlantData {
    val initialPlants = listOf(
        Plant(
            id = 1,
            name_ar = "نعناع بلدي",
            name_en = "Spearmint",
            category = "herb",
            water_frequency_days = 2,
            light_needs = "شمس خفيفة إلى متوسطة (نصف ظل)",
            icon_ref = "herb_mint"
        ),
        Plant(
            id = 2,
            name_ar = "ريحان إيطالي",
            name_en = "Sweet Basil",
            category = "herb",
            water_frequency_days = 3,
            light_needs = "شمس مباشرة 6 ساعات يومياً",
            icon_ref = "herb_basil"
        ),
        Plant(
            id = 3,
            name_ar = "طماطم كرزية",
            name_en = "Cherry Tomato",
            category = "vegetable",
            water_frequency_days = 2,
            light_needs = "شمس مباشرة كاملة",
            icon_ref = "veg_tomato"
        ),
        Plant(
            id = 4,
            name_ar = "بقدونس",
            name_en = "Parsley",
            category = "herb",
            water_frequency_days = 2,
            light_needs = "شمس معتدلة غير حارقة",
            icon_ref = "herb_parsley"
        ),
        Plant(
            id = 5,
            name_ar = "صبار الألوفيرا",
            name_en = "Aloe Vera",
            category = "ornamental",
            water_frequency_days = 12,
            light_needs = "إضاءة ساطعة غير مباشرة",
            icon_ref = "succulent_aloe"
        ),
        Plant(
            id = 6,
            name_ar = "ورد جوري",
            name_en = "Damask Rose",
            category = "ornamental",
            water_frequency_days = 3,
            light_needs = "شمس مباشرة وفيرة",
            icon_ref = "flower_rose"
        ),
        Plant(
            id = 7,
            name_ar = "زنبق السلام",
            name_en = "Peace Lily",
            category = "ornamental",
            water_frequency_days = 5,
            light_needs = "ظل جزئي وإضاءة خافتة",
            icon_ref = "flower_lily"
        ),
        Plant(
            id = 8,
            name_ar = "نبتة البوتس (قلب عبد الوهاب)",
            name_en = "Golden Pothos",
            category = "ornamental",
            water_frequency_days = 7,
            light_needs = "إضاءة غير مباشرة متوسطة",
            icon_ref = "leaf_pothos"
        ),
        Plant(
            id = 9,
            name_ar = "شجرة ليمون قزمي",
            name_en = "Dwarf Lemon Tree",
            category = "vegetable",
            water_frequency_days = 4,
            light_needs = "شمس ساطعة ومباشرة بالبلكونة",
            icon_ref = "tree_lemon"
        ),
        Plant(
            id = 10,
            name_ar = "زعتر بري",
            name_en = "Thyme",
            category = "herb",
            water_frequency_days = 5,
            light_needs = "شمس كاملة وجو جاف",
            icon_ref = "herb_thyme"
        ),
        Plant(
            id = 11,
            name_ar = "جلد النمر (سانسيفيريا)",
            name_en = "Snake Plant",
            category = "ornamental",
            water_frequency_days = 14,
            light_needs = "يتحمل الإضاءة المنخفضة إلى الساطعة",
            icon_ref = "succulent_snake"
        ),
        Plant(
            id = 12,
            name_ar = "إكليل الجبل (روزماري)",
            name_en = "Rosemary",
            category = "herb",
            water_frequency_days = 4,
            light_needs = "شمس مباشرة وحرارة معتدلة",
            icon_ref = "herb_rosemary"
        ),
        Plant(
            id = 13,
            name_ar = "فلفل حار",
            name_en = "Hot Chili Pepper",
            category = "vegetable",
            water_frequency_days = 3,
            light_needs = "شمس مباشرة دافئة",
            icon_ref = "veg_chili"
        ),
        Plant(
            id = 14,
            name_ar = "نبتة الزاميا الخضراء",
            name_en = "ZZ Plant",
            category = "ornamental",
            water_frequency_days = 14,
            light_needs = "ظل وإضاءة غير مباشرة",
            icon_ref = "leaf_zz"
        )
    )
}
