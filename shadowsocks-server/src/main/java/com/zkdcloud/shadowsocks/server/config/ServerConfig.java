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
     * localPort
     */
    private int localPort;
    /**
     * password
     */
    private String password;
    /**
     * method
     */
    private String method;
    /**
     * timeOut
     */
    private Long timeout;
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
    private Long criTime;
    /**
     * client readIdle time(second)
     */
    private Long cwiTime;
    /**
     * client allIdle time(second)
     */
    private Long caiTime;
    /**
     * remote readIdle time(second)
     */
    private Long rriTime;
    /**
     * remote readIdle time(second)
     */
    private Long rwiTime;
    /**
     * remote allIdle time(second)
     */
    private Long raiTime;


    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
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

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
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

    public Long getCriTime() {
        return criTime;
    }

    public void setCriTime(Long criTime) {
        this.criTime = criTime;
    }

    public Long getCwiTime() {
        return cwiTime;
    }

    public void setCwiTime(Long cwiTime) {
        this.cwiTime = cwiTime;
    }

    public Long getCaiTime() {
        return caiTime;
    }

    public void setCaiTime(Long caiTime) {
        this.caiTime = caiTime;
    }

    public Long getRriTime() {
        return rriTime;
    }

    public void setRriTime(Long rriTime) {
        this.rriTime = rriTime;
    }

    public Long getRwiTime() {
        return rwiTime;
    }

    public void setRwiTime(Long rwiTime) {
        this.rwiTime = rwiTime;
    }

    public Long getRaiTime() {
        return raiTime;
    }

    public void setRaiTime(Long raiTime) {
        this.raiTime = raiTime;
    }
}
