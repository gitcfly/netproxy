package com.ckj.netproxy;

import android.net.VpnService;
import android.os.ParcelFileDescriptor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class VpnClient extends Thread {
    int MAX_PACKET_SIZE=Short.MAX_VALUE;
    VpnService mVpnService;
    VpnService.Builder builder;
    private ParcelFileDescriptor mInterface;

    public VpnClient(VpnService service, VpnService.Builder builder) {
        this.mVpnService=service;
        this.builder=builder;
    }

    @Override
    public void run() {
        try {
            //a. Configure the TUN and get the interface.
            mInterface = builder.setSession("MyVPNService")
                    .addAddress("192.168.1.1", 0)
                    .addRoute("0.0.0.0", 0).establish();
            //b. Packets to be sent are queued in this input stream.
            FileInputStream in = new FileInputStream(
                    mInterface.getFileDescriptor());
            //b. Packets received need to be written to this output stream.
            FileOutputStream out = new FileOutputStream(
                    mInterface.getFileDescriptor());
            //c. The UDP channel can be used to pass/get ip package to/from server
            DatagramChannel tunnel = DatagramChannel.open();
            // Connect to the server, localhost is used for demonstration only.
            tunnel.connect(new InetSocketAddress("127.0.0.1", 65080));
            //d. Protect this socket, so package send by it will not be feedback to the vpn service.
            mVpnService.protect(tunnel.socket());
            ByteBuffer packet = ByteBuffer.allocate(MAX_PACKET_SIZE);
            //e. Use a loop to pass packets.
            while (true) {
                try {
                    int length = in.read(packet.array());
                    if (length > 0) {
                        packet.limit(length);
                        tunnel.write(packet);
                        packet.clear();
                    }
                    length = tunnel.read(packet);
                    if (length > 0) {
                        out.write(packet.array(), 0, length);
                        packet.clear();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (mInterface != null) {
                    mInterface.close();
                    mInterface = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

