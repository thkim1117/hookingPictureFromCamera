package com.ttam.camera;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/**
 * @author thkim.
 * Created On 2025-05-07.
 * Description:
 */
public class ForegroundImageService extends Service {

    private static final String CHANNEL_ID = "ImageMonitorChannel";
    private ImageObserver imageObserver;

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundService();

        imageObserver = new ImageObserver(this, new ImageObserver.OnImageDetectedListener() {
            @Override
            public void onImageDetected(Uri imageUri) {
                Log.d("ForegroundService", "Image URI: " + imageUri.toString());
                // TODO: 자동 업로드, 분석 등 원하는 작업 수행

                Intent broadcastIntent = new Intent("com.example.NEW_IMAGE");
                broadcastIntent.putExtra("image_uri", imageUri.toString());
                sendBroadcast(broadcastIntent);
            }
        });

        getContentResolver().registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true,
                imageObserver
        );
    }

    private void startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Image Monitor Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("이미지 감지 중")
                .setContentText("카메라 촬영 이미지를 감시하고 있습니다.")
                .setSmallIcon(android.R.drawable.ic_menu_camera)
                .build();

        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        Log.d("ForegroundService", "onDestroy called");
        stopForeground(true);

        getContentResolver().unregisterContentObserver(imageObserver);


        super.onDestroy();

        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}