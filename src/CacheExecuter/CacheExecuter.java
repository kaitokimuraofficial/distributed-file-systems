package src.CacheExecuter;

import src.File.File;
import src.FileCache.FileCache;


/**
* ファイルのキャッシュを操作・管理するクラス
* @author　Kaito Kimura
*/

public class CacheExecuter {
    private final FileCache fileCache;
    private final int ownedBy;

    public static class Builder {
        private final FileCache fileCache;
        private final int ownedBy;
        
        public Builder(int ownedBy) {
            this.fileCache = new FileCache.Builder().build();
            this.ownedBy = ownedBy;
        }

        public CacheExecuter build() {
            return new CacheExecuter(this);
        }
    }

    private CacheExecuter(Builder builder) {
        fileCache = builder.fileCache;
        ownedBy = builder.ownedBy;
    }

    // getter method
    public File getFile() {
        return fileCache.getFile();
    }

    public int getOwnedBy() {
        return ownedBy;
    }

    // set method
    public void setFile(File file) {
        this.fileCache.setFile(file);
    }
}
