package com.ckj.netproxy;

import android.net.VpnService;

import org.greenrobot.eventbus.EventBus;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

public class VpnServer extends Thread{
    EventBus eventBus;
    VpnService mVpnService;
    ServerSocket serChannel;
    int MAX_PACKET_SIZE=Short.MAX_VALUE;
    String response="hello client,i am server !";
    ByteBuffer resppacke;
    CountDownLatch latch;
    public VpnServer(VpnService service, CountDownLatch latch){
        eventBus=EventBus.getDefault();
        this.mVpnService=service;
        this.latch=latch;
        this.resppacke=ByteBuffer.wrap(response.getBytes());
    }

    @Override
    public void run() {
        try {
            serChannel=new ServerSocket(65080);
            byte[] packet=new byte[MAX_PACKET_SIZE];
            latch.countDown();
            try{
                while (true){
                    Socket channel=serChannel.accept();
                    mVpnService.protect(channel);
                    InputStream in=channel.getInputStream();
                    OutputStream out=channel.getOutputStream();
                    while (true) {
                        try{
                            int length = in.read(packet);
                            String msg=new String(packet,0,length,"utf-8");
                            eventBus.post(msg);
                            System.out.println(msg);
                            if (length > 0) {
                                out.write(response.getBytes());
                                out.flush();
                            }
                        }catch (SocketException e){
                            e.printStackTrace();
                            break;
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
