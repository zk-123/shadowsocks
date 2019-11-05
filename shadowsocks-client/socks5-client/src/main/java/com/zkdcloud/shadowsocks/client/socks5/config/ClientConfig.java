package com.zkdcloud.shadowsocks.client.socks5.config;

/**
 * client config
 *
 * @author zk
 * @since 2018/8/23
 */
public class ClientConfig {
    public static ClientConfig clientConfig = new ClientConfig();
    /**
     * server address
     */
    private String server;
    /**
     * local address
     */
    private String local;
    /**
     * password
     */
    private String password;
    /**
     * method
     */
    private String method;
    /**
     * idle seconds
     */
    private Integer idleTime;
    /**
     * number of boss thread
     */
    private int bossThreadNumber;
    /**
     * number of workers thread
     */
    private int workersThreadNumber;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
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

    public static ClientConfig getClientConfig() {
        return clientConfig;
    }

    public static void setClientConfig(ClientConfig clientConfig) {
        ClientConfig.clientConfig = clientConfig;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Integer getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(Integer idleTime) {
        this.idleTime = idleTime;
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
}
