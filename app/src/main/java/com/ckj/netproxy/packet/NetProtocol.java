package com.ckj.netproxy.packet;

public enum NetProtocol {
    Tcp(6),
    Udp(17);

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    int protocol;
    NetProtocol(int protocol){
        this.protocol=protocol;
    }

    public static NetProtocol getProtocol(int protocol) {
        for(NetProtocol netProtocol:NetProtocol.values()){
            if(netProtocol.protocol==protocol){
                return netProtocol;
            }
        }
        return null;
    }
}
