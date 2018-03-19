package br.com.sooba.kolorcam.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/**
 * Room database to manage captured color registered with SQLite
 */
@Database(entities = arrayOf(ColorCapture::class), version = 1, exportSchema = false)
abstract class ColorCaptureRoomDatabase : RoomDatabase() {

    abstract fun colorCaptureDao() : ColorCaptureDao

    companion object {
        private var INSTANCE : ColorCaptureRoomDatabase? = null

        fun getDatabase(context : Context) : ColorCaptureRoomDatabase? {
            if(INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                        ColorCaptureRoomDatabase::class.java,
                        "color.db")
                        .fallbackToDestructiveMigration()
                        .build()
            }

            return INSTANCE
        }
    }
}