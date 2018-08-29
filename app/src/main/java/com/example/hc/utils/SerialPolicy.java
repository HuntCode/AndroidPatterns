package com.example.hc.utils;

/*
 * 顺序加载策略
 */
public class SerialPolicy implements LoadPolicy {
    @Override
    public int compare(BitmapRequest request1, BitmapRequest request2) {
        return request1.serialNum - request2.serialNum;
    }
}
