package br.com.sooba.kolorcam

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_camera -> {
                message.setText(R.string.title_camera)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_history -> {
                message.setText(R.string.title_history)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_info -> {
                message.setText(R.string.title_info)
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
}
