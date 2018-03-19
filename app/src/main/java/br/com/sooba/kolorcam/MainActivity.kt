package br.com.sooba.kolorcam

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import br.com.sooba.kolorcam.fragments.ColorCaptureHistoryFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_camera -> {
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

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun showHistory() {
        val historyFragment = ColorCaptureHistoryFragment()

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, historyFragment)
        fragmentTransaction.commit()
    }
}
