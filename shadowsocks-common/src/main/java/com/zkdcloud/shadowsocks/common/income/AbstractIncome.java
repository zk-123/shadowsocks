package com.zkdcloud.shadowsocks.common.income;

/**
 * start proxy
 *
 * @author zk
 * @since 2018/8/11
 */
public abstract class AbstractIncome {
    /**
     * start server or client
     *
     * @throws InterruptedException ex
     */
    public abstract void startup() throws InterruptedException;
}
