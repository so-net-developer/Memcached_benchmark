package com.example.lib.workload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 負荷試験実施用のパラメータ.
 */
public class WorkloadParam
{

    public String taskName = null;

    public final List<String> taskArgs = new ArrayList<String>();

    public int taskParallel = 1;

    public long taskInterval = 0;

    public final FinishCondition finishCondition = new FinishCondition();

    /** 画面に結果を表示するためのインターバル. */
    public long reportInterval = 5000;

    /** 結果を出力(追記)するファイル. */
    public File reportFile = null;

}
