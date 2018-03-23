package br.com.sooba.kolorcam.views

import android.content.Context
import android.util.AttributeSet
import android.view.TextureView

class AutoFitTextureView : TextureView {

    var mRatioWidth : Int = 0
    var mRatioHeight : Int = 0

    constructor(context : Context) : super(context, null) {
    }

    constructor(context:Context, attrs:AttributeSet) : super(context, attrs, 0){
    }

    constructor(context: Context, attrs: AttributeSet, defStyle:Int) :
            super(context, attrs, defStyle) {
    }

    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated from the parameters. Note that the actual sizes of parameters don't matter, that
     * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
     *
     * @param width  Relative horizontal size
     * @param height Relative vertical size
     */
    fun setAspectRation(width:Int, height:Int) {
        if(width < 0 || height < 0) {
            throw IllegalArgumentException("Size cannot be negative")
        }

        mRatioWidth = width
        mRatioHeight = height
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        if(0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height)
        } else {

            if(width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight/mRatioWidth)
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height)
            }
        }
    }
}