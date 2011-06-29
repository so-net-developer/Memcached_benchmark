package com.example.lib.benchmark;

import java.util.List;

public interface BenchmarkTaskFactory {

    AbstractedBenchmarkTask createBenchmarkTask(
            int index,
            BenchmarkParam param);

    void disposeBenchmarkTask(AbstractedBenchmarkTask task);

    List<String> getTaskResultsString();

    void resetTaskResults();

}
