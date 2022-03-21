/*
 * Copyright (c) 2015 [1076559197@qq.com | tchen0707@gmail.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chuyu.face.cusview.loading;

import android.content.Context;
import android.view.View;

/**
 *名称：Loading布局方法调用接口
 *创建人：xw
 *创建时间：2017/3/17 0017 下午 4:29
 *详细说明：
 */
public interface IVaryViewHelper {

	/**
     * 获取当前显示的布局，可能是原始布局view，也可能是Loading布局
	 * @return
     */
	public abstract View getCurrentLayout();

	/**
     * 重置布局，去掉Loading布局，显示原始布局
	 */
	public abstract void restoreView();

	/**
     * 显示指定布局（此操作为框架调用，一般不自己用）
	 * @param view
	 */
	public abstract void showLayout(View view);

	/**
     * 用当前布局加载指定资源id
	 * @param layoutId
     * @return
     */
	public abstract View inflate(int layoutId);

	public abstract Context getContext();

	/**
     * 获取原始布局view
	 * @return
     */
	public abstract View getView();

}