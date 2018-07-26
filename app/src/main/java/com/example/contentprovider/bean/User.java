package com.example.contentprovider.bean;

/**
 * Created by Administrator on 2018/7/26.
 */

public class User {
    private int _id;
    private String name;
    private int sex;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "User{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                '}';
    }
}
