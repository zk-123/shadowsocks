package com.zkdcloud.shadowsocks.income;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * description
 *
 * @author zk
 * @since 2018/8/11
 */
public abstract class AbstractIncome {
    /**
     * boosLoopGroup
     */
    protected EventLoopGroup bossLoopGroup = new NioEventLoopGroup(1,new DefaultThreadFactory("boss"));
    /**
     * worksLoopGroup
     */
    protected EventLoopGroup worksLoopGroup = new NioEventLoopGroup(1,new DefaultThreadFactory("works"));

    public abstract void startup() throws InterruptedException;
}
