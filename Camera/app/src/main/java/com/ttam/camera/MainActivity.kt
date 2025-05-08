package com.ttam.camera

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.ttam.camera.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding 초기화
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // BroadcastReceiver 등록
        val filter = IntentFilter("com.example.NEW_IMAGE")
        registerReceiver(imageReceiver, filter)

        binding.btnStop.setOnClickListener { onDestroy() }
        binding.btnStart.setOnClickListener {
            // 포그라운드 서비스 시작
            val serviceIntent = Intent(this, ForegroundImageService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
        }

        onBackPressedDispatcher.addCallback(this) {
            onDestroy()
        }

    }

    // 이미지 URI를 받을 BroadcastReceiver 설정
    val imageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if ("com.example.NEW_IMAGE" == intent.action) {
                val uriString = intent.getStringExtra("image_uri")
                if (uriString != null) {
                    val imageUri = Uri.parse(uriString)
                    Log.d("thkim", "Received image URI: $imageUri")

                    // Glide를 사용하여 이미지 로드
                    Glide.with(this@MainActivity)
                        .load(imageUri)
                        .into(binding.imageView)  // binding 사용
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        Log.d("ForegroundService", "onDestroy()");
        val serviceIntent = Intent(this, ForegroundImageService::class.java)
        stopService(serviceIntent)
        unregisterReceiver(imageReceiver)


        super.onDestroy()
    }
//    override fun onPause() {
//        super.onPause()
//        unregisterReceiver(imageReceiver)
//    }
}