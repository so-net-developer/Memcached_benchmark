package com.example.app.memcached_benchmark;

import com.example.app.memcached.Benchmark;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Main {

    private static final Log LOG = LogFactory.getLog(Main.class);

    private static void showUsage() {
        System.out.println("{COMMAND} {MODE} [OPTIONS...]");
        System.out.println("");
        System.out.println("MODE:");
        System.out.println("  bench     Run benchmark");
        System.out.println("  load      Run benchmark");
    }

    /**
     * 配列の最初の要素を取り除いた配列を返す.
     */
    private static String[] strip(String[] array) {
        // 長さを決定.
        int length = 0;
        if (array != null && array.length > 1) {
            length = array.length - 1;
        }
        // 要素のコピー(+位置ずらし).
        String[] newArray = new String[length];
        for (int i = 0; i < newArray.length; ++i) {
            newArray[i] = array[i + 1];
        }
        return newArray;
    }

    public static void main(String[] args) {
        String mode = args.length > 0 ? args[0] : null;
        if ("bench".equals(mode)) {
            Benchmark.main(strip(args));
        } else if ("load".equals(mode)) {
            Load.main(strip(args));
        } else {
            showUsage();
            System.exit(0);
        }
    }

}
