package com.chuyu.face.bean;


import com.chuyu.face.base.ApplicationContext;

import java.util.List;

import io.objectbox.Box;

public class BlogDao {
    public static void insertOrUpdateBlogItem(FaceData newItem) {
        getBlogItemBox().put(newItem);
    }

    public static List<FaceData> getAll() {
        return getBlogItemBox().getAll();
    }

    public static void delete() {
        getBlogItemBox().removeAll();
    }

    public static List<FaceData> getbyid(String idno) {
        List<FaceData> faceData = getBlogItemBox().query().equal(FaceData_.idno, idno).build().find();
        if (faceData.size() != 0) {
            return faceData;
        }
        return null;
    }

    public static Box<FaceData> getBlogItemBox() {
        return ApplicationContext.getInstance().getBoxStore().boxFor(FaceData.class);
    }


}
