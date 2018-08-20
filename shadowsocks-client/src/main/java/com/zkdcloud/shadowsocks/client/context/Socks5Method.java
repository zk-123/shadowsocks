package com.zkdcloud.shadowsocks.client.context;

/**
 * socks5 approve method
 *
 * @author zk
 * @since 2018/8/20
 */
public enum Socks5Method {
    /**
     * not approve
     */
    NO_APPROVE((byte) 0x00),
    /**
     * gssapi
     */
    GSSAPI((byte) 0x01),
    /**
     * un support
     */
    NO_ACCEPT((byte) 0xff);
    /**
     * value
     */
    private byte value;

    Socks5Method(byte value) {
        this.value = value;
    }

    /**
     * get value
     *
     * @return the real value of method
     */
    public byte getValue() {
        return value;
    }
}
