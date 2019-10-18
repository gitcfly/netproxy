package com.ckj.netproxy.packet;

public class Tools {

    public static int unInt(byte val){
        int re=val;
        if(val<0){
            re = val & 0xff;
        }
        return re;
    }

    public static int getIntFormByte(byte val,int from,int to){
        int valtt=val;
        if(val<0){
            valtt = val & 0xff;
        }
        String valt=Integer.toBinaryString(valtt);
        valt=valt.substring(from,to);
        int result=Integer.valueOf(valt,2);
        return result;
    }

    public static int getIntFromBytes(byte... vals){
        if(vals==null||vals.length==0){
            return 0;
        }
        String valt="";
        for(int val:vals){
            if(val<0){
                val = val & 0xff;
            }
            valt+=Integer.toBinaryString(val);
        }
        int result=Integer.valueOf(valt,2);
        return result;
    }

    public static byte[] copyBytes(byte[] data,int from,int to){
        byte[] result=new byte[to-from];
        for(int i=from;i<to;i++){
            result[i-from]=data[i];
        }
        return result;
    }
}
