package com.example.app.memcached.task;

public class Generator {

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
        s.append("A1/");
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
        s.append("\"}");
        return s.toString();
    }

}
