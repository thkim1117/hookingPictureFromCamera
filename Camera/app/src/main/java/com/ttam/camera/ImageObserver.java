package com.ttam.camera;

import android.content.ContentUris;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

/**
 * @author thkim.
 * Created On 2025-05-07.
 * Description:
 */
public class ImageObserver extends ContentObserver {

    private final Context context;

    public interface OnImageDetectedListener {
        void onImageDetected(Uri imageUri);
    }

    private final OnImageDetectedListener listener;

    public ImageObserver(Context context, OnImageDetectedListener listener) {
        super(new Handler(Looper.getMainLooper()));
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Uri latestImage = getLatestImageUri(context);
        if (latestImage != null) {
            Log.d("ImageObserver", "New image detected: " + latestImage);
            listener.onImageDetected(latestImage);
        }
    }

    private Uri getLatestImageUri(Context context) {
        Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME
        };

        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        try (Cursor cursor = context.getContentResolver().query(
                collection,
                projection,
                null,
                null,
                sortOrder)) {
            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                return ContentUris.withAppendedId(collection, id);
            }
        }
        return null;
    }
}