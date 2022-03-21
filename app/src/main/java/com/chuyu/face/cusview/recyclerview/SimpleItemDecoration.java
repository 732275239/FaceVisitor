package com.chuyu.face.cusview.recyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chuyu.face.tools.DensityUtils;

/**
 * Created by Zoello on 2018/10/26.
 */

public class SimpleItemDecoration extends RecyclerView.ItemDecoration {
    private int space;//间隔
    private boolean topIndex = true;//第一个item的是否需要间隔

    public boolean isTopIndex() {
        return topIndex;
    }

    public SimpleItemDecoration setTopIndex(boolean topIndex) {
        this.topIndex = topIndex;
        return this;
    }

    /**
     * @param context
     * @param spaceDp dp
     */
    public SimpleItemDecoration(Context context, int spaceDp) {
        space = DensityUtils.dp2px(context, space);
        this.space = spaceDp;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        得到数据源总数
        int count = parent.getAdapter().getItemCount();
        //得到当前item的位置
        int index = parent.getChildAdapterPosition(view);

        if (index != count - 1)
            outRect.bottom = space;
        if (index == 0 && topIndex) {
            outRect.top = space;
        }
    }
}