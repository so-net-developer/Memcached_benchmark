package com.example.app.memcached.task;

import java.util.Random;

public class Generator {

    public static final int DEFAULT_VALUE_MAXLEN = 100;

    private static GeneratorParam param = new GeneratorParam();

    public static void setGeneratorParam(GeneratorParam value) {
        Generator.param = value;
    }

    public static StringBuilder formatNumber(
            StringBuilder s,
            int number,
            int width,
            char padding)
    {
        String v = Integer.toString(number);
        for (int i = v.length(); i < width; ++i) {
            s.append(padding);
        }
        s.append(v);
        return s;
    }

    /**
     * Get valid key.
     */
    public static String getKey(int index, int subIndex) {
        return getValidKey(index, subIndex);
    }

    /**
     * Get valid key.
     */
    public static String getValidKey(int index, int subIndex) {
        return getRawKey(index, subIndex, 'f');
    }

    /**
     * Get invalid key.
     */
    public static String getInvalidKey(int index, int subIndex) {
        return getRawKey(index, subIndex, 'a');
    }

    public static String getRawKey(int index, int subIndex, char sep) {
        StringBuilder s = new StringBuilder();
        if (param.keyPrefix != null) {
            s.append(param.keyPrefix);
        } else {
            s.append("A1/");
        }
        formatNumber(s, index, 20, '0');
        s.append(sep);
        formatNumber(s, subIndex, 20, '0');
        return s.toString();
    }

    public static String getValue(int index, int subIndex, int revision) {
        StringBuilder s = new StringBuilder();
        s.append("{");
        s.append("\"index\":\"");
        formatNumber(s, index, 30, '0');
        s.append("\",");
        s.append("\"sub\":\"");
        formatNumber(s, subIndex, 30, '0');
        s.append("\",");
        s.append("\"rev\":\"");
        formatNumber(s, revision, 30, '0');

        if (param.valueLengthIsVariable) {
            int seed = subIndex + index * 13 + revision * 37;
            appendRandomPayload(s, seed);
        }

        s.append("\"}");
        return s.toString();
    }

    /**
     * 可変長のフィールドを値に追加する.
     */
    public static void appendRandomPayload(StringBuilder s, int seed) {
        // Append
        s.append("\",");
        s.append("\"payload\":\"");

        // Determine max length.
        final int max;
        if (Generator.param.valueLengthMax > 0) {
            max = Generator.param.valueLengthMax;
        } else {
            max = DEFAULT_VALUE_MAXLEN;
        }

        // Determine and append payload.
        Random r = new Random(seed);
        final int len = r.nextInt(max) + 1;
        for (int i = 0; i < len; ++i) {
            final int n = r.nextInt(10);
            s.append((char)('0' + n));
        }
    }

}
