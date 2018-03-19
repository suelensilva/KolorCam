package br.com.sooba.kolorcam.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

/**
 * Color capture DAO
 */
@Dao
interface ColorCaptureDao {

    @Insert
    fun insert(color: ColorCapture?):Long

    //@Query("DELETE FROM color_capture")
    //fun delete(id: Int)

    @Query("SELECT * FROM color_capture ORDER BY timestamp DESC")
    fun getAllColors(): LiveData<List<ColorCapture>>
}