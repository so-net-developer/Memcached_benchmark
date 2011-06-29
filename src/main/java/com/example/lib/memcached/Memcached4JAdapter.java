package com.example.lib.memcached;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Memcached4JAdapter implements MemcachedAdapter {

    private static final Log LOG = LogFactory.getLog(MemcachedAdapter.class);

    public static class Factory implements MemcachedAdapterFactory {

        @Override
        public MemcachedAdapter createMemcachedAdapter() {
            return new Memcached4JAdapter();
        }

    }

    private SockIOPool pool = null;

    private MemCachedClient client = null;

    private Memcached4JAdapter() {
    }

    @Override
    public void open(String servers) throws Exception {
        // Initialie socket IO pool.
        this.pool = SockIOPool.getInstance();
        this.pool.setServers(servers.split(" "));
        // FXIME: tuning parameters of pool.
        this.pool.initialize();

        this.client = new MemCachedClient();
    }

    @Override
    public void set(String key, String value) throws Exception {
        this.client.set(key, value);
    }

    @Override
    public String get(String key) throws Exception {
        Object v = this.client.get(key);
        return v != null ? v.toString() : null;
    }

    @Override
    public void delete(String key) throws Exception {
        this.client.delete(key);
    }

    @Override
    public void close() {
        if (this.pool != null) {
            this.pool.shutDown();
        }
    }

}
