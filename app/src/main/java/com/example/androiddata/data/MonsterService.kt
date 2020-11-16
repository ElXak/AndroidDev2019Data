package com.example.androiddata.data

import retrofit2.Response
import retrofit2.http.GET

// Web service interface
interface MonsterService {
    // Annotation member of retrofit2.http
    @GET("/repo/feed/monster_data.json")
    // suspend means this fun is designed to be called within a coroutine
    suspend fun getMonsterData(): Response<List<Monster>>
}