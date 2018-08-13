package com.zkdcloud.shadowsocks;

import com.zkdcloud.shadowsocks.income.AbstractIncome;
import com.zkdcloud.shadowsocks.income.TCPStartIncome;

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
