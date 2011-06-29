package com.example.lib.workload;

import com.example.lib.util.LatencyPeriod;
/**
 * 負荷試験レポート.
 */
public class WorkloadReport {

    public boolean updated = false;

    public long startNanoTime = 0;

    public long executionCount = 0;

    public final LatencyPeriod latencyPeriod = new LatencyPeriod();

    public Object taskReport = null;

    public void reset() {
        this.latencyPeriod.reset();
        this.executionCount = 0;
        this.startNanoTime = System.nanoTime();
        this.updated = false;
    }

    public double getTps() {
        long elapsed = System.nanoTime() - this.startNanoTime;
        return this.executionCount * (double)1000000000 / elapsed;
    }

}
