package developer.lin.local.picturebrowse.entity;

import java.io.Serializable;

/**
 * Created by lin on 2015/10/27.
 */
public class GirdViewEntity implements Serializable {

    private String path;
    private boolean isSelected;
    private String dirPath;
    private String absolutePath;

    public String getAbsolutePath() {
        return dirPath + "/" + path;
    }

    public void setAbsolutePath(String absolutePath) {
        //   this.absolutePath = absolutePath;
    }


    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
