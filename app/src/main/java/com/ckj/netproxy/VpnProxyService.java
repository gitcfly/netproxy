package com.ckj.netproxy;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.CountDownLatch;

public class VpnProxyService extends VpnService {

    private ParcelFileDescriptor mInterface;
    Builder builder = new Builder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            CountDownLatch latch=new CountDownLatch(1);
            new VpnServer(VpnProxyService.this,latch).start();
            latch.await();
            Thread.sleep(1000);
            new VpnClient(VpnProxyService.this,builder).start();
        }catch (Exception e){
            e.printStackTrace();
        }
        return START_STICKY;
    }

}