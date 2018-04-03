package br.com.sooba.kolorcam.adapters

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.sooba.kolorcam.R
import br.com.sooba.kolorcam.adapters.ColorCaptureHistoryAdapter.ColorCaptureViewHolder
import br.com.sooba.kolorcam.room.ColorCapture
import br.com.sooba.kolorcam.views.ColorImageView

class ColorCaptureHistoryAdapter(private val context : Context) : RecyclerView.Adapter<ColorCaptureViewHolder>() {

    lateinit var colorCaptures : List<ColorCapture>

    constructor(colorCaptures : List<ColorCapture>,
                context : Context) : this(context) {
        this.colorCaptures = colorCaptures
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorCaptureViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.color_capture_item, parent, false)
        return ColorCaptureViewHolder(view)
    }

    override fun getItemCount(): Int {
        return colorCaptures.size
    }

    override fun onBindViewHolder(holder: ColorCaptureViewHolder, position: Int) {
        val colorInt = colorCaptures[position].colorRgb
        holder.colorHexaTextView.text = "#"+Integer.toHexString(colorInt);
        holder.colorImageView.setColor(colorInt)
    }

    class ColorCaptureViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var colorHexaTextView : TextView = itemView.findViewById(R.id.color_hexa)
        var colorImageView : ColorImageView = itemView.findViewById(R.id.color_image_view)
    }
}

