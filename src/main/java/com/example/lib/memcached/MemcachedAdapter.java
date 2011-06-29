package com.example.lib.memcached;

public interface MemcachedAdapter {

    void open(String servers) throws Exception;

    void set(String key, String value) throws Exception;

    String get(String key) throws Exception;

    void delete(String key) throws Exception;

    void close() throws Exception;

}
