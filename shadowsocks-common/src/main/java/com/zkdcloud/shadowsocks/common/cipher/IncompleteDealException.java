package com.zkdcloud.shadowsocks.common.cipher;

/**
 * description
 *
 * @author zk
 * @since 2019/10/24
 */
public class IncompleteDealException extends Exception{
    private byte[] dealBytes;
    private int dealLength;

    public IncompleteDealException(){

    }
    public IncompleteDealException(byte[] dealBytes, int dealLength){
        this.dealBytes = dealBytes;
        this.dealLength = dealLength;
    }
    public byte[] getDealBytes() {
        return dealBytes;
    }

    public void setDealBytes(byte[] dealBytes) {
        this.dealBytes = dealBytes;
    }

    public int getDealLength() {
        return dealLength;
    }

    public void setDealLength(int dealLength) {
        this.dealLength = dealLength;
    }
}
