package com.chuyu.face.cusview.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chuyu.face.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public abstract class LoadMoreAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    //item类型
    private static final int TYPE_HEADER = 0;//头部
    private static final int TYPE_BASE = 1;//列表item
    private static final int TYPE_EMPTY = 2;//空布局
    private static final int TYPE_LOADMORE = 3;//脚部

    protected BaseActivity mContext;
    protected int mLayoutResId = -1;//列表item布局Id
    protected View vHeader;
    protected View vEmpty;
    protected LoadMoreView vLoadMore;
    protected boolean mLoadMoreEnable;                    //标识是否启用加载更多
    protected boolean mIsHeaderShowInEmpty;               //标识header是否在empty时显示
    protected OnLoadMoreListener mOnLoadMoreListener;     //加载更多执行的回调
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;
    protected List<T> mData = new ArrayList<>();

    public LoadMoreAdapter(Context context, int layoutResId, List<T> mData) {
        this.mContext = (BaseActivity) context;
        this.mLayoutResId = layoutResId;
        this.mData = mData;
    }

    public T getItem(int indexOfData) {
        return mData.get(indexOfData);
    }

    protected abstract void convert(BaseViewHolder holder, T item, int indexOfData);

    @Override
    public int getItemViewType(int position) {
        //如果没有数据
        if (mData == null || mData.isEmpty()) {
            //如果position是0，并且有有头部，头部个数为1
            if (position < getHeaderCount()) {
                return TYPE_HEADER;
            } else {
                return TYPE_EMPTY;
            }
            //如果有数据
        } else {
            if (position < getHeaderCount()) {
                return TYPE_HEADER;
            } else {
                int index = position - getHeaderCount();
                if (index < mData.size()) {
                    return TYPE_BASE;
                } else {
                    return TYPE_LOADMORE;
                }
            }
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder holder = null;
        switch (viewType) {
            case TYPE_HEADER: {
                holder = new BaseViewHolder(vHeader);
                break;
            }
            case TYPE_BASE:
                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                View view = layoutInflater.inflate(mLayoutResId, parent, false);
                holder = new BaseViewHolder(view);
                initItemClick(holder);
                break;
            case TYPE_EMPTY:
                holder = new BaseViewHolder(vEmpty);
                break;
            case TYPE_LOADMORE:
                holder = new BaseViewHolder(vLoadMore);
                break;
        }
        return holder;
    }

    private void initItemClick(final BaseViewHolder holder) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRecyclerViewItemClickListener != null) {
                    mOnRecyclerViewItemClickListener.onItemClick(holder.itemView, holder.getLayoutPosition() - getHeaderCount());
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                break;
            case TYPE_BASE:
                convert(holder, mData.get(position - getHeaderCount()), position - getHeaderCount());
                break;
            case TYPE_EMPTY:
                break;
            case TYPE_LOADMORE:
                //如果有LoadMore布局
                if (mOnLoadMoreListener != null && vLoadMore.isLoadMoreEnable()) {
                    mOnLoadMoreListener.onLoadMore();
                }
                break;
        }
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        int count = getItemCount();
        if ((position == 0 && vHeader != null) || (count == position + 1 && vLoadMore != null)) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();

            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {

                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;

                p.setFullSpan(true);
            }
        }
    }

    @Override
    public int getItemCount() {
        //如果没有数据
        if (mData == null || mData.isEmpty()) {
            //头部+空布局
            int headerCount = getHeaderCount();
            int emptyCount = isEmptyEnable() ? 1 : 0;
            return headerCount + emptyCount;
        } else {
            //如果有数据
            //头布局+数据个数+更多布局
            int headerCount = getHeaderCount();
            int loadMoreCount = mLoadMoreEnable ? 1 : 0;
            return headerCount + mData.size() + loadMoreCount;
        }
    }

    /**
     * 判断当前头部的数量（0或者1）
     *
     * @return
     */
    protected int getHeaderCount() {
        //如果没有数据
        if (mData == null || mData.isEmpty()) {
            //如果有头部，如果在布局为空时候显示头部
            if (isHeaderEnable() && mIsHeaderShowInEmpty) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return isHeaderEnable() ? 1 : 0;
        }
    }


    /**
     * 判断是否有头部
     *
     * @return
     */
    private boolean isHeaderEnable() {
        return vHeader != null;
    }

    /**
     * 空布局
     *
     * @return
     */
    private boolean isEmptyEnable() {
        return vEmpty != null;
    }

    /**
     * 设置头部View
     *
     * @param vHeader
     */
    public void setHeaderView(View vHeader) {
        this.vHeader = vHeader;
    }

    /**
     * 获取头部View
     *
     * @return
     */
    public View getHeaderView() {
        return vHeader;
    }

    /**
     * 设置空布局View
     *
     * @param vEmpty
     */
    public void setEmptyView(View vEmpty) {
        this.vEmpty = vEmpty;
    }

    /**
     * 获取空布局View
     *
     * @return
     */
    public View getEmptyView() {
        return vEmpty;
    }

    /**
     * 设置加载更多的布局
     *
     * @param vLoadMore
     */
    public void setLoadMoreView(LoadMoreView vLoadMore) {
        this.vLoadMore = vLoadMore;
    }

    /**
     * 为LoadMoreView设置背景
     */
    public void setLoadMoreViewBackgroundColor(int colorRes) {
        if (vLoadMore != null) {
            vLoadMore.setBackgroundResource(colorRes);
        }
    }

    /**
     * 为LoadMoreView设置背景
     */
    public void setLoadMoreViewtBackgroundDrawable(int drawableRes) {
        if (vLoadMore != null) {
            vLoadMore.setBackgroundResource(drawableRes);
        }
    }

    /**
     * 设置是否启用加载更多
     */
    public void setLoadMoreEnable(boolean loadMoreEnable) {
        mLoadMoreEnable = loadMoreEnable;
    }

    /**
     * 设置显示empty时，是否显示header
     */
    public void setHeaderShowInEmpty(boolean headerShowInEmpty) {
        mIsHeaderShowInEmpty = headerShowInEmpty;
    }

    /**
     * 获取加载更多监听器
     *
     * @return
     */
    public OnLoadMoreListener getOnLoadMoreListener() {
        return mOnLoadMoreListener;
    }

    /**
     * 设置加载更多时触发的操作
     */
    public void setOnLoadMoreListener(final OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
        vLoadMore.setOnRetryClickListener(new OnRetryClickListener() {
            @Override
            public void OnRetryClick() {
                if (onLoadMoreListener != null) {
                    onLoadMoreListener.onLoadMore();
                }
            }
        });
    }

    /**
     * 显示加载中
     */
    public void showLoadMoreLoading() {
        if (vLoadMore != null) {
            vLoadMore.showLoading();
        }
    }

    /**
     * 加载失败，重试
     */
    public void showLoadMoreRetry() {
        if (vLoadMore != null) {
            vLoadMore.showRetry();
        }
    }

    /**
     * 没有更多，已加载完毕
     */
    public void showLoadMoreEnd() {
        if (vLoadMore != null) {
            vLoadMore.showEnd();
        }
    }


    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        mOnRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    /**
     * RecyclerView的列表点击事件接口
     */
    public interface OnRecyclerViewItemClickListener {

        void onItemClick(View view, int indexOfData);
    }

    /**
     * 记载更多接口
     */
    public interface OnLoadMoreListener {

        void onLoadMore();
    }


    public void notifyDataChanged(int loadMoreNum) {
        if (vLoadMore != null) {
            if (loadMoreNum < 10) {
                showLoadMoreEnd();
            } else {
                showLoadMoreLoading();
            }
        }
        notifyDataSetChanged();
    }
}
