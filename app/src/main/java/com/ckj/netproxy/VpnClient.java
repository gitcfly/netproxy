package com.ckj.netproxy;

import android.net.VpnService;
import android.os.ParcelFileDescriptor;

import com.ckj.netproxy.packet.IpPacket;
import com.ckj.netproxy.packet.NetProtocol;
import com.ckj.netproxy.packet.TcpPacket;
import com.ckj.netproxy.packet.Tools;

import org.greenrobot.eventbus.EventBus;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class VpnClient extends Thread {
    ExecutorService executor= Executors.newFixedThreadPool(5);
    int MAX_PACKET_SIZE=65535;
    public VpnService mVpnService;
    public VpnService.Builder builder;
    private ParcelFileDescriptor mInterface;

    public VpnClient(VpnService service, VpnService.Builder builder) {
        this.mVpnService=service;
        this.builder=builder;
    }

    @Override
    public void run() {
        try {
            Socket socket= new Socket();
            socket.connect(new InetSocketAddress("10.92.239.198",65081), 2000);
            socket.setSoTimeout(5000);
            OutputStream netOut=socket.getOutputStream();
            byte[] packet = new byte[MAX_PACKET_SIZE];
            DatagramChannel channel=DatagramChannel.open();
            channel.connect(new InetSocketAddress("10.92.239.198",65082));
            mInterface = builder.setSession("MyVPNService")
                    .addAddress("192.168.0.1", 24)
                    .addRoute("0.0.0.0", 0)
                    .establish();
            FileInputStream in = new FileInputStream(mInterface.getFileDescriptor());
            FileOutputStream out = new FileOutputStream(mInterface.getFileDescriptor());
            mVpnService.protect(socket);
            ByteBuffer byteBuffer=ByteBuffer.allocate(1203);
            byteBuffer.put("hello server!".getBytes("utf-8"));
            mVpnService.protect(channel.socket());
            while (true){
                try {
                    int length = in.read(packet);
                    if (length > 0) {
                        System.out.println("read from victical net:\n"+new String(packet,0,length,"utf-8"));
                        netOut.write(byteBuffer);
//                        byte[] data=Tools.copyBytes(packet,0,length);
//                        ProxyTask task=new ProxyTask(mVpnService,out,data,length);
//                        executor.execute(task);
//                        netOut.write("sdfsfsfadfsf".getBytes());
//                        netOut.flush();
                    }
                } catch (SocketException e){
                    e.printStackTrace();
                    Event.sendEvent(e.getMessage());
                    break;
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Event.sendEvent(e.getMessage());
        } finally {
            try {
                if (mInterface != null) {
                    mInterface.close();
                    mInterface = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Event.sendEvent(e.getMessage());
            }
            System.out.println("退出VPN模式");
        }
    }

}

