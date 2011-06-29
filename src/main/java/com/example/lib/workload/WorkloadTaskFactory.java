package com.example.lib.workload;

import java.util.List;

import com.example.lib.util.ReportWriter;
import com.example.lib.util.Reporter;

public interface WorkloadTaskFactory extends Reporter {

    void setupTaskArgs(List<String> args);

    WorkloadTask createWorkloadTask(
            WorkloadManager manager,
            WorkloadParam param,
            long index,
            int parallelIndex);

    /**
     * タスクを破棄する.
     *
     * @return レポートデータを返す.
     */
    Object disposeWorkloadTask(
            WorkloadTask task);

}
