package com.example.lib.memcached;

import java.io.IOException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClient;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XMemcachedAdapter implements MemcachedAdapter {

    private static final Log LOG = LogFactory.getLog(XMemcachedAdapter.class);

    public static final int TIMEOUT = 10000;

    public static class Factory implements MemcachedAdapterFactory {

        @Override
        public MemcachedAdapter createMemcachedAdapter() {
            return new XMemcachedAdapter();
        }

    }

    private MemcachedClient client = null;

    private XMemcachedAdapter() {
    }

    @Override
    public void open(String servers) throws Exception {
        this.client = new XMemcachedClient(AddrUtil.getAddresses(servers));
    }

    @Override
    public void set(String key, String value) throws Exception {
        this.client.set(key, 0, value, TIMEOUT);
    }

    @Override
    public String get(String key) throws Exception {
        return this.client.get(key, TIMEOUT);
    }

    @Override
    public void delete(String key) throws Exception {
        this.client.delete(key, TIMEOUT);
    }

    @Override
    public void close() {
        if (this.client != null) {
            try {
                this.client.shutdown();
            } catch (IOException e) {
                LOG.warn(e);
            }
            this.client = null;
        }
    }

}
