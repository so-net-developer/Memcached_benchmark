package com.example.lib.util;

/**
 * レポートを報告するオブジェクトが実装すべきインターフェース.
 */
public interface Reporter {

    Object createReportObject();

    void updateReportObject(Object reportObject, Object newReport);

    void outputReportObject(Object reportObject, ReportWriter writer);

    void resetReportObject(Object reportObject);

}
