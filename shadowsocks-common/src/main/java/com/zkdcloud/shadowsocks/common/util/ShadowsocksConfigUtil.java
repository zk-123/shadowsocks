package com.zkdcloud.shadowsocks.common.util;

import com.alibaba.fastjson.JSONReader;
import com.zkdcloud.shadowsocks.common.config.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.zkdcloud.shadowsocks.common.context.ContextConstant.ConfigProperties.*;

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
     * 获取clientConfig
     *
     * @return clientConfig
     */
    public static ClientConfig getClientConfigInstance() {
        if (clientConfig == null) {
            clientConfig = getClientConfig();

            //validate timeout
            if (clientConfig.getTimeout() == null || clientConfig.getTimeout() <= 0) {
                logger.warn("timeout is set invalid, set default :{}", DEFAULT_TIMEOUT_TIME);
                clientConfig.setTimeout(DEFAULT_TIMEOUT_TIME);
            }
        }
        return clientConfig;
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
     * 重新读取clientConfig
     *
     * @return config of client
     */
    private ClientConfig reloadClientConfig() {
        clientConfig = getClientConfig();
        return clientConfig;
    }
}
