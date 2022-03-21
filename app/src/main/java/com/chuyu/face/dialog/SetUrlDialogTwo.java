package com.chuyu.face.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chuyu.face.R;
import com.chuyu.face.tools.ToastUtil;
import com.chuyu.face.utils.SoftInputUtils;


public class SetUrlDialogTwo {
    private int inOrOut = 1;
    public String[] split;
    public int inOrOut1;

    public SetUrlDialogTwo(String url, int inOrOut) {
        if (!url.isEmpty()) {
            String text = url;
            text = text.substring(7, url.length() - 1);
            split = text.split(":");
        }
        this.inOrOut1 = inOrOut;
    }

    public void show(Activity mContext) {
        Dialog dialog = new Dialog(mContext, R.style.dialog_fullscreena);
        dialog.setContentView(R.layout.seturl_dialog_layout);
        dialog.setCanceledOnTouchOutside(true);
        TextView okBtn = dialog.findViewById(R.id.okBtn);
        EditText ip = dialog.findViewById(R.id.ip);
        ip.setText(split[0]);
        EditText code = dialog.findViewById(R.id.code);
        code.setText(split[1]);
        RadioGroup radio = dialog.findViewById(R.id.radio);
        if (inOrOut1 == 1) {
            radio.check(R.id.in);
        } else {
            radio.check(R.id.out);
        }
        radio.setOnCheckedChangeListener((group, checkedId) -> {
            switch (group.getCheckedRadioButtonId()) {
                case R.id.in:
                    inOrOut = 1;
                    break;
                case R.id.out:
                    inOrOut = 2;
                    break;
                default:
                    break;
            }
        });
        okBtn.setOnClickListener(v -> {
            String ips = ip.getText().toString().trim();
            String codes = code.getText().toString().trim();
            if (ips.isEmpty() || codes.isEmpty()) {
                ToastUtil.showLongTips(mContext, "请输入完整参数");
            } else {
                dialog.dismiss();
                SoftInputUtils.hideSoftInput(mContext);
                if (yesOnclickListener != null) {
                    String aaa = "http://" + ips + ":" + codes + "/";
                    yesOnclickListener.onYesClick(aaa, inOrOut);
                }
            }
        });
        dialog.show();

    }

    public interface onYesOnclickListener {
        void onYesClick(String s, int inOrOut);
    }

    private onYesOnclickListener yesOnclickListener;

    public void setYesOnclickListener(onYesOnclickListener onYesOnclickListener) {

        this.yesOnclickListener = onYesOnclickListener;
    }

    public interface onDisdiss {
        void onDis();
    }

    private onDisdiss onDisdiss;

    public void setOnDisdiss(onDisdiss onDisdiss) {

        this.onDisdiss = onDisdiss;
    }


}
