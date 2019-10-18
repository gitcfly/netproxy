package com.ckj.netproxy.packet;

import java.io.Serializable;
import java.util.Arrays;

public class IpPacket implements Serializable {
    public int protocol;
    public String srcHost;
    public String destHost;
    public byte[] data;

    public IpPacket(){}

    public IpPacket(int protocol, String srcHost, String destHost, byte[] data) {
        this.protocol = protocol;
        this.srcHost = srcHost;
        this.destHost = destHost;
        this.data = data;
    }

    public static IpPacket analyze(byte[] data) {
        return analyze(data,0,data.length);
    }

    public static IpPacket analyze(byte[] data,int offset,int length) {
        if(data==null||data.length==0){
            return null;
        }
        IpPacket ipPacket=new IpPacket();
        int ipHeadLength=Tools.getIntFormByte(data[0],3,7);
        if(ipHeadLength==0){
            ipHeadLength=5;
        }
        ipHeadLength=ipHeadLength*4;
        byte protocol=data[9];
        ipPacket.protocol=protocol;
        int allLength=Tools.getIntFromBytes(data[2],data[3]);
        ipPacket.srcHost= new StringBuilder().append(Tools.unInt(data[12])+".")
                .append(Tools.unInt(data[13])+".")
                .append(Tools.unInt(data[14])+".")
                .append(Tools.unInt(data[15]))
                .toString();
        ipPacket.destHost= new StringBuffer()
                .append(Tools.unInt(data[16])+".")
                .append(Tools.unInt(data[17])+".")
                .append(Tools.unInt(data[18])+".")
                .append(Tools.unInt(data[19]))
                .toString();
        ipPacket.data=Tools.copyBytes(data,ipHeadLength,allLength);
        return ipPacket;
    }
}
