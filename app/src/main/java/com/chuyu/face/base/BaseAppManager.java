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

//          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
//                              _oo0oo_
//                             o8888888o
//                             88" . "88
//                             (| -_- |)
//                             0\  =  /0
//                           ___/`---‘\___
//                        .' \\\|     |// '.
//                       / \\\|||  :  |||// \\
//                      / _ ||||| -:- |||||- \\
//                      | |  \\\\  -  /// |   |
//                      | \_|  ''\---/''  |_/ |
//                      \  .-\__  '-'  __/-.  /
//                    ___'. .'  /--.--\  '. .'___
//                 ."" '<  '.___\_<|>_/___.' >'  "".
//                | | : '-  \'.;'\ _ /';.'/ - ' : | |
//                \  \ '_.   \_ __\ /__ _/   .-' /  /
//            ====='-.____'.___ \_____/___.-'____.-'=====
//                              '=---='
//
//
//          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
//
//                  佛祖保佑                 永无BUG

package com.chuyu.face.base;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

/**
 * 名称：Activity控制器
 * 详细说明：
 */
public class BaseAppManager {

    private static final String TAG = BaseAppManager.class.getSimpleName();

    private static BaseAppManager instance = null;
    private static List<Activity> mActivities = new LinkedList<Activity>();

    private BaseAppManager() {

    }

    public static BaseAppManager getInstance() {
        if (null == instance) {
            synchronized (BaseAppManager.class) {
                if (null == instance) {
                    instance = new BaseAppManager();
                }
            }
        }
        return instance;
    }

    public int size() {
        return mActivities.size();
    }

    public synchronized Activity getForwardActivity() {
        return size() > 0 ? mActivities.get(size() - 1) : null;
    }

    /**
     * 加入当前Activity到集合中
     *
     * @param activity
     */
    public synchronized void addActivity(Activity activity) {
        mActivities.add(activity);
    }

    /**
     * 从集合中移除当前Activity
     *
     * @param activity
     */
    public synchronized void removeActivity(Activity activity) {
        if (mActivities.contains(activity)) {
            mActivities.remove(activity);
        }
    }

    public synchronized void clear() {
        try {
            for (int i = mActivities.size() - 1; i > -1; i--) {
                Activity activity = mActivities.get(i);
                removeActivity(activity);
                activity.finish();
                i = mActivities.size();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 结束所有Activity
     */
    public synchronized void clearAll() {
        try {
            for (int i = 0; i < mActivities.size(); i++) {
                if (null!=mActivities.get(i)){
                    mActivities.get(i).finish();
                }
            }
            mActivities.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public synchronized void clear(int size) {
//        mActivities.size() - 2  有两个不能关 一个是mainactivity 一个是当前的activity
        try {
            size = size > mActivities.size() - 2 ? mActivities.size() - 2 : size;
            for (int i = 0; i < size; i++) {
                Activity activity = mActivities.get(mActivities.size() - 2);
                removeActivity(activity);
                activity.finish();
            }
//            for (int i = mActivities.size() - 1; i < (mActivities.size() - 1); i--) {
//                Activity activity = mActivities.get(i);
//                removeActivity(activity);
//                activity.finish();

//                mActivities.size() = mActivities.size()-1;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void clearToTop() {
        try {
            for (int i = mActivities.size() - 2; i > -1; i--) {
                Activity activity = mActivities.get(i);
                removeActivity(activity);
                activity.finish();
                i = mActivities.size() - 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized void backMain() {
        try {
            if (mActivities.size() <= 1) {
                return;
            }
            for (int i = mActivities.size() - 1; i > -1; i--) {
                Activity activity = mActivities.get(i);
                removeActivity(activity);
                activity.finish();
                i = mActivities.size();
                if (i == 1) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
