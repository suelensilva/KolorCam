package br.com.sooba.kolorcam.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mColorCaptureViewModel = ViewModelProviders.of(activity!!).get(ColorCaptureViewModel::class.java)

        val recyclerView = view.findViewById<RecyclerView>(R.id.history_recycler_view)

        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView!!.layoutManager = layoutManager

        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, layoutManager.orientation));

        val adapter = ColorCaptureHistoryAdapter(context!!)
        adapter.colorCaptures = ArrayList()
        recyclerView.adapter = adapter

        recyclerView.visibility = View.VISIBLE

        mColorCaptureViewModel.getAllColors().observe(activity!!, object : Observer<List<ColorCapture>> {
            override fun onChanged(t: List<ColorCapture>?) {

                if(t!!.isNotEmpty()) {
                    val textView = view.findViewById<TextView>(R.id.no_colors_msg)
                    textView?.visibility = View.GONE

                    adapter.colorCaptures = t
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }
}