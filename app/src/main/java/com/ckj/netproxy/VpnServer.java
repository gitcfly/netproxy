package com.ckj.netproxy;

import android.net.VpnService;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class VpnServer extends Thread{
    VpnService mVpnService;
    DatagramChannel serChannel;
    int MAX_PACKET_SIZE=Short.MAX_VALUE;
    String response="hello client,i am server !";
    ByteBuffer resppacke=ByteBuffer.wrap(response.getBytes());
    @Override
    public void run() {
        try {
            serChannel=DatagramChannel.open();
            serChannel.socket().bind(new InetSocketAddress(65080));
            mVpnService.protect(serChannel.socket());
            ByteBuffer packet = ByteBuffer.allocate(MAX_PACKET_SIZE);
            while (true) {
                try{
                    int length = serChannel.read(packet);
                    String msg=new String(packet.array(),0,length);
                    System.out.println(msg);
                    if (length > 0) {
                        // Write the outgoing packet to the tunnel.
                        packet.limit(length);
                        serChannel.write(resppacke);
                        packet.clear();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
