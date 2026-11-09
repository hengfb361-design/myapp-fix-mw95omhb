package com.example.myapp
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 100, 50, 50)
        }

        val btn = Button(this).apply {
            text = "Start 8 Ball Pool Mod Menu"
            setOnClickListener {
                if (checkOverlayPermission()) {
                    startService(Intent(this@MainActivity, PoolOverlayService::class.java))
                    Toast.makeText(this@MainActivity, "Mod Menu Overlay Started!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        layout.addView(btn)
        setContentView(layout)
    }

    private fun checkOverlayPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
            return false
        }
        return true
    }
}
