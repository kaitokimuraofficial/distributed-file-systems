package src.FileCache;

import java.util.Date;
import java.util.ArrayList;

import src.Directory.Directory;
import src.File.File;

/**
* ファイルのキャッシュ
* @author　Kaito Kimura
* @author　kei-0917
*/

public class FileCache {
    private Directory root;
    private Date lastUpdatedDate;

    public FileCache() {
        this.root = new Directory("root", new ArrayList<Directory>(), new ArrayList<File>());
        this.lastUpdatedDate = new Date();
    }

    // getter method
    public Date getLastUpdatedDate() {
        return this.lastUpdatedDate;
    }

    public File getFile(String filePath) {
        return this.root.getFile(filePath);
    }

    // setter method
    public void setFile(String filePath, File updatedFile) {
        this.root.setFile(filePath, updatedFile);
        this.lastUpdatedDate = new Date();
    }
}
