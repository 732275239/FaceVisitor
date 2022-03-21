package com.chuyu.face.http;

import com.chuyu.face.base.URLs;
import com.chuyu.face.http.nohttp.NoHttpController;
import com.chuyu.face.http.nohttp.NoHttpDownLoadListener;

import java.util.ArrayList;


/**
 * 请求联网参数处理
 *
 * @author xx
 */
public class CallNet {

    /**
     * 使用NoHttp请求网络（请求网络获取数据、上传文件）
     *
     * @param <T>
     * @param param
     */
    public static <T> void callNetNohttp(ParamBean param, ConnectTask callback) {
        NoHttpController.getRequestInstance().requestNet(URLs.HOST_URL + param.getAct(), param.getParams(), callback, 1);
    }
    /**
     * 没有loading
     * 使用NoHttp请求网络（请求网络获取数据、上传文件）
     *
     * @param <T>
     * @param param
     */
    public static <T> void callNetNohttp(ParamBean param, NoLoadingConnectTask callback) {
        NoHttpController.getRequestInstance().requestNet(URLs.HOST_URL + param.getAct(), param.getParams(), callback, 1);
    }


    /**
     * 使用NoHttp下载文件
     *
     * @param downFileBean     （下载地址、保存路径的集合）
     * @param downloadListener （下载监听）
     */
    public static void downNetNohttp(ArrayList<DownFileBean> downFileBean, NoHttpDownLoadListener downloadListener) {
        NoHttpController.getDownloadInstance().downLoadNet(downFileBean, downloadListener);
    }

}
