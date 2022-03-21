package com.chuyu.face.tools.EventBus;

/**
 * Created by Zoello on 2018/10/25.
 */

public class  EventCenter<T> {
    /**
     * reserved data
     */
    private T data;

    /**
     * 这个code要与其他事件的code保持不同
     */
    private int eventCode = -1;

    /**
     *
     * @param eventCode eventCode不要自己定义，需要在EventCode中定义，以做区分
     */
    public EventCenter(int eventCode) {
        this(eventCode, null);
    }

    /**
     *
     * @param eventCode eventCode不要自己定义，需要在EventCode中定义，以做区分
     * @param data
     */
    public EventCenter(int eventCode, T data) {
        this.eventCode = eventCode;
        this.data = data;
    }

    /**
     * 返回EventBus当前的Code
     *
     * @return
     */
    public int getEventCode() {
        return this.eventCode;
    }

    /**
     * get event reserved data
     *
     * @return
     */
    public T getData() {
        return this.data;
    }
}
