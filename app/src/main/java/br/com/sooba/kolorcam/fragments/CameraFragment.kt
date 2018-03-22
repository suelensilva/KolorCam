package br.com.sooba.kolorcam.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.sooba.kolorcam.R

/**
 * Fragment to capture color using camera2 API
 */
class CameraFragment : android.support.v4.app.Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.camera_layout, container, false)
    }
}