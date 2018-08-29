package com.example.hc.utils;

/*
 * 逆序加载策略
 */
public class ReversePolicy implements LoadPolicy {
    @Override
    public int compare(BitmapRequest request1, BitmapRequest request2) {
        return request2.serialNum - request1.serialNum;
    }
}
