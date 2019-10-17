package com.ckj.netproxy;

import android.net.VpnService;
import android.os.ParcelFileDescriptor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;

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
            mInterface = builder.setSession("MyVPNService").addAddress("10.0.2.0", 24).addRoute("0.0.0.0", 0).establish();
            FileInputStream in = new FileInputStream(mInterface.getFileDescriptor());
            FileOutputStream out = new FileOutputStream(mInterface.getFileDescriptor());
            byte[] packet = new byte[MAX_PACKET_SIZE];
            while (true){
                Socket tunnel = new Socket("127.0.0.1",65080);
                mVpnService.protect(tunnel);
                OutputStream serOut=tunnel.getOutputStream();
                InputStream serIn=tunnel.getInputStream();
                while (true) {
                    try {
                        int length = in.read(packet);
                        if (length > 0) {
                            String msg=new String(packet,0,length,"utf-8");
                            System.out.println(msg);
                            serOut.write(packet,0,length);
                            serOut.flush();
                        }
                        length = serIn.read(packet);
                        if (length > 0) {
                            String msg=new String(packet,0,length,"utf-8");
                            out.write(msg.getBytes());
                            out.flush();
                            System.out.println("get msg from servser :"+msg);
                        }
                    } catch (SocketException e){
                        e.printStackTrace();
                        break;
                    } catch(Exception e){
                        e.printStackTrace();
                    }
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

