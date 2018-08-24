package com.zkdcloud.shadowsocks.common.util;

import com.alibaba.fastjson.JSONReader;
import com.zkdcloud.shadowsocks.common.bean.ClientConfig;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static com.zkdcloud.shadowsocks.common.context.ContextConstant.ConfigProperties.CLIENT_CONFIG_FILE;

/**
 * client and server config
 *
 * @author zk
 * @since 2018/8/23
 */
public class ShadowsocksConfigUtil {
    private static ClientConfig clientConfig;

    /**
     * 获取clientConfig
     *
     * @return clientConfig
     */
    public static ClientConfig getClientConfigInstance(){
        if(clientConfig == null){
            clientConfig = getClientConfig();
        }
        return clientConfig;
    }

    /**
     * 重新读取clientConfig
     *
     * @return config of client
     */
    private ClientConfig reloadClientConfig(){
        clientConfig = getClientConfig();
        return clientConfig;
    }
    /**
     * 获取clientConfig
     *
     * @return client
     */
    private synchronized static ClientConfig getClientConfig(){
        ClientConfig clientConfig = null;
        try {
            Path path = Paths.get(ClassLoader.getSystemResource(CLIENT_CONFIG_FILE).toURI());
            clientConfig = new JSONReader(new FileReader(path.toFile())).readObject(ClientConfig.class);
        } catch (URISyntaxException | FileNotFoundException e) {
            e.printStackTrace();
        }
        return clientConfig;
    }
}
