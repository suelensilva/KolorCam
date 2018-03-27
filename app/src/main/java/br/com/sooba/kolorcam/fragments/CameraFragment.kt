package br.com.sooba.kolorcam.fragments

import android.Manifest

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import android.hardware.camera2.*
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import br.com.sooba.kolorcam.R
import br.com.sooba.kolorcam.views.AutoFitTextureView
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import kotlin.Comparator
import kotlin.collections.ArrayList

private const val REQUEST_CAMERA_PERMISSION = 20
private const val TAG = "CameraFragment"

private const val MAX_PREVIEW_WIDTH = 1920
private const val MAX_PREVIEW_HEIGHT = 1080

private var ORIENTATIONS : SparseIntArray = SparseIntArray()

/**
 * Fragment to capture color using camera2 API
 */
class CameraFragment : android.support.v4.app.Fragment(), ActivityCompat.OnRequestPermissionsResultCallback {

    init {
        ORIENTATIONS.append(Surface.ROTATION_0, 90)
        ORIENTATIONS.append(Surface.ROTATION_90, 0)
        ORIENTATIONS.append(Surface.ROTATION_180, 270)
        ORIENTATIONS.append(Surface.ROTATION_270, 180)
    }

    private lateinit var mTextureView: AutoFitTextureView

    private lateinit var mPreviewSize : Size

    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    val mCameraOpenCloseLock = Semaphore(1)

    /**
     * ID of the current {@link CameraDevice}.
     */
    private lateinit var mCameraId : String

    /**
     * A reference to the opened {@link CameraDevice}.
     */
    var mCameraDevice : CameraDevice? = null

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private var mBackgroundThread : HandlerThread? = null

    /**
     * A {@link Handler} for running tasks in the background.
     */
    private var mBackgroundHandler : Handler? = null

    /**
     * {@link CaptureRequest.Builder} for the camera preview
     */
    lateinit var mPreviewRequestBuilder : CaptureRequest.Builder

    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    var mCaptureSession : CameraCaptureSession? = null

    /**
     * {@link CaptureRequest} generated by {@link #mPreviewRequestBuilder}
     */
    lateinit var mPreviewRequest : CaptureRequest

    /**
     * Flag that indicates if flash is on or off
     */
    private var mIsFlashOn = false

    /**
     * An {@link ImageReader} that handles still image capture.
     */
    private var mImageReader : ImageReader? = null

    /**
     * Whether the current camera device supports Flash or not.
     */
    private var mFlashSupported : Boolean = false

    /**
     * Orientation of camera sensor
     */
    private var mSensorOrientation : Int = Surface.ROTATION_0

    private val mSurfaceTextureListener : SurfaceTextureListener = object : SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture?, width: Int, height: Int) {
            configureTransform(width, height)
        }

        override fun onSurfaceTextureUpdated(texture: SurfaceTexture?) {
        }

        override fun onSurfaceTextureDestroyed(texture: SurfaceTexture?): Boolean {
            return true
        }

        override fun onSurfaceTextureAvailable(texture: SurfaceTexture?, width: Int, height: Int) {
            openCamera(width, height)
        }
    }

    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */
    private val mStateCallback : CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice: CameraDevice?) {
            mCameraOpenCloseLock.release()
            mCameraDevice = cameraDevice
            createCameraPreviewSession()
        }

        override fun onDisconnected(cameraDevice: CameraDevice?) {
            mCameraOpenCloseLock.release()
            cameraDevice?.close()
            mCameraDevice = null
        }

        override fun onError(cameraDevice: CameraDevice?, error: Int) {
            mCameraOpenCloseLock.release()
            mCameraDevice?.close()
            mCameraDevice = null
            activity!!.finish()
        }

    }

    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to bitmap captured
     * from preview.
     */
    private val mCaptureCallback : CameraCaptureSession.CaptureCallback = object : CameraCaptureSession.CaptureCallback() {

        override fun onCaptureProgressed(session: CameraCaptureSession?,
                                         request: CaptureRequest?,
                                         partialResult: CaptureResult?) {
        }

        override fun onCaptureCompleted(session: CameraCaptureSession?,
                                        request: CaptureRequest?,
                                        result: TotalCaptureResult?) {
            processBitmap()
        }

        private fun processBitmap() {
            //TODO ("Process bitmap here")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.camera_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mTextureView = view.findViewById(R.id.texture_view)
    }

    override fun onResume() {
        super.onResume()

        startBackgroundThread()

        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (mTextureView.isAvailable) {
            openCamera(mTextureView.width, mTextureView.height)
        } else {
            mTextureView.surfaceTextureListener = mSurfaceTextureListener
        }
    }

    override fun onPause() {
        closeCamera()
        stopBackgroundThread()
        super.onPause()
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("CameraBackground")
        mBackgroundThread?.start()
        mBackgroundHandler = Handler(mBackgroundThread?.looper)
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private fun stopBackgroundThread() {
        mBackgroundThread?.quitSafely()

        try {
            mBackgroundThread?.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch(e:InterruptedException) {
            Log.e(TAG, "InterruptedException while stopping background thread", e)
        }
    }

    fun openCamera(width:Int, height:Int) {
        val activity = activity as Activity

        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
            return
        }

        setUpCameraOutputs(width, height)
        configureTransform(width, height)

        val manager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        try {
            if(!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening")
            }
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler)
        } catch (e : CameraAccessException) {
            Log.e(TAG, "CameraAccessException while trying to open camera", e)
        }
    }

    /**
     * Closes the current {@link CameraDevice}.
     */
    private fun closeCamera() {
        try {
            mCameraOpenCloseLock.acquire()

            mCaptureSession?.close()
            mCaptureSession = null

            mCameraDevice?.close()
            mCameraDevice = null

            mImageReader?.close()
            mImageReader = null

        } catch (e:InterruptedException) {
            Log.e(TAG, "InterruptedException while trying to close camera", e)
        } finally {
            mCameraOpenCloseLock.release()
        }
    }

    /**
     * Sets up member variables related to camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    private fun setUpCameraOutputs(width: Int, height: Int) {
        val activity = activity

        val manager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        try {
            for(cameraId : String in manager.cameraIdList) {

                val characteristics : CameraCharacteristics = manager.getCameraCharacteristics(cameraId)

                // Only use back camera
                val facing : Int = characteristics.get(CameraCharacteristics.LENS_FACING)
                if(facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue
                }

                val map : StreamConfigurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

                // For still image captures, we use the largest available size.
                val largest : Size = map.getOutputSizes(ImageFormat.JPEG).maxWith(CompareSizesByArea())!!
                mImageReader = ImageReader.newInstance(largest.width, largest.height, ImageFormat.JPEG,2)

                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                val displayRotation = activity.windowManager.defaultDisplay.rotation

                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)

                var swappedDimensions = false
                when(displayRotation) {
                    Surface.ROTATION_0,
                    Surface.ROTATION_180 -> {
                        if(mSensorOrientation == Surface.ROTATION_90 || mSensorOrientation == Surface.ROTATION_270) {
                            swappedDimensions = true
                        }
                    }
                    Surface.ROTATION_90,
                    Surface.ROTATION_270 -> {
                        if(mSensorOrientation == Surface.ROTATION_0 || mSensorOrientation == Surface.ROTATION_180) {
                            swappedDimensions = true
                        }
                    }
                }

                val displaySize = Point()
                activity.windowManager.defaultDisplay.getSize(displaySize)

                var rotatedPreviewWidth = width
                var rotatedPreviewHeight = height
                var maxPreviewWidth = displaySize.x
                var maxPreviewHeight = displaySize.y

                if(swappedDimensions) {
                    rotatedPreviewWidth = height
                    rotatedPreviewHeight = width
                    maxPreviewWidth = displaySize.y
                    maxPreviewHeight = displaySize.x
                }

                if(maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH
                }

                if(maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT
                }

                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture::class.java),
                        rotatedPreviewWidth, rotatedPreviewHeight,
                        maxPreviewWidth, maxPreviewHeight,
                        largest)!!

                // We fit the aspect ratio of TextureView to the size of preview we picked.
                val orientation = resources.configuration.orientation

                if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mTextureView.setAspectRation(mPreviewSize.width, mPreviewSize.height)
                } else {
                    mTextureView.setAspectRation(mPreviewSize.height, mPreviewSize.width)
                }

                // Check if the flash is supported.
                val available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
                mFlashSupported = available

                mCameraId = cameraId

                return
            }
        } catch (e:CameraAccessException) {
            Log.e(TAG, "CameraAccessException while setting up camera outputs", e)
        }
    }

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */

    fun configureTransform(viewWidth:Int, viewHeight:Int) {
        val activity = activity ?: return

        val rotation : Int = activity.windowManager.defaultDisplay.rotation
        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = RectF(0f, 0f, mPreviewSize.height.toFloat(), mPreviewSize.width.toFloat())
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()

        if(Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)

            val scale = Math.max(
                    viewHeight.toFloat() / mPreviewSize.height.toFloat(),
                    viewWidth.toFloat() / mPreviewSize.width.toFloat()
            )
            matrix.postScale(scale, scale, centerX, centerY)
            matrix.postRotate(90f * (rotation - 2), centerX, centerY)
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180f, centerX, centerY)
        }

        mTextureView.setTransform(matrix)
    }

    private fun requestCameraPermission() {
        val permissions = Array(1,  init = {
            Manifest.permission.CAMERA
        })
        requestPermissions(permissions, REQUEST_CAMERA_PERMISSION)
    }

    /**
     * Creates a new {@link CameraCaptureSession} for camera preview.
     */
    fun createCameraPreviewSession() {
        try {
            val texture = mTextureView.surfaceTexture

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.width, mPreviewSize.height)

            // This is the output Surface we need to start preview.
            val surface = Surface(texture)

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)!!
            mPreviewRequestBuilder.addTarget(surface)

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice!!.createCaptureSession(
                    Arrays.asList(surface, mImageReader?.surface),
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession?) {
                            Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
                        }

                        override fun onConfigured(cameraCaptureSession: CameraCaptureSession?) {
                            mCaptureSession = cameraCaptureSession

                            try {
                                // Auto focus should be continuous for camera preview.
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)

                                // Finally, we start displaying the camera preview.
                                mPreviewRequest = mPreviewRequestBuilder.build()

                                mCaptureSession?.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback, mBackgroundHandler)
                            } catch (e: CameraAccessException) {
                                Log.e(TAG, "CameraAccessException while creating camera preview session", e)
                            }
                        }
                    },
                    null)
        } catch(e : CameraAccessException) {
            Log.e(TAG, "CameraAccessException while creating camera preview session", e)
        }
    }

    fun changeFlashStatus():Boolean {
        if(mFlashSupported) {
            mCaptureSession!!.stopRepeating()
            mIsFlashOn = if(mIsFlashOn) {
                mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF)
                mCaptureSession!!.setRepeatingRequest(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler)

                false
            } else {
                mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH)
                mCaptureSession!!.setRepeatingRequest(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler)

                true
            }
        }

        return mIsFlashOn
    }

    companion object {
        open class CompareSizesByArea : Comparator<Size> {
            override fun compare(firstSize: Size?, secondSize: Size?): Int {
                return firstSize?.width?.times(firstSize.height)?.minus(
                        secondSize?.width?.times(secondSize.height)!!)!!
            }
        }

        /**
         * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that
         * is at least as large as the respective texture view size, and that is at most as large as the
         * respective max size, and whose aspect ratio matches with the specified value. If such size
         * doesn't exist, choose the largest one that is at most as large as the respective max size,
         * and whose aspect ratio matches with the specified value.
         *
         * @param choices           The list of sizes that the camera supports for the intended output
         *                          class
         * @param textureViewWidth  The width of the texture view relative to sensor coordinate
         * @param textureViewHeight The height of the texture view relative to sensor coordinate
         * @param maxWidth          The maximum width that can be chosen
         * @param maxHeight         The maximum height that can be chosen
         * @param aspectRatio       The aspect ratio
         * @return The optimal {@code Size}, or an arbitrary one if none were big enough
         */
        fun chooseOptimalSize(choices:Array<Size>,
                              textureViewWidth:Int,
                              textureViewHeight:Int,
                              maxWidth:Int,
                              maxHeight:Int,
                              aspectRatio:Size) : Size? {

            // Collect the supported resolutions that are at least as big as the preview Surface
            val bigEnough = ArrayList<Size>()

            // Collect the supported resolutions that are smaller than the preview Surface
            val notBigEnough = ArrayList<Size>()

            val w = aspectRatio.width
            val h = aspectRatio.height

            for(option : Size in choices) {
                if(option.width <= maxWidth && option.height <= maxHeight &&
                        option.height == option.width * h / w) {

                    if(option.width >= textureViewWidth && option.height >= textureViewHeight) {
                        bigEnough.add(option)
                    } else {
                        notBigEnough.add(option)
                    }
                }
            }

            // Pick the smallest of those big enough. If there is no one big enough, pick the
            // largest of those not big enough.
            return when {
                bigEnough.size > 0 -> bigEnough.maxWith(CompareSizesByArea())
                notBigEnough.size > 0 -> notBigEnough.maxWith(CompareSizesByArea())
                else -> {
                    Log.e(CameraFragment::class.simpleName, "Couldn't find any suitable preview size")
                    choices[0]
                }
            }
        }
    }
}