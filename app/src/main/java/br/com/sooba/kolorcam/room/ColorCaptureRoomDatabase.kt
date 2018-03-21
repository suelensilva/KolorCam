package br.com.sooba.kolorcam.room

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.RoomDatabase.Callback
import android.content.Context
import android.os.AsyncTask

/**
 * Room database to manage captured color registered with SQLite
 */
@Database(entities = arrayOf(ColorCapture::class), version = 1, exportSchema = false)
abstract class ColorCaptureRoomDatabase : RoomDatabase() {

    abstract fun colorCaptureDao() : ColorCaptureDao

    companion object {
        private var INSTANCE : ColorCaptureRoomDatabase? = null

        var sRoomDatabaseCallback  = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)

                PopulateDbAsync(INSTANCE).execute()
            }
        }

        fun getDatabase(context : Context) : ColorCaptureRoomDatabase? {
            if(INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                        ColorCaptureRoomDatabase::class.java,
                        "color.db")
                        .fallbackToDestructiveMigration()
                        .addCallback(sRoomDatabaseCallback)
                        .build()
            }

            return INSTANCE
        }

        class PopulateDbAsync(var db : ColorCaptureRoomDatabase?) : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                val dao = db?.colorCaptureDao()
                dao?.insert(ColorCapture(4, "#FF00FF", System.currentTimeMillis()))
                dao?.insert(ColorCapture(5, "#FFFFFF", System.currentTimeMillis()))

                return null
            }

        }
    }
}