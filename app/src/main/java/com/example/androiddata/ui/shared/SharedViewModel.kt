package com.example.androiddata.ui.shared

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.androiddata.data.Monster
import com.example.androiddata.data.MonsterRepository

// Asks repository for data without knowing from where it comes from and passes to User Interface
class SharedViewModel(app: Application) : AndroidViewModel(app) {

//    moved to Repository Class because view model doesn't need to know about data source
//    private val listType = Types.newParameterizedType(
//        List::class.java, Monster::class.java
//    )

//    init {
//        getting text from Resources
//        val text = FileHelper.getTextFromResources(app, R.raw.monster_data)
//        getting text from Assets
//        val text = FileHelper.getTextFromAssets(app, "monster_data.json")
//        Log.i(TAG, text)
//        parseText(text)
//        after adding Repository Class: And deleted after LiveData pattern
//        val monsterData = dataRepository.getMonsterData(app)
//        for (monster in monsterData) {
//          Log.i(TAG, "${monster.monsterName} (\$${monster.price})")
//        }
//    }

//    fun parseText(text: String) {
//        val moshi = Moshi.Builder().build()
//        val adapter: JsonAdapter<List<Monster>> = moshi.adapter(listType)
//        val monsterData = adapter.fromJson(text)
//
//        for (monster in monsterData ?: emptyList()) {
//          Log.i(TAG, "${monster.monsterName} (\$${monster.price})")
//        }
//    }

    private val dataRepository = MonsterRepository(app)
    val monsterData = dataRepository.monsterData

    // for passing data to layout
    val selectedMonster = MutableLiveData<Monster>()

    fun refreshData() {
        dataRepository.refreshDataFromWeb()
    }

}
