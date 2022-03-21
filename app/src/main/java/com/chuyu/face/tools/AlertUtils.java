package com.chuyu.face.tools;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;

import com.chuyu.face.R;
import com.chuyu.face.listening.ConfirmDialogOkListening;
import com.chuyu.face.listening.LoginDialogOkListening;


/**
 * Created by Grady on 15/8/21.
 */
public class AlertUtils {

    public static void configDialog(final Context mcontext,final String title, final LoginDialogOkListening loginDialogOkListening){
        if(mcontext==null){
            return;
        }
        final EditText loginPwdEdit = new EditText(mcontext);
        loginPwdEdit.setPadding(50,30,50,30);
        loginPwdEdit.setText("");
        //loginPwdEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext, R.style.AlertDialogCustom);
        builder.setIcon(R.mipmap.net_config);
        builder.setTitle(title);
        builder.setView(loginPwdEdit);
        builder.setCancelable(false);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(loginDialogOkListening!=null){
                    String inputText=loginPwdEdit.getText().toString();
                    if(TextUtils.isEmpty(inputText)){
                        ToastUtil.showTips(mcontext,"请"+title);
                        return;
                    }
                    //inputText=inputText.toUpperCase();
                    loginDialogOkListening.clickSureDialog(inputText);
                }
                dialog.dismiss();

            }
        });


        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    public static void loginDialog(final Context mcontext,final String title, final LoginDialogOkListening loginDialogOkListening){
        if(mcontext==null){
            return;
        }
        final EditText loginPwdEdit = new EditText(mcontext);
        loginPwdEdit.setPadding(50,30,50,30);
        loginPwdEdit.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext, R.style.AlertDialogCustom);
        builder.setIcon(R.drawable.phone);
        builder.setTitle(title);
        builder.setView(loginPwdEdit);
        builder.setCancelable(false);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(loginDialogOkListening!=null){
                    String inputText=loginPwdEdit.getText().toString();
                    if(TextUtils.isEmpty(inputText)){
                        ToastUtil.showTips(mcontext,"请"+title);
                        return;
                    }
                    loginDialogOkListening.clickSureDialog(inputText);
                }
                dialog.dismiss();

            }
        });


        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    public static void confirmDialog(Context mcontext, String title, String message, final ConfirmDialogOkListening confirmDialogOkListening) {
        if(mcontext==null){
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext, R.style.AlertDialogCustom);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(confirmDialogOkListening!=null){
                    confirmDialogOkListening.clickDialogOk();
                }
                dialog.dismiss();
            }
        });


        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }




    public static void alert(Context mcontext,String title, String message) {
        try{
            alert(mcontext,title, message, true, "确定", null);
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    private static void alert(Context mcontext,String title, String message, boolean cancelable, String p, String n) {
        if(mcontext==null){
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext, R.style.AlertDialogCustom);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(cancelable);
        if (!TextUtils.isEmpty(p)) {
            builder.setPositiveButton(p, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        if (!TextUtils.isEmpty(n)) {
            builder.setNegativeButton(n, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        builder.create().show();
    }


}
