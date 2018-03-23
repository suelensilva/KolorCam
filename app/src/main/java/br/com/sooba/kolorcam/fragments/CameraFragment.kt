package br.com.sooba.kolorcam.fragments

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.app.ActivityCompat
import android.view.LayoutInflater
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.view.ViewGroup
import br.com.sooba.kolorcam.R
import br.com.sooba.kolorcam.views.AutoFitTextureView

/**
 * Fragment to capture color using camera2 API
 */
class CameraFragment : android.support.v4.app.Fragment(), ActivityCompat.OnRequestPermissionsResultCallback {

    lateinit var mTextureView: AutoFitTextureView

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    lateinit var mBackgroundThread : HandlerThread

    /**
     * A {@link Handler} for running tasks in the background.
     */
    lateinit var mBackgroundHandler : Handler


    private val mSurfaceTextureListener : SurfaceTextureListener = object : SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
        }

        override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
            return true
        }

        override fun onSurfaceTextureAvailable(texture: SurfaceTexture?, width: Int, height: Int) {
            openCamera(width, height)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.camera_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mTextureView = view.findViewById<AutoFitTextureView>(R.id.texture_view)
    }

    override fun onResume() {
        super.onResume()

        startBackgroundThread()

        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("CameraBackground")
        mBackgroundThread.start()
        mBackgroundHandler = Handler(mBackgroundThread.looper)
    }

    fun openCamera(width:Int, height:Int) {
        TODO("not implemented")
    }
}