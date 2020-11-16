package com.example.androiddata.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// DAO (Data Access Object) Interface: Typically you have 1 DAO for each entity, but you can design
// however you like. The purpose of DAO is to define database operations,
// queries - insert, update and delete operations and so on

@Dao
interface MonsterDao {

    // Define each of operations you want to support
    // Query operations
    @Query("SELECT * from monsters")
    fun getAll(): List<Monster>

    // Insert operation, all operation except SELECT will have keyword suspend so that they
    // designed to work with coroutines
    @Insert
    suspend fun insertMonster(monster: Monster)

    // Bulk insert
    @Insert
    suspend fun insertMonsters(monsters: List<Monster>)

    @Query("DELETE from monsters")
    suspend fun deleteAll()

}