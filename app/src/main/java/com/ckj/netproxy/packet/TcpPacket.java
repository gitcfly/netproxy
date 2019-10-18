package com.ckj.netproxy.packet;

import android.widget.Toast;

import java.io.Serializable;
import java.util.Arrays;

public class TcpPacket implements Serializable {
    public IpPacket ipPacket;
    public String srcHost;
    public String desHost;
    public int srcPort;
    public int desPort;
    public byte[] data;

    public TcpPacket(IpPacket ipPacket){
        this.ipPacket=ipPacket;
        this.srcHost=ipPacket.srcHost;
        this.desHost=ipPacket.destHost;
        srcPort=Tools.getIntFromBytes(ipPacket.data[0],ipPacket.data[1]);
        desPort= Tools.getIntFromBytes(ipPacket.data[2],ipPacket.data[3]);
        this.data= Tools.copyBytes(ipPacket.data,24,ipPacket.data.length);
    }
}


