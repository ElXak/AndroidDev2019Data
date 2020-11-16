package com.example.androiddata.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androiddata.IMAGE_BASE_URL
import com.squareup.moshi.Json

// Data Class is very good for keeping data. it has many functions like toString, get, set
// in addition to representing data entities that can be stored in RAM this class can also be used
// to define sql lite database

// Annotation from androidx.room. If you don't add tableName attribute,
// it will be calculated automatically by room
// you need to create Entity for each table
@Entity(tableName = "monsters")
data class Monster(

    // Primary key for database table by annotation from room
    @PrimaryKey(autoGenerate = true)
    val monsterId: Int,
//    if variable name doesn't match json name use annotation. It comes from Moshi library
//    @Json(name = "monsterName") val name: String,
    val monsterName: String,
    val imageFile: String,
    val caption: String,
    val description: String,
    val price: Double,
    val scariness: Int
) {
    // read-only property
    val imageUrl
        // explicit getter fun which calculates image url
        get() = "$IMAGE_BASE_URL/$imageFile.webp"
    // read-only property
    val thumbnailUrl
        // explicit getter fun
        get() = "$IMAGE_BASE_URL/${imageFile}_tn.webp"

}
