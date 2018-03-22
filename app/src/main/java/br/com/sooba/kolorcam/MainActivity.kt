package br.com.sooba.kolorcam

import android.arch.lifecycle.ViewModelProviders
import android.arch.persistence.room.RoomDatabase
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import br.com.sooba.kolorcam.fragments.CameraFragment
import br.com.sooba.kolorcam.fragments.ColorCaptureHistoryFragment
import br.com.sooba.kolorcam.room.ColorCapture
import br.com.sooba.kolorcam.viewmodel.ColorCaptureViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mColorCaptureViewModel : ColorCaptureViewModel

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_camera -> {
                showCamera()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_history -> {
                showHistory()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_info -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mColorCaptureViewModel = ViewModelProviders.of(this).get(ColorCaptureViewModel::class.java)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun showCamera() {

        val cameraFragment = CameraFragment()

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, cameraFragment)
        fragmentTransaction.commit()
    }

    private fun showHistory() {

        val historyFragment = ColorCaptureHistoryFragment()

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, historyFragment)
        fragmentTransaction.commit()
    }

}
