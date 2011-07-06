package com.example.app.memcached_benchmark;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

import com.example.app.memcached.task.Generator;
import com.example.app.memcached.task.GeneratorParam;
import com.example.app.memcached_benchmark.task.GetWorkloadTask;
import com.example.app.memcached_benchmark.task.MemcachedWorkloadTaskFactory;
import com.example.app.memcached_benchmark.task.MixWorkloadTaskFactory;
import com.example.lib.memcached.Memcached4JAdapter;
import com.example.lib.memcached.MemcachedAdapter;
import com.example.lib.memcached.MemcachedAdapterFactory;
import com.example.lib.memcached.XMemcachedAdapter;
import com.example.lib.workload.WorkloadManager;
import com.example.lib.workload.WorkloadParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 負荷試験実施のためのエントリーポイント.
 */
public class Load {

    private static final Log LOG = LogFactory.getLog(Load.class);

    private static final HashMap<String, MemcachedWorkloadTaskFactory> TASKS;

    static {
        TASKS = new HashMap<String, MemcachedWorkloadTaskFactory>();
        TASKS.put("get", new GetWorkloadTask.Factory());
        TASKS.put("mix", new MixWorkloadTaskFactory());
    }

    public static void main(String[] args) {
        try {
            run(args);
        } catch (Exception e) {
            LOG.error("Workload aborted", e);
        }
    }

    public static void run(String[] args) throws Exception {
        // 負荷試験のパラメータを取得する.
        WorkloadParam param = new WorkloadParam();
        MemcachedParam param2 = new MemcachedParam();
        GeneratorParam param3 = new GeneratorParam();
        if (!parseArgs(args, param, param2, param3)) {
            LOG.error("invalid arguments");
            return;
        }

        run(param, param2, param3);
    }

    /**
     * 負荷試験を実行する.
     */
    public static boolean run(
            WorkloadParam workloadParam,
            MemcachedParam memcachedParam,
            GeneratorParam generatorParam)
        throws Exception
    {
        // 負荷試験のタスクを取得する.
        MemcachedWorkloadTaskFactory factory = selectFactory(workloadParam);
        if (factory == null) {
            LOG.warn("invalid task: " + workloadParam.taskName);
            return false;
        }

        // FIXME: memcachedParam を直接渡すのではなく、専用ファクトリでラッ
        // ピングする方が良い.
        factory.setMemcachedAdapterFactory(memcachedParam);

        // その他のパラメータを設定する.
        Generator.setGeneratorParam(generatorParam);

        // 負荷試験を実行する.
        boolean retval = false;
        WorkloadManager mananger = new WorkloadManager(workloadParam,
                factory);
        try {
            mananger.executeWorkload();
            retval = true;
        } catch (Exception e) {
            LOG.fatal("workload failed", e);
        }

        return retval;
    }

    /**
     * 引数から負荷試験のパラメータを設定する.
     */
    private static boolean parseArgs(
            String[] args,
            WorkloadParam param,
            MemcachedParam param2,
            GeneratorParam param3)
    {
        for (int i = 0, I = args.length; i < I; ++i) {
            String first = args[i];
            String second = i + 1 < args.length ? args[i + 1] : null;

            if ("-t".equals(first) && second != null) {
                param.taskName = second;
                ++i;
            } else if ("-tp".equals(first) && second != null) {
                param.taskParallel = Integer.parseInt(second);
                ++i;
            } else if ("-ti".equals(first) && second != null) {
                param.taskInterval = Long.parseLong(second);
                ++i;
            } else if ("-fc".equals(first) && second != null) {
                param.finishCondition.count = Integer.parseInt(second);
                ++i;
            } else if ("-ft".equals(first) && second != null) {
                param.finishCondition.dateTime = parseAsDate(second);
                ++i;
            } else if ("-ri".equals(first) && second != null) {
                param.reportInterval = Long.parseLong(second);
                ++i;
            } else if ("-rf".equals(first) && second != null) {
                param.reportFile = parseAsFile(second);
                if (param.reportFile == null) {
                    LOG.warn("Can't write report file as " + second);
                }
                ++i;
            } else if ("-s".equals(first) && second != null) {
                param2.setServers(second);
                ++i;
            } else if ("-a".equals(first) && second != null) {
                param2.setAdapterName(second);
                ++i;
            } else {
                // Try to parse as GeneratorParam.
                int skip = param3.parseArgs(first, second);
                if (skip < 0) {
                    return false;
                } else if (skip > 0) {
                    i += skip - 1;
                    continue;
                }

                // Otherwise, parse as task arguments.
                while (i < I) {
                    param.taskArgs.add(args[i]);
                    ++i;
                }
            }
        }

        // 設定値のチェックを行う.
        boolean valid = true;
        if (param.taskName == null) {
            LOG.info("'-t' option required");
            valid = false;
        }
        if (param.taskParallel <= 0) {
            LOG.info("'-tp' requires over zero value");
            valid = false;
        }

        return valid;
    }

    public static Date parseAsDate(String s) {
        // FIXME: 様々なフォーマットを指定できるようにする.
        // (現在は秒数指定のみ)
        long endTime = System.currentTimeMillis() + Long.parseLong(s) * 1000;
        return new Date(endTime);
    }

    public static File parseAsFile(String s) {
        File f = new File(s);
        if (!f.exists() || (f.isFile() && f.canWrite())) {
            return f;
        } else {
            return null;
        }
    }

    private static MemcachedWorkloadTaskFactory selectFactory(
            WorkloadParam param)
    {
        return TASKS.get(param.taskName);
    }

}
