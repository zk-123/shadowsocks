package com.zkdcloud.shadowsocks.client.socks5;

import com.zkdcloud.shadowsocks.client.socks5.income.TcpClientIncome;
import com.zkdcloud.shadowsocks.common.income.AbstractIncome;

public class ClientStart {
    public static void main(String[] args) throws InterruptedException {
        AbstractIncome income = new TcpClientIncome();
        short port = args != null && args.length == 1 ? Short.valueOf(args[0]) : 1080;
        income.startup(port);
    }
}
