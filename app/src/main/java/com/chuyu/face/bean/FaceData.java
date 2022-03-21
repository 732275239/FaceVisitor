package com.chuyu.face.bean;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class FaceData {
    @Id
    public long id;
    private String name;
    private String idno;
    private byte[] face;



    public String getName() {
        return name;
    }

    public String getIdno() {
        return idno;
    }

    public void setIdno(String idno) {
        this.idno = idno;
    }
    public void setName(String name) {
        this.name = name;
    }

    public byte[] getFace() {
        return face;
    }

    public void setFace(byte[] face) {
        this.face = face;
    }
}
