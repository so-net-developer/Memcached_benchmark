package com.example.lib.workload;

import java.io.FileOutputStream;
import java.io.PrintWriter;

import com.example.lib.util.LatencyPeriod;
import com.example.lib.util.ReportWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 負荷試験マネージャ.
 */
public class WorkloadManager implements ReportWriter {

    private static final Log LOG = LogFactory.getLog(WorkloadManager.class);

    private WorkloadParam param;

    private WorkloadTaskFactory factory;

    private boolean executing = false;

    private final WorkloadReport report = new WorkloadReport();

    private PrintWriter reportWriter = null;

    public WorkloadManager(
            WorkloadParam param,
            WorkloadTaskFactory factory)
    {
        this.param = param;
        this.factory = factory;
        this.report.taskReport = this.factory.createReportObject();
    }

    public void executeWorkload() throws Exception {
        PrintWriter writer = null;
        try {
            // レポートファイルを作成.
            if (this.param.reportFile != null) {
                writer = new PrintWriter(
                        new FileOutputStream(this.param.reportFile, true),
                        false);
                this.reportWriter = writer;
            }
            executeWorkload2();
        } finally {
            if (writer != null) {
                if (this.reportWriter == writer) {
                    this.reportWriter = null;
                }
                writer.close();
            }
        }
    }

    private void executeWorkload2() throws Exception {
        synchronized (this.report) {
            reportReset();
        }
        this.executing = true;
        this.factory.setupTaskArgs(this.param.taskArgs);

        // レポート出力用スレッドの開始.
        Thread reportThread = new Thread() {
            @Override
            public void run() {
                runReportThread();
            }
        };
        reportThread.start();

        // 並列負荷試験をマルチスレッド実行する.
        for (int i = 1; i < this.param.taskParallel; ++i) {
            final int parallelIndex = i;
            Thread workloadThread = new Thread() {
                @Override
                public void run() {
                    try {
                        executeWorkload3(parallelIndex);
                    } catch (Exception e) {
                        LOG.error("parallel workload failed #" +
                                parallelIndex, e);
                    }
                }
            };
            workloadThread.start();
        }

        // メイン負荷試験はメインスレッドで実行する.
        executeWorkload3(0);

        this.executing = false;
        reportOutput();
    }

    /**
     * 負荷試験のタスクを実行する.
     */
    private void executeWorkload3(int parallelIndex) throws Exception {
        // 負荷試験タスク実行.
        long index = 0;
        while (isContinueWorkload(index)) {
            long start = System.currentTimeMillis();
            WorkloadTask task =
                this.factory.createWorkloadTask(this, this.param, index,
                        parallelIndex);

            if (task != null) {
                try {
                    task.executeTask();
                } catch (Exception e) {
                    LOG.warn("task execution error", e);
                }

                // 負荷試験レポートを更新する.
                Object report = this.factory.disposeWorkloadTask(task);
                reportUpdate(1, task.latencyPeriod, report);
            } else {
                LOG.warn("createWorkloadTask() failed");
            }

            // 次のタスクを実行するまで待つ.
            waitNextTask(start);
            ++index;
        }
    }

    /**
     * レポート用スレッドの実体.
     */
    private void runReportThread() {
        while (this.executing) {
            try {
                Thread.sleep(this.param.reportInterval);
                if (!this.executing) {
                    break;
                }
                reportOutput();
            } catch (Exception e) {
                LOG.warn("detect a problem in the reporting thread", e);
            }
        }
    }

    /**
     * レポートデータを更新する.
     */
    private void reportUpdate(
            long executionCount,
            LatencyPeriod latencyPeriod,
            Object taskReport)
    {
        synchronized (this.report) {
            reportUpdate2(this.report, executionCount, latencyPeriod,
                    taskReport);
        }
    }

    private void reportUpdate2(
            WorkloadReport r,
            long executionCount,
            LatencyPeriod latencyPeriod,
            Object taskReport)
    {
        r.updated = true;
        if (executionCount > 0) {
            r.executionCount += executionCount;
        }
        if (latencyPeriod != null) {
            r.latencyPeriod.add(latencyPeriod);
        }
        if (taskReport != null && r.taskReport != null) {
            this.factory.updateReportObject(r.taskReport, taskReport);
        }
    }

    /**
     * レポートデータを出力し、次の出力に備えてリセットする.
     */
    private void reportOutput() {
        synchronized (this.report) {
            if (this.report.updated) {
                reportOutput(this.report);
                reportReset();
            }
        }
    }

    private void reportReset() {
        this.report.reset();
        if (this.report.taskReport != null) {
            this.factory.resetReportObject(this.report.taskReport);
        }
    }

    private void reportOutput(WorkloadReport r) {
        writeReport("[%1$tY/%1$tm/%1$td %1$tH:%1$tM:%1$tS.%1$tL]\n",
                System.currentTimeMillis());
        writeReport("  TPS: %1$,.2f\n", r.getTps());
        writeReport("  Latency (nanosec): min=%1$,d max=%2$,d avg=%3$,d\n",
                r.latencyPeriod.getMin(), r.latencyPeriod.getMax(),
                r.latencyPeriod.getAverage());
        if (r.taskReport != null) {
            this.factory.outputReportObject(r.taskReport, this);
        }
        reportFlush();
    }

    public void writeReport(String format, Object... args) {
        String s = String.format(format, args);
        System.out.print(s);
        if (this.reportWriter != null) {
            this.reportWriter.print(s);
        }
    }

    private void reportFlush() {
        System.out.flush();
        if (this.reportWriter != null) {
            this.reportWriter.flush();
        }
    }

    /**
     * 負荷試験を継続するか決定する.
     */
    private boolean isContinueWorkload(long index) {
        // 実行中フラグが寝てたら強制終了.
        if (!this.executing) {
            return false;
        }

        final FinishCondition cond = this.param.finishCondition;

        // 繰り返し回数で負荷試験を終了するか判定する.
        if (cond.count > 0 && index >= cond.count) {
            return false;
        }

        // 時刻で負荷試験を終了するか判定する.
        if (cond.dateTime != null &&
                System.currentTimeMillis() >= cond.dateTime.getTime()) {
            return false;
        }

        // FIXME: 必要ならば他の終了条件判定をココに追加する.

        // 負荷試験を継続する.
        return true;
    }

    /**
     * 次のタスクの実行まで指定された時間を待つ.
     */
    private void waitNextTask(long start) {
        long remain = (start + this.param.taskInterval)
            - System.currentTimeMillis();
        if (remain > 0) {
            try {
                Thread.sleep(remain);
            } catch (InterruptedException e) {
                // through this exception.
            }
        }
    }

}
