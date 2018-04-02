package br.com.sooba.kolorcam.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.ImageView

/**
 * Visual representation of a captured color
 */
class ColorImageView : ImageView {

    var mColor : Int = Color.WHITE

    constructor(context: Context) :
            super(context) {
    }

    constructor(context:Context, attributeSet: AttributeSet) :
            super(context, attributeSet) {
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int):
            super(context, attributeSet, defStyleAttr) {
    }

    fun setColor(color: Int) {
        mColor = color

        val imageBackground = background as GradientDrawable
        imageBackground.setColor(color)

        invalidate()
        requestLayout()
    }
}