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
    //volatile用于解决DLC失效问题
    private volatile static ImageLoader sInstance;

    //网络请求队列
    private RequestQueue mImageQueue;

    private ImageCache mImageCache = new MemoryCache();

    private ImageLoaderConfig mConfig;

    private ImageLoader() {
    }

    //DCL方式单例
    public static ImageLoader getInstance() {
        if (sInstance == null) {
            synchronized (ImageLoader.class) {
                if (sInstance == null) {
                    sInstance = new ImageLoader();
                }
            }
        }

        return sInstance;
    }

    public void init(ImageLoaderConfig config) {
        mConfig = config;
        mImageCache = mConfig.imageCache;
        checkConfig();
        mImageQueue = new RequestQueue(mConfig.threadCount);
        mImageQueue.start();
    }

    private void checkConfig() {
        //检查配置项
    }

    //线程池
    ExecutorService mExeService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    Handler mUiHandler = new Handler(Looper.getMainLooper());

    /* 先注释，后面看情况改或删除，此段代码来自"单例模式"章节
        public void displayImage(ImageView imageView, String url) {
            displayImage(imageView, url, null, null);
        }

        public void displayImage(ImageView imageView, String url, ImageListener listener) {
            displayImage(imageView, url, null, listener);
        }

        public void displayImage(final ImageView imageView, final String url, final DisplayConfig config, final ImageListener listener) {
            BitmapRequest request = new BitmapRequest(imageView, url, config, listener);
            request.displayConfig = request.displayConfig != null ? request.displayConfig : mConfig.displayConfig;

            //设置加载策略
            request.setLoadPolicy(mConfig.loadPolicy);

            mImageQueue.addRequest(request)
        }
    */
    public void stop() {
        mImageQueue.stop();
    }

    public static interface ImageListener {
        public void onComplete(ImageView imageView, Bitmap bitmap, String url);
    }


    public void displayImage(final String imageUrl, final ImageView imageView) {
        Bitmap bitmap = mImageCache.get(imageUrl);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }

        //缓存中没有，则下载
        submitLoadReqest(imageUrl, imageView);
    }

    private void submitLoadReqest(final String imageUrl, final ImageView imageView) {
        imageView.setImageResource(mLoadingImageId);
        imageView.setTag(imageUrl);

        mExeService.submit(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = downloadImage(imageUrl);
                if (bitmap == null) {
                    imageView.setImageResource(mLoadingFailedImageId);
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
