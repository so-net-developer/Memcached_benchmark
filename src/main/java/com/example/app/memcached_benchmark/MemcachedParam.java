package com.example.app.memcached_benchmark;

import java.util.HashMap;

import com.example.lib.memcached.Memcached4JAdapter;
import com.example.lib.memcached.MemcachedAdapter;
import com.example.lib.memcached.MemcachedAdapterFactory;
import com.example.lib.memcached.XMemcachedAdapter;

/**
 * memcached に必要なパラメータ.
 */
public class MemcachedParam implements MemcachedAdapterFactory {

    private static final HashMap<String,MemcachedAdapterFactory> FACTORIES;

    public static final String ADAPTER_MEMCACHED4J = "memcached";
    public static final String ADAPTER_XMEMCACHED = "xmemcached";

    static {
        FACTORIES = new HashMap<String,MemcachedAdapterFactory>();
        FACTORIES.put(ADAPTER_MEMCACHED4J, new Memcached4JAdapter.Factory());
        FACTORIES.put(ADAPTER_XMEMCACHED, new XMemcachedAdapter.Factory());
    }

    private String servers = "localhost:11211";

    private String adapterName = ADAPTER_MEMCACHED4J;

    public String getServers() {
        return this.servers;
    }

    public void setServers(String value) {
        this.servers = value;
    }

    public String getAdapterName() {
        return this.adapterName;
    }

    public void setAdapterName(String value) {
        if (FACTORIES.containsKey(value)) {
            this.adapterName = value;
        } else {
            throw new IllegalArgumentException(
                    "unsupported memcaced adapter: " + value);
        }
    }

    /**
     * アダプタを作成する.
     */
    public MemcachedAdapter openNewAdapter() throws Exception {
        MemcachedAdapterFactory factory = FACTORIES.get(this.adapterName);
        MemcachedAdapter adapter = factory.createMemcachedAdapter();
        adapter.open(this.servers);
        return adapter;
    }

    /**
     * 接続済みのアダプタを作成する.
     */
    @Override
    public MemcachedAdapter createMemcachedAdapter() {
        MemcachedAdapter adapter = null;
        try {
            adapter = openNewAdapter();
        } catch (Exception e) {
        }
        return adapter;
    }

}
