package com.zkdcloud.shadowsocks.client.socks5.context;

/**
 * socks5 服务端返回类型
 *
 * @author zk
 * @since 2018/8/21
 */
public enum RepType {
    /**
     * 成功
     */
    OPERATOR_SUCCESS((byte) 0x00),
    /**
     * 连接失败
     */
    CONNECT_FAIL((byte) 0x01),
    /**
     * 规则不允许连接
     */
    NOT_ALOW_CONNECT((byte) 0x02),
    /**
     * 网络不可达
     */
    NETWORK_FAIL((byte) 0x03),
    /**
     * 主机不可达
     */
    MACHINE_FAIL((byte) 0x04),
    /**
     * 连接被拒
     */
    CONNECT_REFUSE((byte) 0x05),
    /**
     * TTL超时
     */
    CONNECT_TIMEOUT((byte) 0x06),
    /**
     * 不支持的命令
     */
    UN_SUPPORT_CMD((byte) 0x07),
    /**
     * 不支持的地址类型
     */
    UN_SUPPORT_ADDRESS_TYPE((byte) 0x08);

    private byte value;

    RepType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
