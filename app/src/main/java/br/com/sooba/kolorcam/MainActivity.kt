package br.com.sooba.kolorcam

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import br.com.sooba.kolorcam.activities.AboutActivity
import br.com.sooba.kolorcam.activities.HistoryActivity
import br.com.sooba.kolorcam.fragments.CameraFragment
import br.com.sooba.kolorcam.viewmodel.ColorCaptureViewModel

class MainActivity : AppCompatActivity(), CameraFragment.OnColorChangedListener {

    private lateinit var mColorCaptureViewModel : ColorCaptureViewModel

    private lateinit var mCameraFragment : CameraFragment

    private var mCurrentColor : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mColorCaptureViewModel = ViewModelProviders.of(this).get(ColorCaptureViewModel::class.java)

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

    fun selectColor(view: View) {
        val targetImageView = findViewById<ImageView>(R.id.selected_color_image_view)
        val targetBackground = targetImageView.background

        if(targetBackground is ShapeDrawable) {
            targetBackground.paint.color = this.mCurrentColor!!
        } else if (targetBackground is GradientDrawable) {
            targetBackground.setColor(this.mCurrentColor!!)
        } else if (targetBackground is ColorDrawable) {
            targetBackground.color = this.mCurrentColor!!
        }
    }

    override fun onColorChanged(newColor: Int) {

        mCurrentColor = newColor

        runOnUiThread({
            val targetImageView = findViewById<ImageView>(R.id.inner_target_image_view)
            val targetBackground = targetImageView.background

            if(targetBackground is ShapeDrawable) {
                targetBackground.paint.color = newColor
            } else if (targetBackground is GradientDrawable) {
                targetBackground.setColor(newColor)
            } else if (targetBackground is ColorDrawable) {
                targetBackground.color = newColor
            }
        })

        //val width = Math.round(2*(resources.displayMetrics.xdpi/ DisplayMetrics.DENSITY_DEFAULT));
        //targetBackground.setStroke(width, newColor)

    }

}
