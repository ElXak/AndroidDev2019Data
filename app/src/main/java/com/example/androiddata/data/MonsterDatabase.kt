package com.example.androiddata.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Defining database class that extends class RoomDatabase
// with abstract that means you can't instantiate the class directly in your code
// instead you will need instance of a subclass and that will be handled by Room Architecture

// entities is a list of entity classes.
// If there more then 1 entity class put them with comma delimiter
// version of database is Int and starts with 1, but if you change the database structure later
// you upgrade the version of database and you have to add some code to handle database upgrade
// exportSchema = false means don't generate files to document the database
@Database(entities = [Monster::class], version = 1, exportSchema = false)
abstract class MonsterDatabase: RoomDatabase() {

    // for each DAO add an abstract fun. Name the fun descriptively
    // and have it return of your DAO instance
    // since MonsterDao is instance of interface not superclass don't include "()"
    abstract fun monsterDao(): MonsterDao

    // fun to get the instance of this class
    companion object {
        // Volatile means this obj can be accessed by more then 1 thread at the time
        @Volatile
        private var INSTANCE: MonsterDatabase? = null

        fun getDatabse(context: Context): MonsterDatabase {
            // if INSTANCE == null create an instance
            if (INSTANCE == null) {
                // synchronized means this block can be called by 1 thread at a time
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        // applicationContext to make sure not an activity ontext
                        context.applicationContext,
                        MonsterDatabase::class.java,
                        // monsters.db is name of file in system storage
                        "monsters.db"
                    ).build()
                }
            }
            // !! means if it is null crush the application
            return INSTANCE!!
        }
    }
}