package com.sogou.lib.network.aop;

/**
 * @date:2019-12-02
 * @author:baidengpan
 */
public class RequestRecord {
    public long startTime;
    /**
     * 解析到的dns
     */
    public String dns;

    /**
     * 使用Http Dns标志位
     */
    public boolean usingHttpDns;
    /**
     * 开始dns解析标志位，OKHttp复用缓存池的连接时，不会再次查dns，因此调用开始的时候，
     * 把标志位重置，发生dns解析开始回调时设置为true。检查附带在连接上的这个值，可以知道
     * 连接是新建的连接还是复用连接池已有的连接。
     */
    public boolean dnsStarting;
}
