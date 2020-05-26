package com.swufe.firstapp;

//每条记录对象类
public class RateItem {

    private int id;
    private String curName;
    private String curRate;


    //构造函数
    public  RateItem(){
        curName = "";
        curRate = "";

    }
    public RateItem(String curName, String curRate) {
        this.curName = curName;
        this.curRate = curRate;
    }

    //获得属性
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurName() {
        return curName;
    }

    public void setCurName(String curName) {
        this.curName = curName;
    }

    public String getCurRate() {
        return curRate;
    }

    public void setCurRate(String curRate) {
        this.curRate = curRate;
    }
}
