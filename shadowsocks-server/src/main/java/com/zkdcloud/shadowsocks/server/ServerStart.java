package com.zkdcloud.shadowsocks.server;

import com.zkdcloud.shadowsocks.common.income.AbstractIncome;
import com.zkdcloud.shadowsocks.server.income.TCPServerIncome;

/**
 * server start
 *
 * @author zk
 * @since 2018/8/11
 */
public class ServerStart {
    public static void main(String[] args) throws InterruptedException {
        AbstractIncome abstractIncome = new TCPServerIncome();
        abstractIncome.startup();
    }
}
