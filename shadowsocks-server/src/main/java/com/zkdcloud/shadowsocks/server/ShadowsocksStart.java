package com.zkdcloud.shadowsocks.server;

import com.zkdcloud.shadowsocks.server.income.AbstractIncome;
import com.zkdcloud.shadowsocks.server.income.TCPStartIncome;

/**
 * description
 *
 * @author zk
 * @since 2018/8/11
 */
public class ShadowsocksStart {
    public static void main(String[] args) throws InterruptedException {
        AbstractIncome abstractIncome = new TCPStartIncome();
        abstractIncome.startup();
    }
}
