package com.ihealth.myapplication.view;

/**
 * Created by WEE on 2020-04-19
 */
public class ChartBean {

    //坐标点上的数据
    private String date;
    private String value;

    //x坐标
    private  float xAxis;
    //y坐标
    private float yAxis;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public float getxAxis() {
        return xAxis;
    }

    public void setxAxis(float xAxis) {
        this.xAxis = xAxis;
    }

    public float getyAxis() {
        return yAxis;
    }

    public void setyAxis(float yAxis) {
        this.yAxis = yAxis;
    }

}
