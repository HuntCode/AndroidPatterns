package com.example.hc.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 图片加载器
 */
public class ImageLoader {
    //将缓存独立成类，实现单一职责
    ImageCache mImageCache = new ImageCache();

    //SD卡缓存
    DiskCache mDiskCache = new DiskCache();

    //双缓存
    DoubleCache mDoubleCache = new DoubleCache();

    boolean isUsedDiskCache = false;

    boolean isUseDoubleCache = false;

    ExecutorService mExeService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    Handler mUiHandler = new Handler(Looper.getMainLooper());


    public void displayImage(final String url, final ImageView imageView) {
        Bitmap bitmap = null;
        if (isUseDoubleCache) {
            bitmap = mDoubleCache.get(url);
        } else if (isUsedDiskCache) {
            bitmap = mDiskCache.get(url);
        } else {
            bitmap = mImageCache.get(url);
        }

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }

        //缓存中没有，则下载
        imageView.setTag(url);
        mExeService.submit(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = downloadImage(url);
                if (bitmap == null) {
                    return;
                }

                if (imageView.getTag().equals(url)) {
                    updateImageView(imageView, bitmap);
                }
                mImageCache.put(url, bitmap);

            }
        });
    }

    public void useDiskCache(boolean useDiskCache) {
        isUsedDiskCache = useDiskCache;
    }

    public void setUseDoubleCache(boolean useDoubleCache) {
        isUseDoubleCache = useDoubleCache;
    }

    private void updateImageView(final ImageView imageView, final Bitmap bmp) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bmp);
            }
        });
    }

    public Bitmap downloadImage(String imageUrl) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(imageUrl);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    //测试commit到github
}
