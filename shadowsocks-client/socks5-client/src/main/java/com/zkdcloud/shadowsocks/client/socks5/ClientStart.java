package com.zkdcloud.shadowsocks.client.socks5;

import com.zkdcloud.shadowsocks.client.socks5.income.TcpClientIncome;
import com.zkdcloud.shadowsocks.common.income.AbstractIncome;

public class ClientStart {
    public static void main(String[] args) throws InterruptedException {
        AbstractIncome income = new TcpClientIncome();
        income.startup();
    }
}
