package com.leonyr.smartipaddemo.home;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import java.util.Comparator;

@Keep
public class HomeModel implements Comparable<HomeModel> {

    private String name;
    private int pos;
    private String funcCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getFuncCode() {
        return funcCode;
    }

    public void setFuncCode(String funcCode) {
        this.funcCode = funcCode;
    }

    @Override
    public String toString() {
        return "[name:" + name + ", pos:" + pos + ", funcCode:" + funcCode + "]";
    }

    @Override
    public int compareTo(@NonNull HomeModel o) {
        return this.pos - o.pos;
    }

}
