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
    ImageCache mImageCache = new MemoryCache();

    //线程池
    ExecutorService mExeService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    Handler mUiHandler = new Handler(Looper.getMainLooper());


    public void setImageCache(ImageCache cache) {
        mImageCache = cache;
    }

    public void displayImage(final String imageUrl, final ImageView imageView) {
        Bitmap bitmap = mImageCache.get(imageUrl);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }

        //缓存中没有，则下载
        submitLoadReqest(imageUrl, imageView);

    }

    private void submitLoadReqest(final String imageUrl, final ImageView imageView){
        imageView.setTag(imageUrl);
        mExeService.submit(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = downloadImage(imageUrl);
                if (bitmap == null) {
                    return;
                }

                if (imageView.getTag().equals(imageUrl)) {
                    updateImageView(imageView, bitmap);
                }
                mImageCache.put(imageUrl, bitmap);

            }
        });
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
}
