package com.zkdcloud.shadowsocks.common.util;

import com.alibaba.fastjson.JSONReader;
import com.zkdcloud.shadowsocks.common.bean.ClientConfig;
import com.zkdcloud.shadowsocks.common.bean.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.zkdcloud.shadowsocks.common.context.ContextConstant.ConfigProperties.CLIENT_CONFIG_FILE;
import static com.zkdcloud.shadowsocks.common.context.ContextConstant.ConfigProperties.SERVER_CONFIG_FILE;

/**
 * client and server config
 *
 * @author zk
 * @since 2018/8/23
 */
public class ShadowsocksConfigUtil {
    /**
     * static logger
     */
    private static Logger logger = LoggerFactory.getLogger(ShadowsocksConfigUtil.class);

    /**
     * clientConfig
     */
    private static ClientConfig clientConfig;
    /**
     * serverConfig
     */
    private static ServerConfig serverConfig;

    /**
     * 获取clientConfig
     *
     * @return clientConfig
     */
    public static ClientConfig getClientConfigInstance() {
        if (clientConfig == null) {
            clientConfig = getClientConfig();
        }
        return clientConfig;
    }

    /**
     * 获取serverConfig
     *
     * @return serverConfig
     */
    public static ServerConfig getServerConfigInstance() {
        if (serverConfig == null) {
            serverConfig = getServerConfig();
        }
        return serverConfig;
    }

    /**
     * 获取clientConfig
     *
     * @return client
     */
    private synchronized static ClientConfig getClientConfig() {
        ClientConfig clientConfig = null;
        try {
            Path path = Paths.get(ClassLoader.getSystemResource(CLIENT_CONFIG_FILE).toURI());
            clientConfig = new JSONReader(new FileReader(path.toFile())).readObject(ClientConfig.class);
        } catch (URISyntaxException | FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return clientConfig;
    }

    /**
     * get ServerConfig
     *
     * @return ServerConfig
     */
    private synchronized static ServerConfig getServerConfig() {
        ServerConfig serverConfig = null;
        try {
            Path path = Paths.get(ClassLoader.getSystemResource(SERVER_CONFIG_FILE).toURI());
            serverConfig = new JSONReader(new FileReader(path.toFile())).readObject(ServerConfig.class);
        } catch (URISyntaxException | FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return serverConfig;
    }

    /**
     * 重新读取clientConfig
     *
     * @return config of client
     */
    private ClientConfig reloadClientConfig() {
        clientConfig = getClientConfig();
        return clientConfig;
    }
}
