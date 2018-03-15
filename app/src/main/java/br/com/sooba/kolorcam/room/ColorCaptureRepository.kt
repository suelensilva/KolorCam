package br.com.sooba.kolorcam.room

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask

/**
 * Repository
 */
class ColorCaptureRepository(application: Application) {

    private var colorCaptureDao : ColorCaptureDao
    private var mAllCapturedColors : LiveData<List<ColorCapture>>

    init {
        val db = ColorCaptureRoomDatabase.getDatabase(application.applicationContext)
        colorCaptureDao = db!!.colorCaptureDao()
        mAllCapturedColors = colorCaptureDao.getAllColors()
    }

    fun getAllCapturedColors() : LiveData<List<ColorCapture>> {
        return mAllCapturedColors
    }

    fun insert(capturedColor : ColorCapture) {
        InsertAsyncTask(colorCaptureDao).execute(capturedColor)
    }

    companion object {
        private class InsertAsyncTask() : AsyncTask<ColorCapture, Void, Void>() {

            private lateinit var mColorCaptureDao : ColorCaptureDao

            constructor(colorCaptureDao: ColorCaptureDao) : this() {
                mColorCaptureDao = colorCaptureDao
            }

            override fun doInBackground(vararg p0: ColorCapture?): Void? {
                mColorCaptureDao.insert(p0[0])
                return null
            }

        }
    }
}