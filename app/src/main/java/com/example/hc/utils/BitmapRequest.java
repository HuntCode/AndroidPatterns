package com.example.hc.utils;

import android.support.annotation.NonNull;

public class BitmapRequest implements Comparable<BitmapRequest> {
    int serialNum;

    LoadPolicy mLoadPolicy = new SerialPolicy();

    public void setLoadPolicy(LoadPolicy loadPolicy) {
        mLoadPolicy = loadPolicy;
    }

    @Override
    public int compareTo(@NonNull BitmapRequest bitmapRequest) {
        return mLoadPolicy.compare(this, bitmapRequest);
    }
}
