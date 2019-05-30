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
    private String local_address;
    /**
     * localPort
     */
    private short local_port;
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

    public String getLocal_address() {
        return local_address;
    }

    public void setLocal_address(String local_address) {
        this.local_address = local_address;
    }

    public short getLocal_port() {
        return local_port;
    }

    public void setLocal_port(short local_port) {
        this.local_port = local_port;
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
}
