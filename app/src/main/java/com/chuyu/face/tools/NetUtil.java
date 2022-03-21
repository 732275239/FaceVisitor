package com.chuyu.face.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Administrator on 2018/9/20 0020.
 */

public class NetUtil {


    public static boolean isNetwork(Context context) {
        try{
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if (mNetworkInfo != null) {
                    return mNetworkInfo.isAvailable();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkSocket(String ips){
        Socket skt = new Socket();
        try{
            final String[] ipArray=ips.split(":");
            skt.connect(new InetSocketAddress(ipArray[0], Integer.parseInt(ipArray[1])),
                    5000);
            return skt.isConnected();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (!skt.isClosed()) {
                try {
                    skt.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }



    public static boolean ping(String ip) {
        boolean result = false;
        try {
            //ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result=true;
            } else {
                result=false;
            }
        } catch (Exception e) {
            result=false;
        }
        return result;

    }
}
