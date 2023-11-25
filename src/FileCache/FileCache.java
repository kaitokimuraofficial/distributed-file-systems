package src.FileCache;

import src.File.File;

import java.util.Date;

/**
* ファイルのキャッシュ
* @author　Kaito Kimura
*/

public class FileCache {
    private final int DEFAULTSIZE;
    private final Date creationDate;

    private File file;
    private Date lastUpdatedDate;

    public static class Builder {
        private final int DEFAULTSIZE = 4096;
        private final Date creationDate;

        public Builder() {
            this.creationDate = new Date();
        }

        public FileCache build() {
            return new FileCache(this);
        }
    }

    private FileCache(Builder builder) {
        DEFAULTSIZE = builder.DEFAULTSIZE;
        creationDate = builder.creationDate;

        lastUpdatedDate = builder.creationDate;
    }

    // getter method
    public int getDEFAULTSIZE() {
        return DEFAULTSIZE;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public File getFile() {
        return file;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    // setter method
    public void setFile(File file) {
        this.file = file;
        this.lastUpdatedDate = new Date();
    }
}
