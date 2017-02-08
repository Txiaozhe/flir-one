package deveoper.lin.local.picturebrowse.entity;

import java.io.Serializable;

/**
 * picture parent folder field
 * Created by lin on 2015/10/27.
 */
public class PictureFolderEntity implements Serializable {

    private String dir;
    private String firstImagePath;
    private String dirName;
    private int dirPhotoCount;


    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
        int lastIndexOf = this.dir.lastIndexOf("/");
        this.dirName = this.dir.substring(lastIndexOf);
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public int getDirPhotoCount() {
        return dirPhotoCount;
    }

    public void setDirPhotoCount(int dirPhotoCount) {
        this.dirPhotoCount = dirPhotoCount;
    }
}
