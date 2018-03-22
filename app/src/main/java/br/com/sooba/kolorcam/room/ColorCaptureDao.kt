package br.com.sooba.kolorcam.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

/**
 * Color capture DAO
 */
@Dao
interface ColorCaptureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(color: ColorCapture?):Long

    //@Query("DELETE FROM color_capture")
    //fun delete(id: Int)

    @Query("SELECT * FROM color_capture ORDER BY timestamp DESC")
    fun getAllColors(): LiveData<List<ColorCapture>>

    @Query("SELECT * FROM color_capture WHERE id = (SELECT MAX(id) from color_capture)")
    fun getLastColor() : ColorCapture
}