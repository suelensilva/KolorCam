package br.com.sooba.kolorcam

import android.arch.lifecycle.ViewModelProviders
import android.arch.persistence.room.RoomDatabase
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import br.com.sooba.kolorcam.activities.AboutActivity
import br.com.sooba.kolorcam.activities.HistoryActivity
import br.com.sooba.kolorcam.fragments.CameraFragment
import br.com.sooba.kolorcam.fragments.ColorCaptureHistoryFragment
import br.com.sooba.kolorcam.room.ColorCapture
import br.com.sooba.kolorcam.viewmodel.ColorCaptureViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mColorCaptureViewModel : ColorCaptureViewModel

    lateinit var mCameraFragment : CameraFragment

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
//            R.id.navigation_camera -> {
//                showCamera()
//                return@OnNavigationItemSelectedListener true
//            }
            R.id.navigation_history -> {
//                showHistory()
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

        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        showCamera()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.navigation, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId

        when(id) {
            R.id.navigation_history -> {
                startActivity(Intent(this, HistoryActivity::class.java))
                return true
            }
            R.id.navigation_info -> {
                startActivity(Intent(this, AboutActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun switchFlash(view: View) {
        val newFlashState = mCameraFragment.changeFlashStatus()
        val flashIcon : ImageView = view as ImageView

        if(newFlashState) {
            flashIcon.setImageResource(R.drawable.ic_flash_off_purple_24dp)
        } else {
            flashIcon.setImageResource(R.drawable.ic_flash_on_purple_24dp)
        }
    }

    private fun showCamera() {

        mCameraFragment = CameraFragment()

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, mCameraFragment)
        fragmentTransaction.commit()
    }

}
