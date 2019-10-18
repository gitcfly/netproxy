package com.ckj.netproxy;

import org.greenrobot.eventbus.EventBus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Event {

    public static void sendEvent(Object envent){
        if(envent instanceof String ){
            if(Config.logprex!=null&&!Config.logprex.equals("")){
                String estr=(String) envent;
                Pattern p=Pattern.compile(Config.logprex);
                Matcher matcher= p.matcher(estr);
                if (matcher.find()) {
                    EventBus.getDefault().post(envent);
                }
            }else {
                EventBus.getDefault().post(envent);
            }
        }else {
            EventBus.getDefault().post(envent);
        }
    }
}
