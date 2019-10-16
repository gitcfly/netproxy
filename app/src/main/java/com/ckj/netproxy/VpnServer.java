package com.ckj.netproxy;

import android.net.VpnService;

import org.greenrobot.eventbus.EventBus;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class VpnServer extends Thread{
    EventBus eventBus;
    VpnService mVpnService;
    ServerSocketChannel serChannel;
    int MAX_PACKET_SIZE=Short.MAX_VALUE;
    String response="hello client,i am server !";
    ByteBuffer resppacke;
    public VpnServer(VpnService service){
        eventBus=EventBus.getDefault();
        this.mVpnService=service;
        this.resppacke=ByteBuffer.wrap(response.getBytes());
    }

    @Override
    public void run() {
        try {
            serChannel=ServerSocketChannel.open();
            serChannel.socket().bind(new InetSocketAddress(65080));
            mVpnService.protect(65080);
            ByteBuffer packet = ByteBuffer.allocate(MAX_PACKET_SIZE);
            SocketChannel channel=serChannel.accept();
            while (true) {
                try{
                    int length = channel.read(packet);
                    String msg=new String(packet.array(),0,length,"utf-8");
                    eventBus.post(msg);
                    System.out.println(msg);
                    if (length > 0) {
                        packet.limit(length);
                        channel.write(resppacke);
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
