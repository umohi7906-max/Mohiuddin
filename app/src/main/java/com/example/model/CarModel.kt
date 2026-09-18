package com.example.model

import androidx.compose.ui.graphics.Color

data class CarModel(
    val id: String,
    val name: String,
    val nameBn: String,
    val description: String,
    val descriptionBn: String,
    val price: Int,
    val topSpeedKmh: Int,
    val handling: Float, // 0.8f to 1.5f
    val baseHealth: Int, // 2 to 4
    val primaryColor: Color,
    val accentColor: Color,
    val perk: String,
    val perkBn: String
)

object CarCatalog {
    val ALL_CARS = listOf(
        CarModel(
            id = "turbo_blaze",
            name = "Red Blaze",
            nameBn = "লাল ঝড়",
            description = "Iconic agile street racer with balanced acceleration.",
            descriptionBn = "চমৎকার ব্যালেন্স ও দ্রুত গতির আইকনিক রেসার।",
            price = 0,
            topSpeedKmh = 180,
            handling = 1.0f,
            baseHealth = 3,
            primaryColor = Color(0xFFE11D48),
            accentColor = Color(0xFFFBBF24),
            perk = "Balanced Handling",
            perkBn = "সুষম নিয়ন্ত্রণ"
        ),
        CarModel(
            id = "cyber_lightning",
            name = "Cyber Bolt",
            nameBn = "সাইবার বোল্ট",
            description = "High-tech electric hypercar with razor sharp turns.",
            descriptionBn = "উন্নত প্রযুক্তির ইলেকট্রিক সুপারকার, নিখুঁত টার্নিং।",
            price = 300,
            topSpeedKmh = 210,
            handling = 1.25f,
            baseHealth = 2,
            primaryColor = Color(0xFF06B6D4),
            accentColor = Color(0xFFA855F7),
            perk = "Agile Cornering",
            perkBn = "দ্রুত ডজ করার সুবিধা"
        ),
        CarModel(
            id = "golden_fury",
            name = "Golden Fury",
            nameBn = "সোনালী ফিউরি",
            description = "Luxury gold supercar with magnetic alloy chassis.",
            descriptionBn = "স্বর্ণালী লাক্সারি কার, কয়েন আকর্ষণের বিশেষ ক্ষমতা।",
            price = 700,
            topSpeedKmh = 230,
            handling = 1.1f,
            baseHealth = 3,
            primaryColor = Color(0xFFF59E0B),
            accentColor = Color(0xFF1E293B),
            perk = "Auto Coin Pull",
            perkBn = "কাছের কয়েন নিজে টেনে নেয়"
        ),
        CarModel(
            id = "shadow_reaper",
            name = "Night Stealth",
            nameBn = "কালো চিতা",
            description = "Stealth carbon hypercar tuned for maximum nitro thrust.",
            descriptionBn = "কার্বন ফাইবার বডি ও ভয়ংকর নাইট্রো পাওয়ার।",
            price = 1400,
            topSpeedKmh = 260,
            handling = 1.35f,
            baseHealth = 4,
            primaryColor = Color(0xFF4F46E5),
            accentColor = Color(0xFF10B981),
            perk = "Super Nitro & Armor",
            perkBn = "অতিরিক্ত নাইট্রো ও মজবুত আর্মার"
        )
    )

    fun getCarById(id: String): CarModel {
        return ALL_CARS.find { it.id == id } ?: ALL_CARS.first()
    }
}
