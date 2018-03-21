package br.com.sooba.kolorcam.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import br.com.sooba.kolorcam.R
import br.com.sooba.kolorcam.adapters.ColorCaptureHistoryAdapter
import br.com.sooba.kolorcam.room.ColorCapture
import br.com.sooba.kolorcam.viewmodel.ColorCaptureViewModel

class ColorCaptureHistoryActivity : AppCompatActivity() {

    private lateinit var mColorCaptureViewModel : ColorCaptureViewModel

    private var mColorCaptureList = ArrayList<ColorCapture>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mColorCaptureViewModel = ViewModelProviders.of(this).get(ColorCaptureViewModel::class.java)

        setContentView(R.layout.history_layout)

        val textView = findViewById<TextView>(R.id.no_colors_msg)
        textView?.visibility = View.GONE

        val recyclerView = findViewById<RecyclerView>(R.id.history_recycler_view)

        val colorCaptureAdapter = ColorCaptureHistoryAdapter(mColorCaptureList, this)

        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        recyclerView.visibility = View.VISIBLE

        mColorCaptureViewModel.getAllColors().observe(this, object : Observer<List<ColorCapture>> {
            override fun onChanged(t: List<ColorCapture>?) {

                if(t!!.size > 0) {
                    mColorCaptureList.clear()

                    for (i in 0..t.size-1) {
                        mColorCaptureList.add(t.get(i))
                    }
                    //colorCaptureAdapter.colorCaptures = t!!
                    colorCaptureAdapter.notifyDataSetChanged()
                }
            }

        })
    }
}