package com.example.myapp
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager

class PoolOverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: GuidelineCanvasView

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        overlayView = GuidelineCanvasView(this)
        windowManager.addView(overlayView, params)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        if (::overlayView.isInitialized) {
            windowManager.removeView(overlayView)
        }
    }
}

class GuidelineCanvasView(context: Context) : View(context) {
    private val whitePaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 6f
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    private val orangePaint = Paint().apply {
        color = Color.rgb(255, 140, 0)
        strokeWidth = 5f
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    private val purplePaint = Paint().apply {
        color = Color.rgb(186, 85, 211)
        strokeWidth = 4f
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    init {
        // Load native library for C++ OpenCV calculation hook
        System.loadLibrary("pool-engine")
    }

    private external fun getLineCoordinates(): FloatArray

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Fetch real-time coordinates from C++ Native / OpenCV engine
        try {
            val coords = getLineCoordinates()
            if (coords.size >= 8) {
                canvas.drawLine(coords[0], coords[1], coords[2], coords[3], whitePaint)
                canvas.drawLine(coords[2], coords[3], coords[4], coords[5], orangePaint)
                canvas.drawLine(coords[0], coords[1], coords[6], coords[7], purplePaint)
                return
            }
        } catch (e: Exception) {
            // Fallback default extended lines if native bridge is pending
        }

        // Default mockup guidelines matching target visual
        canvas.drawLine(300f, 400f, 900f, 200f, whitePaint)
        canvas.drawLine(500f, 300f, 350f, 900f, orangePaint)
        canvas.drawLine(300f, 400f, 150f, 250f, purplePaint)
        
        invalidate() // Loop redraw for 60 FPS animation frame
    }
}
