package com.flir.flirone.imagehelp;

/**
 * Created by txiaozhe on 16/02/2017.
 */

public class ImageInfo {
    private String name;
    private String time;
    private String nfcCode;
    private String uploadInfo;
    private String maxTemp;
    private String maxTempX;
    private String maxTempY;
    private String averTemp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getNfcCode() {
        return nfcCode;
    }

    public void setNfcCode(String nfcCode) {
        this.nfcCode = nfcCode;
    }

    public String getUploadInfo() {
        return uploadInfo;
    }

    public void setUploadInfo(String uploadInfo) {
        this.uploadInfo = uploadInfo;
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

    public String getAverTemp() {
        return averTemp;
    }

    public void setAverTemp(String averTemp) {
        this.averTemp = averTemp;
    }

    @Override
    public String toString() {
        return "ImageInfo{" +
                "name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", nfcCode='" + nfcCode + '\'' +
                ", uploadInfo='" + uploadInfo + '\'' +
                ", maxTemp='" + maxTemp + '\'' +
                ", maxTempX='" + maxTempX + '\'' +
                ", maxTempY='" + maxTempY + '\'' +
                ", averTemp='" + averTemp + '\'' +
                '}';
    }
}
