package com.ckj.netproxy;

import android.net.VpnService;

import com.ckj.netproxy.packet.IpPacket;
import com.ckj.netproxy.packet.NetProtocol;
import com.ckj.netproxy.packet.TcpPacket;

import org.greenrobot.eventbus.EventBus;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ProxyTask implements Runnable {
    VpnService vpnService;
    public OutputStream output;
    byte[] request;
    int length;
    byte[] response=new byte[65535];
    public ProxyTask(VpnService vpnService,OutputStream output, byte[] request, int length){
        this.output=output;
        this.request=request;
        this.vpnService=vpnService;
        this.length=length;
    }

    @Override
    public void run() {
        try{
            IpPacket ipPacket=IpPacket.analyze(request,0,length);
            if(ipPacket.protocol== NetProtocol.Tcp.getProtocol()){
                TcpPacket tcpPacket=new TcpPacket(ipPacket);
                String requstStr=new String(tcpPacket.data,"utf-8");
                EventBus.getDefault().post("requst:\n"+requstStr+"\n");
                Socket socket= new Socket();
                try {
                    vpnService.protect(socket);
                    socket.connect(new InetSocketAddress(tcpPacket.desHost,tcpPacket.desPort), 2000);
                    socket.setSoTimeout(5000);
                    OutputStream netOut=socket.getOutputStream();
                    netOut.write(tcpPacket.data);
                    netOut.flush();
                    InputStream netIn=socket.getInputStream();
                    int netLength= netIn.read(response);
                    output.write(response,0,netLength);
                    output.flush();
                    socket.close();
                    String responseStr=new String(response,0,netLength,"utf-8");
                    EventBus.getDefault().post("response:\n"+responseStr+"\n\n");
                }catch (Exception e){
                    e.printStackTrace();
                    socket.close();
                }
            }else {
                output.write("no support prototol ! ".getBytes());
                output.flush();
                EventBus.getDefault().post("no suport prototol !:\n\n");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
