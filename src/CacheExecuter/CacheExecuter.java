package src.CacheExecuter;

import java.util.Arrays;

import src.Directory.Directory;
import src.File.File;
import src.FileCache.FileCache;


/**
* ファイルのキャッシュを操作・管理するクラス
* @author　Kaito Kimura
*/

/**
* して欲しいこと
* 1, search()の実装
* 2, cacheExecuterが持つFileCacheの中身を更新するsetFileCache()の実装
* 3, (できればテスト)
* 
* 修正は相談の上でガンガンしてもOK！
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

    /**
    * searchメソッド
    * FileCacheのDirectoryクラスからfileNameのFileを見つけて返す
    * @param fileName 見つけたいFileの名前
    * @return 見つけたいFile
    */
    private File search(String fileName) {
        return File
    }

    // getter method
    public int getOwnedBy() {
        return ownedBy;
    }

    /**
    * Clientクラスからの命令は
    * File.fileContentに対するRead, Writeのみだと仮定
    * CacheExecuter、FileCache、Clientの関係はsequence.mdを参考にしてください
    */

    /**
    * getFileContentメソッド
    * Fileの内容をString変換して返す
    * ClientがあるFileについてReadしている場面
    * @param fileName 見つけたいFileの名前
    * @return 見つけたいFileのfileContent
    */
    public String getFileContent(String fineName) {
        File targetFile = search(fineName);
        return Arrays.toString(targetFile.getFileContent());
    }

    /**
    * setFileContentメソッド
    * Fileの内容を変更する
    * ClientがWriteしている場面
    * @param fileName 見つけたいFileの名前
    * @return 成功したら書き込んだtextの文字数、失敗したら-1
    */
    public int setFileContent(String fineName, String text) {
        File targetFile = search(fineName);
        int len = targetFile.setFileContent(text);
        return len;
    }

    // set method
    /**
    * setFileCacheメソッド
    * FileCacheが持つDirectoryクラスを更新する
    * ClientがOpenをしたり、他のClientのWriteによってキャッシュがUpdateされる場面
    * @param directory 置き換える内容
    * @return 成功したらtrue、失敗したらfalse
    */
    public Boolean setFileCache(Directory direcotry) {
        this.fileCache.setDirectories() = direcotry;
    }
}