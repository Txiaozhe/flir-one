package com.flir.flirone.imagehelp;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by txiaozhe on 12/02/2017.
 */

public class MyImage {
    private String name;
    private String path;
    private String type;
    private String size;
    private String time;

    private String maxTemp;
    private String maxTempX;
    private String maxTempY;

    private String averageTemp;

    public MyImage(String name) {
        this.name = name;
    }

    public MyImage() {}

    public MyImage(String name, String path, String type, String size, String time, String maxTemp, String maxTempX, String maxTempY, String averageTemp) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.size = size;
        this.time = time;
        this.maxTemp = maxTemp;
        this.maxTempX = maxTempX;
        this.maxTempY = maxTempY;
        this.averageTemp = averageTemp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    public String getMaxTempX() {
        return maxTempX;
    }

    public void setMaxTempX(String maxTempX) {
        this.maxTempX = maxTempX;
    }

    public String getMaxTempY() {
        return maxTempY;
    }

    public void setMaxTempY(String maxTempY) {
        this.maxTempY = maxTempY;
    }

    public String getAverageTemp() {
        return averageTemp;
    }

    public void setAverageTemp(String averageTemp) {
        this.averageTemp = averageTemp;
    }


}
