package com.example.lib.util;

/**
 * レイテンシを計測するための簡易フレームワーク.
 */
public class LatencyPeriod {

    private long max = Long.MIN_VALUE;

    private long min = Long.MAX_VALUE;

    private long total = 0;

    private int count = 0;

    private long start = 0;

    public LatencyPeriod() {
    }

    public LatencyPeriod(LatencyPeriod src) {
        add(src);
    }

    public void reset() {
        this.max = Long.MIN_VALUE;
        this.min = Long.MAX_VALUE;
        this.total = 0;
        this.count = 0;
    }

    public long getMax() {
        return this.max;
    }

    public long getMin() {
        return this.min;
    }

    public long getTotal() {
        return this.total;
    }

    public int getCount() {
        return this.count;
    }

    public long getAverage() {
        long average = 0;
        if (this.count > 0) {
            average = this.total / this.count;
        }
        return average;
    }

    public void start() {
        this.start = System.nanoTime();
    }

    public void end() {
        // FIXME: guard if start() isn't called yet.
        long latency = System.nanoTime() - this.start;
        if (latency < this.min) {
            this.min = latency;
        }
        if (latency > this.max) {
            this.max = latency;
        }
        this.total += latency;
        ++this.count;
    }

    public void add(LatencyPeriod target) {
        if (target.max > this.max) {
            this.max = target.max;
        }
        if (target.min < this.min) {
            this.min = target.min;
        }
        this.total += target.total;
        this.count += target.count;
        this.start = target.start;
    }

}
