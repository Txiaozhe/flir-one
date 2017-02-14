package com.flir.flirone.imagehelp;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by txiaozhe on 12/02/2017.
 */

public class MyImage {
    private String isUpLoad;
    private String teleimei; // 手机串号
    private String barcode; //NFC标签
    private Bitmap heatimage; //热成像图片

    private String imagename; //图片名
    private String path;
    private String type;
    private String size;
    private String imagetime; //拍照时间，yyyy-mm-dd hh:MM:ss

    private String maxtemperature; //温度最高值
    private String maxtemplocalx; //温度最高值x坐标
    private String maxtemplocaly; //温度最高值y坐标

    private String meantemperature; //温度平均值

    //private String telelong;
    //private String telelat; //当前经纬度，预留

    public MyImage() {}

    public MyImage(String isUpLoad) {
        this.isUpLoad = isUpLoad;
    }

    //用于保存到数据库
    public MyImage(String isUpLoad, String teleimei, String barcode, String path, String imagename, String imagetime, String maxtemperature, String maxtemplocalx, String maxtemplocaly, String meantemperature) {
        this.isUpLoad = isUpLoad;
        this.teleimei = teleimei;
        this.barcode = barcode;
        this.path = path;
        this.imagename = imagename;
        this.imagetime = imagetime;
        this.maxtemperature = maxtemperature;
        this.maxtemplocalx = maxtemplocalx;
        this.maxtemplocaly = maxtemplocaly;
        this.meantemperature = meantemperature;
    }

    //用于上传图片
    public MyImage(String isUpLoad, String teleimei, String barcode, Bitmap heatimage, String imagename, String imagetime, String maxtemperature, String maxtemplocalx, String maxtemplocaly, String meantemperature) {
        this.isUpLoad = isUpLoad;
        this.teleimei = teleimei;
        this.barcode = barcode;
        this.heatimage = heatimage;
        this.imagename = imagename;
        this.imagetime = imagetime;
        this.maxtemperature = maxtemperature;
        this.maxtemplocalx = maxtemplocalx;
        this.maxtemplocaly = maxtemplocaly;
        this.meantemperature = meantemperature;
    }

    //包含所有属性
    public MyImage(String teleimei, String barcode, Bitmap heatimage, String imagename, String path, String type, String size, String imagetime, String maxtemperature, String maxtemplocalx, String maxtemplocaly, String meantemperature) {
        this.teleimei = teleimei;
        this.barcode = barcode;
        this.heatimage = heatimage;
        this.imagename = imagename;
        this.path = path;
        this.type = type;
        this.size = size;
        this.imagetime = imagetime;
        this.maxtemperature = maxtemperature;
        this.maxtemplocalx = maxtemplocalx;
        this.maxtemplocaly = maxtemplocaly;
        this.meantemperature = meantemperature;
    }

    public String getIsUpLoad() {
        return isUpLoad;
    }

    public void setIsUpLoad(String isUpLoad) {
        this.isUpLoad = isUpLoad;
    }

    public String getTeleimei() {
        return teleimei;
    }

    public void setTeleimei(String teleimei) {
        this.teleimei = teleimei;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Bitmap getHeatimage() {
        return heatimage;
    }

    public void setHeatimage(Bitmap heatimage) {
        this.heatimage = heatimage;
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getImagetime() {
        return imagetime;
    }

    public void setImagetime(String imagetime) {
        this.imagetime = imagetime;
    }

    public String getMaxtemperature() {
        return maxtemperature;
    }

    public void setMaxtemperature(String maxtemperature) {
        this.maxtemperature = maxtemperature;
    }

    public String getMaxtemplocalx() {
        return maxtemplocalx;
    }

    public void setMaxtemplocalx(String maxtemplocalx) {
        this.maxtemplocalx = maxtemplocalx;
    }

    public String getMaxtemplocaly() {
        return maxtemplocaly;
    }

    public void setMaxtemplocaly(String maxtemplocaly) {
        this.maxtemplocaly = maxtemplocaly;
    }

    public String getMeantemperature() {
        return meantemperature;
    }

    public void setMeantemperature(String meantemperature) {
        this.meantemperature = meantemperature;
    }

    @Override
    public String toString() {
        return "MyImage{" +
                "isUpLoad='" + isUpLoad + '\'' +
                ", teleimei='" + teleimei + '\'' +
                ", barcode='" + barcode + '\'' +
                ", heatimage=" + heatimage +
                ", imagename='" + imagename + '\'' +
                ", imagetime='" + imagetime + '\'' +
                ", maxtemperature='" + maxtemperature + '\'' +
                ", maxtemplocalx='" + maxtemplocalx + '\'' +
                ", maxtemplocaly='" + maxtemplocaly + '\'' +
                ", meantemperature='" + meantemperature + '\'' +
                '}';
    }
}
