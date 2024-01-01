package src.CacheHandler;

import java.util.Arrays;

import src.Directory.Directory;
import src.File.File;
import src.FileCache.FileCache;


/**
* ファイルのキャッシュを操作・管理するクラス
* @author  kaitokimuraofficial
*/

/**
* して欲しいこと
* 1, search()の実装
* 2, cacheHandlerが持つFileCacheの中身を更新するsetFileCache()の実装
* 3, (できればテスト)
* 
*/

public class CacheHandler {
    private final FileCache fileCache;
    private final int ownedBy;

    private CacheHandler(int ownedBy) {
        this.fileCache = new FileCache();
        this.ownedBy = ownedBy;
    }

    /**
    * searchメソッド
    * FileCacheのDirectoryクラスからfilePathのFileを見つけて返す
    * @param filePath 見つけたいFileのパス
    * @return 見つけたいFile
    */
    private File search(String filePath) {
        return this.fileCache.getFile(filePath);
    }

    // getter method
    public int getOwnedBy() {
        return this.ownedBy;
    }

    /**
    * Clientクラスからの命令は
    * File.fileContentに対するRead, Writeのみだと仮定
    */

    /**
    * getFileContentメソッド
    * Fileの内容をString変換して返す
    * ClientがあるFileについてReadしている場面
    * @param filePath 見つけたいFileのパス
    * @return 見つけたいFileのfileContent
    */
    public String getFileContent(String filePath) {
        File targetFile = this.search(filePath);
        return Arrays.toString(targetFile.getFileContent());
    }

    /**
    * setFileContentメソッド
    * Fileの内容を変更する
    * ClientがWriteしている場面
    * @param filePath 見つけたいFileのパス
    * @return 成功したら書き込んだtextの文字数、失敗したら-1
    */
    public int setFileContent(String filePath, String text) {
        File targetFile = this.search(filePath);
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
    public Boolean setFileCache(Directory directory) {
        this.fileCache.setDirectories() = directory;
    }
}
