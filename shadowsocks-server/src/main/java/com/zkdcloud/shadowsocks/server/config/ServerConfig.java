package com.zkdcloud.shadowsocks.server.config;

/**
 * server config
 *
 * @author zk
 * @since 2018/8/28
 */
public class ServerConfig {
    public static ServerConfig serverConfig = new ServerConfig();
    /**
     * localAddress
     */
    private String localAddress;
    /**
     * password
     */
    private String password;
    /**
     * method
     */
    private String method;
    /**
     * number of boss thread
     */
    private int bossThreadNumber;
    /**
     * number of workers thread
     */
    private int workersThreadNumber;
    /**
     * client idle time(second)
     */
    private long clientIdle;
    /**
     * remote idle time(second)
     */
    private long remoteIdle;

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getBossThreadNumber() {
        return bossThreadNumber;
    }

    public void setBossThreadNumber(int bossThreadNumber) {
        this.bossThreadNumber = bossThreadNumber;
    }

    public int getWorkersThreadNumber() {
        return workersThreadNumber;
    }

    public void setWorkersThreadNumber(int workersThreadNumber) {
        this.workersThreadNumber = workersThreadNumber;
    }

    public long getClientIdle() {
        return clientIdle;
    }

    public void setClientIdle(long clientIdle) {
        this.clientIdle = clientIdle;
    }

    public long getRemoteIdle() {
        return remoteIdle;
    }

    public void setRemoteIdle(long remoteIdle) {
        this.remoteIdle = remoteIdle;
    }
}
