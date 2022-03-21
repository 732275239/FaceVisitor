package com.chuyu.face.listening;

import android.view.View;



import wendu.dsbridge.CompletionHandler;

/**
 * Created by Administrator on 2018/9/27 0027.
 */

public interface CoreWebViewListening {

    public void webTitle(String title);

    public void openLoading(String msg);
    public void openBgLoading(String msg);
    public void closeBgLoading();
    public void closeLoading();
    public void loadUrl(String url);//在当前页面加载一个url
    public void refreshWin();//刷新当前页面
    public void showError();
    public void openWin(String url,String title,boolean hasLoad);
    public void fishOpenWin(String url,String title,boolean hasLoad,int size);//关闭前面几个页面，并开新页面
    public void showRightTextBtn(String title, View.OnClickListener onClickListener);
    public void showRightImgBtn(String imgUrl, View.OnClickListener onClickListener);

    public void openUploadImg(UploadImgHandleListening uploadImgHandleListening);
    public void openRoundImg(UpRoundHandleListening upRoundHandleListening);

    public void openAlipay(String paramReq,PayCallBackListening payCallBackListening);
    public void openLocation(CompletionHandler<String> completionHandler);
    public void WXShare(String url,String title,String descroption,String imgurl);

}
