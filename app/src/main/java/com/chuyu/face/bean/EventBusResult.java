package com.chuyu.face.bean;

/**
 * Created by Administrator on 2018/8/4 0004.
 */

public class EventBusResult implements java.io.Serializable{

    private int eventCode;
    private String eventMsg;
    private boolean isLine;

    public EventBusResult(){

    }

    public EventBusResult(int eventCode,String eventMsg){
        this.eventCode=eventCode;
        this.eventMsg=eventMsg;
    }

    public int getEventCode() {
        return eventCode;
    }

    public void setEventCode(int eventCode) {
        this.eventCode = eventCode;
    }

    public String getEventMsg() {
        return eventMsg;
    }

    public void setEventMsg(String eventMsg) {
        this.eventMsg = eventMsg;
    }

    public boolean isLine() {
        return isLine;
    }

    public void setLine(boolean line) {
        isLine = line;
    }
}
