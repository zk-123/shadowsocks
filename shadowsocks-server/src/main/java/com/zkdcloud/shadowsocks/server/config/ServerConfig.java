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
     * client readIdle time(second)
     */
    private long criTime;
    /**
     * client readIdle time(second)
     */
    private long cwiTime;
    /**
     * client allIdle time(second)
     */
    private long caiTime;
    /**
     * remote readIdle time(second)
     */
    private long rriTime;
    /**
     * remote readIdle time(second)
     */
    private long rwiTime;
    /**
     * remote allIdle time(second)
     */
    private long raiTime;


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

    public long getCriTime() {
        return criTime;
    }

    public void setCriTime(long criTime) {
        this.criTime = criTime;
    }

    public long getCwiTime() {
        return cwiTime;
    }

    public void setCwiTime(long cwiTime) {
        this.cwiTime = cwiTime;
    }

    public long getCaiTime() {
        return caiTime;
    }

    public void setCaiTime(long caiTime) {
        this.caiTime = caiTime;
    }

    public long getRriTime() {
        return rriTime;
    }

    public void setRriTime(long rriTime) {
        this.rriTime = rriTime;
    }

    public long getRwiTime() {
        return rwiTime;
    }

    public void setRwiTime(long rwiTime) {
        this.rwiTime = rwiTime;
    }

    public long getRaiTime() {
        return raiTime;
    }

    public void setRaiTime(long raiTime) {
        this.raiTime = raiTime;
    }
}
