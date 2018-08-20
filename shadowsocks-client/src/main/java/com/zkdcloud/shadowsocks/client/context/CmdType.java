package com.zkdcloud.shadowsocks.client.context;

public enum CmdType {
    /**
     * connect
     */
    CONNECT((byte) 0x01),
    /**
     * bind
     */
    BIND((byte) 0x02),
    /**
     * udp
     */
    UDP((byte) 0x03);
    /**
     * hex value
     */
    private byte value;

    CmdType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
