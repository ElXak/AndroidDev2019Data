package com.example.androiddata.data

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.androiddata.TAG
import com.example.androiddata.WEB_SERVICE_URL
import com.example.androiddata.utilities.FileHelper
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

// Encapsulates the logic of getting data. Decides whether getting data from web service, cache or file
// Once you defined Database with room you can use it from anywhere of your app,
// but we architect to access database from 1 place, repository
class MonsterRepository(val app: Application) {

    // for publisher-subscriber pattern: in this pattern Repository acquires the data, but instead of returning data it publishes it
    // by using a component called LiveData. Any other component in the application can subscribe to handle changes to the data using
    // a pattern called a observer
    val monsterData = MutableLiveData<List<Monster>>()

    // ParameterizedType is used in creating Moshi adapter
    private val listType = Types.newParameterizedType(
        List::class.java, Monster::class.java
    )

    // instance of DAO
    private val monsterDao = MonsterDatabase.getDatabse(app).monsterDao()

    // runs on initialization
    init {
/* deleted after sql implementation. it was implementation of json web service with data caching
//        getMonsterData()
        // read data from cache
        val data = readDataFromCache()
        // if data in cache is empty read data from web
        if (data.isEmpty()) {
            refreshDataFromWeb()
        } else {
            // if data from cache is not empty we will put it in monsterData
            monsterData.value = data
            Log.i(TAG, "Using local data")
        }
//        Log.i(TAG, "Network available: ${networkAvailable()}")
*/
        // do all work in background thread
        CoroutineScope(Dispatchers.IO).launch {
            // try to get all data from database
            val data = monsterDao.getAll()
            if (data.isEmpty()) {
                // if data is empty read get it from WebService
                callWebService()
            } else {
                // else post it to live data
                monsterData.postValue(data)

                // Toast can not be called from background thread
                // Toast architecture is designed to be called from foreground thread
                // But we can switch thread on the fly
                withContext(Dispatchers.Main) {
                    Toast.makeText(app, "Using local data", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

//    moved from View Model
//    fun getMonsterData(context: Context): List<Monster> {
//        val text = FileHelper.getTextFromResources(app, R.raw.monster_data)
//        val text = FileHelper.getTextFromAssets(app, "monster_data.json")
//        val moshi = Moshi.Builder()
//            // use this function when var name in Class doesn't match with jason name
//            .add(KotlinJsonAdapterFactory())
//            .build()
//        val adapter: JsonAdapter<List<Monster>> = moshi.adapter(listType)
//        return adapter.fromJson(text) ?: emptyList()
//    }

    // getMonsterData in pattern of LiveData no parameter and no return. it publishes to rest of
    // application through LiveData object
    fun getMonsterData() {
//        val text = FileHelper.getTextFromResources(app, R.raw.monster_data)
        val text = FileHelper.getTextFromAssets(app, "monster_data.json")
        val adapter: JsonAdapter<List<Monster>> = jsonAdapter()
        //moved to fun jsonAdapter()
/*
        val moshi = Moshi.Builder()
                // use this function when var name in Class doesn't match with jason name
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter: JsonAdapter<List<Monster>> = moshi.adapter(listType)
*/
        monsterData.value = adapter.fromJson(text) ?: emptyList()
    }

    // @WorkerThread annotation is an indicator that this function will be called in a background thread
    @WorkerThread
    suspend fun callWebService() {
        if (networkAvailable()) {
            // instead of Log we use Toast
//            Log.i(TAG, "callWebService: calling")

            // Toast can not be called from background thread
            // Toast architecture is designed to be called from foreground thread
            // But we can switch thread on the fly
            withContext(Dispatchers.Main) {
                Toast.makeText(app, "Using remote data", Toast.LENGTH_LONG).show()
            }

            val retrofit = Retrofit.Builder()
                .baseUrl(WEB_SERVICE_URL)
                    // integrate moshi lib for parsing the json and no need to create an adapter
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
            // create service
            val service = retrofit.create(MonsterService::class.java)
            // get service data
            val serviceData = service.getMonsterData().body() ?: emptyList()
//            Log.i(TAG, "callWebService: $serviceData")
            // we can't use .value or setValue(). Because they are can only be called from UI thread
            // instead we call postValue(). which is designed to be called from a background thread
            monsterData.postValue(serviceData)

/* deleted, after using sql we save data to sql instead cache
            // after posting data to LiveData we will save it to cache
            saveDataToCache(serviceData)
*/
            monsterDao.deleteAll()
            monsterDao.insertMonsters(serviceData)
        }
    }

    // used for suppressing deprecated functions can be found in intention actions
    // here we are suppressing everything inside networkAvailable()
    @Suppress("DEPRECATION")
    private fun networkAvailable(): Boolean {
        val connectivityManager = app.getSystemService(Context.CONNECTIVITY_SERVICE)
                // implicit type of object
                as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting ?: false
    }

    fun refreshDataFromWeb() {
        // Dispatchers.IO means do it in the background thread
        // Dispatchers.Main means do it in the foreground thread
        CoroutineScope(Dispatchers.IO).launch {
            callWebService()
        }
    }

    private fun saveDataToCache(monsterData: List<Monster>) {
        if (ContextCompat.checkSelfPermission(
                // context
                app,
                // Permission. Manifest from android.Manifest
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // duplicated code goes to function
            val adapter: JsonAdapter<List<Monster>> = jsonAdapter()
            // use moshi for creating jason
            val json = adapter.toJson(monsterData)
            FileHelper.saveTextToFile(app, json, "monsters.json")
        }
    }

    private fun readDataFromCache(): List<Monster> {
        val json =FileHelper.readTextFile(app, "monsters.json")
        return if (json == null) {
            emptyList()
        } else {
            // duplicated code goes to function
            val adapter: JsonAdapter<List<Monster>> = jsonAdapter()
            adapter.fromJson(json) ?: emptyList()
        }
    }

    private fun jsonAdapter(): JsonAdapter<List<Monster>> {
        val moshi = Moshi.Builder()
            // use this function when var name in Class doesn't match with jason name
            .add(KotlinJsonAdapterFactory())
            .build()
        // ParametrizedType moved to the root of class for multiple usage
        //val listType = Types.newParameterizedType(List::class.java, Monster::class.java)
        // declaration of val adapter: JsonAdapter<List<Monster>> can be omitted
        return moshi.adapter(listType)
    }

}