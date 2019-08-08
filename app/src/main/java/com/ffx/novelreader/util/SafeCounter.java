package com.ffx.novelreader.util;

/**
 * Created by TwoFlyLiu on 2019/8/7.
 */

public class SafeCounter {
    private int count;

    public  SafeCounter() {
        count = 0;
    }

    public SafeCounter(int count) {
        this.count = count;
    }

    public synchronized int getCount() {
        return count;
    }

    public synchronized void setCount(int count) {
        this.count = count;
    }

    public synchronized void increase(int step) {
        this.count += step;
    }

    public synchronized void decrease(int step) {
        this.count -= step;
    }
}
