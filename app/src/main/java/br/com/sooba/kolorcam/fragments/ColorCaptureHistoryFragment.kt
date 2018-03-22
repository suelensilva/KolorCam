package br.com.sooba.kolorcam.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.sooba.kolorcam.R
import br.com.sooba.kolorcam.adapters.ColorCaptureHistoryAdapter
import br.com.sooba.kolorcam.room.ColorCapture
import br.com.sooba.kolorcam.viewmodel.ColorCaptureViewModel

/**
 * Fragment that shows the list of all captured colors
 * made with this app
 */
class ColorCaptureHistoryFragment : android.support.v4.app.Fragment() {

    private lateinit var mColorCaptureViewModel : ColorCaptureViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.history_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mColorCaptureViewModel = ViewModelProviders.of(activity!!).get(ColorCaptureViewModel::class.java)

        val textView = view.findViewById<TextView>(R.id.no_colors_msg)
        textView?.visibility = View.GONE

        val recyclerView = view.findViewById<RecyclerView>(R.id.history_recycler_view)

        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView!!.layoutManager = layoutManager

        val adapter = ColorCaptureHistoryAdapter(context!!)
        adapter.colorCaptures = ArrayList<ColorCapture>()
        recyclerView.adapter = adapter

        recyclerView.visibility = View.VISIBLE

        mColorCaptureViewModel.getAllColors().observe(activity!!, object : Observer<List<ColorCapture>> {
            override fun onChanged(t: List<ColorCapture>?) {
                adapter.colorCaptures = t!!
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun colors(): List<ColorCapture> {
        return listOf(
                ColorCapture(1, "#FF0000",
                        System.currentTimeMillis()),
                ColorCapture(2, "#00FF00",
                        System.currentTimeMillis()),
                ColorCapture(3, "#0000FF",
                        System.currentTimeMillis())
        )
    }
}