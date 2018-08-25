package com.example.hc.utils;

public class ImageLoaderConfig {

    ImageCache imageCache = new MemoryCache();

    DisplayConfig displayConfig = new DisplayConfig();

    LoadPolicy loadPolicy = new SerialPolicy();

    int threadCount = Runtime.getRuntime().availableProcessors() + 1;

    private ImageLoaderConfig() {

    }

    public static class Builder {
        ImageCache imageCache = new MemoryCache();

        DisplayConfig displayConfig = new DisplayConfig();

        LoadPolicy loadPolicy = new SerialPolicy();

        int threadCount = Runtime.getRuntime().availableProcessors() + 1;

        public Builder setThreadCount(int count) {
            threadCount = Math.max(1, count);
            return this;
        }

        public Builder setCache(ImageCache cache) {
            imageCache = cache;
            return this;
        }

        public Builder setLoadingImage(int resId) {
            displayConfig.loadingResId = resId;
            return this;
        }

        public Builder setLoadingFailedImage(int resId) {
            displayConfig.loadingFailedResId = resId;
            return this;
        }

        public Builder setPolicy(LoadPolicy policy) {
            if (policy != null) {
                loadPolicy = policy;
            }

            return this;
        }

        void applyConfig(ImageLoaderConfig config) {
            config.imageCache = this.imageCache;
            config.displayConfig = this.displayConfig;
            config.loadPolicy = this.loadPolicy;
            config.threadCount = this.threadCount;
        }

        public ImageLoaderConfig create() {
            ImageLoaderConfig config = new ImageLoaderConfig();
            applyConfig(config);
            return config;
        }
    }
}
