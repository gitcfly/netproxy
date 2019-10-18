package com.ckj.netproxy;

public class Test {

    public static void  main(String ...args){
        byte val=-12;
        int valtt=val;
        if(val<0){
            valtt = val & 0xff;
        }
        String valt=Integer.toBinaryString(valtt);
        int ival=Integer.valueOf(valt,2);
        System.out.println(ival);
    }
}
