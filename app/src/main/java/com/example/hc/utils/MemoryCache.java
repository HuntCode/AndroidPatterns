package com.example.hc.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

public class MemoryCache implements ImageCache {
    LruCache<String, Bitmap> mMemoryCache;

    public MemoryCache() {
        initImageCache();
    }


    private void initImageCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        final int cacheSize = maxMemory / 4;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    public void put(String url, Bitmap bitmap) {
        mMemoryCache.put(url, bitmap);
    }

    public Bitmap get(String url) {
        return mMemoryCache.get(url);
    }
}
