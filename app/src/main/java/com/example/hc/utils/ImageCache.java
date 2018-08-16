package com.example.hc.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

public interface ImageCache {
    public Bitmap get(String url);

    public void put(String url, Bitmap bmp);


}
