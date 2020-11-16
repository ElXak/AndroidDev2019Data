package com.example.androiddata.utilities

import android.app.Application
import android.content.Context
import java.io.File

class FileHelper {
    // companion object properties and methods are called directly from class definition not from instance of the class
    companion object {
        // Keeping data in resources keeps you out of missing file errors & good for localization for multi lang applications
        fun getTextFromResources(context: Context, resourceId: Int): String {
            return context.resources.openRawResource(resourceId).use {
                it.bufferedReader().use {
                    it.readText()
                }
            }
        }

        // Assets gives some flexibility. You can build files dynamically
        fun getTextFromAssets(context: Context, fileName: String): String {
            return context.assets.open(fileName).use {
                it.bufferedReader().use {
                    it.readText()
                }
            }
        }

        // saves file to cache directory
        fun saveTextToFile(app: Application, json: String?, fileName: String) {
            // create file object by passing two arguments, location and file name
            // instead of getExternalFilesDir() can be used cacheDir or fileDir.
            // if you don't want user to clean it like cache
            // cache can be cleaned by operating system if device runs low in storage
            // "monster" is sub directory in applications external storage directory
            val file = File(app.getExternalFilesDir("monsters"), fileName)
            // writes text to file with desired Charset
            file.writeText(json ?: "", Charsets.UTF_8)
        }

        // reads file from cache directory
        fun readTextFile(app: Application, fileName: String): String? {
            val file = File(app.getExternalFilesDir("monsters"), fileName)
            // check if file exists return text from it else retutn null
            return if (file.exists()) {
                file.readText()
            } else null
        }
    }
}