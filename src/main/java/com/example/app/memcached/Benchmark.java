package com.example.app.memcached;

import com.example.app.memcached.task.DeleteTask;
import com.example.app.memcached.task.GetTask2;
import com.example.app.memcached.task.GetTask;
import com.example.app.memcached.task.SetTask;
import com.example.app.memcached.task.TaskFactory;
import com.example.app.memcached.task.TaskParam;
import com.example.app.memcached.task.WaitTask;
import com.example.lib.benchmark.BenchmarkManager;
import com.example.lib.benchmark.BenchmarkParam;
import com.example.lib.memcached.Memcached4JAdapter;
import com.example.lib.memcached.MemcachedAdapter;
import com.example.lib.memcached.MemcachedAdapterFactory;
import com.example.lib.memcached.XMemcachedAdapter;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Benchmark {

    private static final Log LOG = LogFactory.getLog(Benchmark.class);

    private static final HashMap<String,TaskFactory> taskMap;

    private static final HashMap<String,MemcachedAdapterFactory> adapterMap;

    static {
        taskMap = new HashMap<String,TaskFactory>();
        taskMap.put("wait", new WaitTask.Factory());
        taskMap.put("set", new SetTask.Factory());
        taskMap.put("get", new GetTask.Factory());
        taskMap.put("get30", new GetTask2.Factory(30));
        taskMap.put("delete", new DeleteTask.Factory());

        adapterMap = new HashMap<String,MemcachedAdapterFactory>();
        adapterMap.put("xmemcached", new XMemcachedAdapter.Factory());
        adapterMap.put("memcached", new Memcached4JAdapter.Factory());
    }

    public static class Param {

        public String mode = null;

        public boolean warmup = false;

        public int parallelCount = 10;

        public TaskParam taskParam = new TaskParam();

        public String adapter = "memcached";

    }

    public static void main(String[] args) {
        Param p = new Param();
        if (!parse(args, p)) {
            return;
        }

        LOG.info("mode=" + p.mode);

        if ("help".equals(p.mode)) {
            showUsage();
        } else if (taskMap.containsKey(p.mode)) {
            if (!isValid(p)) {
                return;
            }
            try {
                runBenchmarkTask(p, taskMap.get(p.mode),
                        adapterMap.get(p.adapter));
            } catch (Exception e) {
                LOG.error("detect error", e);
            }
        } else {
            showUsage();
        }
    }

    /**
     * Parse arguments as Param.
     *
     * @param in args
     * @param out param
     */
    private static boolean parse(String[] args, Param param) {
        // Detemine the mode.
        if (args.length > 0) {
            param.mode = args[0];
        } else {
            System.err.println("require arguments");
            return false;
        }

        for (int i = 1; i < args.length; ++i) {
            String first = args[i];
            String second = i + 1 < args.length ? args[i + 1] : null;

            if ("-p".equals(first) && second != null) {
                ++i;
                param.parallelCount = Integer.parseInt(second);
            } else if ("-w".equals(first)) {
                param.warmup = true;
            } else if ("-i".equals(first) && second != null) {
                ++i;
                param.taskParam.iteration = Integer.parseInt(second);
            } else if ("-r".equals(first) && second != null) {
                ++i;
                param.taskParam.revision = Integer.parseInt(second);
            } else if ("-s".equals(first) && second != null) {
                ++i;
                param.taskParam.servers = second;
            } else if ("-a".equals(first) && second != null) {
                ++i;
                param.adapter = second;
            }
        }

        return true;
    }

    private static boolean isValid(Param param) {
        if (!taskMap.containsKey(param.mode)) {
            System.err.println("unknown task: " + param.mode);
            return false;
        } else if (!adapterMap.containsKey(param.adapter)) {
            System.err.println("unknown adapter: " + param.adapter);
            return false;
        } else {
            return true;
        }
    }

    private static void showUsage() {
        LOG.trace("showUsage");
    }

    private static void runBenchmarkTask(
            Param param,
            TaskFactory taskFactory,
            MemcachedAdapterFactory adapterFactory)
        throws Exception
    {
        if (LOG.isInfoEnabled()) {
            LOG.info("run benchmark: " + param.mode);
        }
        dumpParam(param);

        MemcachedAdapter adapter = adapterFactory.createMemcachedAdapter();
        adapter.open(param.taskParam.servers);

        long elapsed = -1;
        long score = 0;

        try {
            BenchmarkParam param2 = new BenchmarkParam(
                    param.parallelCount);
            taskFactory.setTaskParam(param.taskParam);
            taskFactory.setMemcachedAdapter(adapter);
            BenchmarkManager manager = new BenchmarkManager(param2,
                    taskFactory);

            try {
                if (param.warmup) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("warming up: " + param.mode); 
                    }
                    manager.executeBenchmark();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("measuring: " + param.mode);
                    }
                    taskFactory.resetTaskResults();
                }
                elapsed = manager.executeBenchmark();
                score = manager.getScore();
            } catch (Exception e) {
                LOG.fatal("benchmark failed", e);
            }
        } finally {
            adapter.close();
        }

        // Calculate and output TPS.
        double tps = score * (double)1000000000 / elapsed;

        // Output benchmark result.
        System.out.println("Benchmark's results");
        System.out.format("  finished %1$d tasks in %2$d (nanosec)\n",
                param.parallelCount, elapsed);
        System.out.format("  task=%1$s TPS=%2$.2f\n", param.mode, tps);
        List<String> othres = taskFactory.getTaskResultsString();
        for (String s : othres) {
            System.out.format("  %1$s\n", s);
        }
    }

    private static void dumpParam(Param param) {
        System.out.format("Benchmark's parameters:\n");
        System.out.format("  Mode: %1$s\n", param.mode);
        System.out.format("  Adapter: %1$s\n", param.adapter);
        System.out.format("  Threads: %1$d\n", param.parallelCount);
        System.out.format("  Warming up: %1$s\n",
                param.warmup ? "enabled" : "disabled");
        System.out.format("  Iteration: %1$d\n", param.taskParam.iteration);
        System.out.format("  Revision: %1$d\n", param.taskParam.revision);
        System.out.format("  Servers: %1$s\n", param.taskParam.servers);
    }

}
