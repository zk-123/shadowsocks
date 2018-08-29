package com.zkdcloud.shadowsocks.common.bean;

/**
 * client config (usually is standard shadowsocks 'config.json')
 *
 * @author zk
 * @since 2018/8/23
 */
public class ClientConfig {
    /**
     * server ip
     */
    private String server;
    /**
     * server port
     */
    private short server_port;
    /**
     * password
     */
    private String password;
    /**
     * method
     */
    private String method;
    /**
     * plugin
     */
    private String plugin;
    /**
     * plugin options
     */
    private String plugin_opts;
    /**
     * remarks
     */
    private String remarks;
    /**
     * timeout seconds
     */
    private Long timeout;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public short getServer_port() {
        return server_port;
    }

    public void setServer_port(short server_port) {
        this.server_port = server_port;
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

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public String getPlugin_opts() {
        return plugin_opts;
    }

    public void setPlugin_opts(String plugin_opts) {
        this.plugin_opts = plugin_opts;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }
}
