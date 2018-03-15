package br.com.sooba.kolorcam.room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Entity class for a captured color
 */
@Entity(tableName = "color_capture")
data class ColorCapture (
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "color_hexa") var colorHexa: String,
    @ColumnInfo(name = "timestamp") var time: Long
) {
    constructor():this(null, "", 0)
}
