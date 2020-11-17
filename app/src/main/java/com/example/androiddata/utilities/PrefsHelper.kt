package com.example.androiddata.utilities

import android.content.Context
import android.content.SharedPreferences

// "item_type_key" = can be anything you like
const val ITEM_TYPE_KEY = "item_type_key"

// manages preferences
class PrefsHelper {

    companion object {
        // gets access to preferences xml file
        private fun preferences(context: Context): SharedPreferences =
            // default = name of file we want to create, 0 = this is available anywhere
            context.getSharedPreferences("default", 0)

        fun setItemType(context: Context, type: String) {
            // gets access to preferences xml file and edit it
            preferences(context).edit()
                .putString(ITEM_TYPE_KEY, type)
                    // saves the data persistently to the xml file
                .apply()
        }

        fun getItemType(context: Context): String =
            // because we are providing a default value "List" we can simply add "!!"
            // as an assertion taht says this will definitely not be null
            preferences(context).getString(ITEM_TYPE_KEY, "List")!!
    }

}