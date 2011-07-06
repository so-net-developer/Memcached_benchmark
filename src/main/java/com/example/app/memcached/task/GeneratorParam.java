package com.example.app.memcached.task;

/**
 * ベンチマークや負荷試験に使用するキーや値の内容を制御するためのパラメータ.
 */
public final class GeneratorParam {

    public String keyPrefix = null;
    
    public boolean valueLengthIsVariable = false;

    public int valueLengthMax = -1;

    /**
     * @retval 0ならば解釈しなかった. 0より小さければエラーを検出した.
     *         0より大きければその数だけ引数を解釈に利用した.
     */
    public int parseArgs(String first, String second) {
        if ("-kp".equals(first)) {
            if (second == null) {
                System.err.println("-kp (key prefix) require an argument");
                return -1;
            }
            this.keyPrefix = second;
            return 2;
        } else if ("-vv".equals(first)) {
            this.valueLengthIsVariable = true;
            return 1;
        } else if ("-vx".equals(first)) {
            this.valueLengthMax = Integer.parseInt(second);
            return 2;
        }
        return 0;
    }

}
