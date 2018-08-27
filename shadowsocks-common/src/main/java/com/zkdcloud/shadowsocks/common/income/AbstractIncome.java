package com.zkdcloud.shadowsocks.common.income;

/**
 * 程序启动入口
 *
 * @author zk
 * @since 2018/8/11
 */
public abstract class AbstractIncome {
    public abstract void startup() throws InterruptedException;
}
