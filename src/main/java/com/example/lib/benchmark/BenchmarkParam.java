package com.example.lib.benchmark;

public class BenchmarkParam {

    public int parallelCount;

    public BenchmarkParam(int parallelCount) {
        this.parallelCount = parallelCount;
    }

    public BenchmarkParam() {
        this(1);
    }
}
