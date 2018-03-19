package br.com.sooba.kolorcam.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.sooba.kolorcam.R

/**
 * Fragment that shows the list of all captured colors
 * made with this app
 */
class ColorCaptureHistoryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.history_layout, container, false)
    }
}