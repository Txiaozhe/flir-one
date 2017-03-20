package com.flir.flirone.imagehelp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by txiaozhe on 12/02/2017.
 */

public class ImageHelp {

    private String path; //文件目录

    public ImageHelp(String path) {
        this.path = path;
    }

    public Bitmap getBitMap(String imagePath) {
        Bitmap bitmap = null;
        try {
            File file = new File(imagePath);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(imagePath);
            }
        } catch (Exception e) {

        }

        return bitmap;
    }

    public File[] getFiles() {
        File[] files = null;
        try {
            File file = new File(this.path);
            files = file.listFiles();
        } catch (Exception e) {

        }

        return files;
    }

    //获取缩略图
    public Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    //获取文件大小
    public String getFileOrFilesSize(File file) {
        long blockSize = 0;
        try {
            blockSize = getFileSize(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FormetFileSize(blockSize);
    }

    private long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
        }
        return size;
    }

    private String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    public String getTimeFromName(File file) {
        String name = file.getName();

        String time = name.substring(name.indexOf("_") + 1, name.length() - 4);
        String year = time.substring(0, 4);
        String month = time.substring(4, 6);
        String day = time.substring(6, 8);
        String hour = time.substring(8, 10);
        String min = time.substring(10, 12);
        String sec = time.substring(12, time.length());

        return year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec;
    }

    public String getTimeFromName(String fileName) {

        String time = fileName.substring(fileName.indexOf("_") + 1);
        String year = time.substring(0, 4);
        String month = time.substring(4, 6);
        String day = time.substring(6, 8);
        String hour = time.substring(8, 10);
        String min = time.substring(10, 12);
        String sec = time.substring(12, time.length());

        return year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec;
    }


    public byte[] getFileToByte(File file) {
        byte[] by = new byte[(int) file.length()];
        try {
            InputStream is = new FileInputStream(file);
            ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
            byte[] bb = new byte[2048];
            int ch;
            ch = is.read(bb);
            while (ch != -1) {
                bytestream.write(bb, 0, ch);
                ch = is.read(bb);
            }
            by = bytestream.toByteArray();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return Base64.encode(by, Base64.DEFAULT);
    }


    public ImageInfo getInfoFromName(String name) {
        int index2 = name.indexOf("@");
        int index3 = name.indexOf("#");
        int index4 = name.indexOf("$");
        int index5 = name.indexOf("%");
        int index6 = name.indexOf(".jpg");
        int index7 = name.indexOf("_20");
        int index8 = name.indexOf("_UP");

        ImageInfo info = new ImageInfo();
        info.setName(name.substring(0, index2));
        if (index8 > 0) {
            info.setTime(name.substring(index7 + 1, index8));
        } else {
            info.setTime(name.substring(index7 + 1, index2));
        }
        info.setMaxTemp(name.substring(index2 + 1, index3));
        info.setMaxTempX(name.substring(index3 + 1, index4));
        info.setMaxTempY(name.substring(index4 + 1, index5));
        info.setAverTemp(name.substring(index5 + 1, index6));
        info.setNfcCode(name.substring(0, index7));

        return info;
    }

    public void renameImage(File file) {
        if (file.getName().indexOf("_UP") < 0) {
            File oldFile = new File(file.getPath());
            String oldPath = file.getPath();
            int index1 = oldPath.indexOf("@");
            String newPath = oldPath.substring(0, index1) + "_UP@" + oldPath.substring(index1 + 1);
            oldFile.renameTo(new File(newPath));
        }
    }

    public boolean deleteFile(String path) {
        File file = new File(path);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    public boolean deleteFile(File file) {
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    private int daysBetween(String date1, String date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        long time1 = 0;
        long time2 = 0;

        try {
            cal.setTime(sdf.parse(date1));
            time1 = cal.getTimeInMillis();
            cal.setTime(sdf.parse(date2));
            time2 = cal.getTimeInMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    public void checkAllImagesDate() {
        File[] files = getFiles();
        try {
            for (int i = 0; i < files.length; i++) {
                String time = getTimeFromName(files[i]);
                Date dateNow = new Date();
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                String timeNow = format.format(dateNow);

                if (daysBetween(time, timeNow) > 7 && files[i].getName().indexOf("_UP") > 0) {
                    deleteFile(files[i]);
                }
            }
        } catch (Exception e) {}
    }

}
