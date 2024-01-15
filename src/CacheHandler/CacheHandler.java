package src.CacheHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Path;
import java.nio.file.Paths;

import src.Directory.Directory;
import src.File.File;
import src.FileCache.FileCache;
import src.Mode.Mode;


/**
* ファイルのキャッシュを操作・管理するクラス
* @author  kaitokimuraofficial
*/

public class CacheHandler {
    private final FileCache fileCache;
    private final int ownedBy;
    private final Map<String, File> openedFiles = new HashMap<>();

    public CacheHandler(int ownedBy) {
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
    * openFileContentメソッド
    * 指定されたパスのFileを開き、readできる状態にする
    * @param filePath openしたいFileのパス
    */
   public boolean openFileContent(String filePath, Mode fileMode) {
      if (openedFiles.containsKey(filePath)) return true;

      File targetFile = this.search(filePath);

      // ファイルが存在せず、かつ書き込み可能な権限でファイルを開いている場合は新規作成する
      if (targetFile == null && fileMode.canWrite()) {
         Path p = Paths.get(filePath);
         fileCache.setFile(filePath, new File(this.ownedBy, p.getFileName().toString(), true, true));
      }

      // 権限があるか確認
      openedFiles.put(filePath, targetFile);

      return openedFiles.containsKey(filePath);
   }

   /**
    * closeFileContentメソッド
    * 指定されたパスのFileをopenedMapsから削除する
    * @param filePath closeしたいFileのパス
    */
   public boolean closeFileContent(String filePath) {
      if (!openedFiles.containsKey(filePath)) return true;

      openedFiles.remove(filePath);

      return !openedFiles.containsKey(filePath);
   }

    /**
    * getFileContentメソッド
    * Fileの内容をString変換して返す
    * ClientがあるFileについてReadしている場面
    * @param filePath 見つけたいFileのパス
    * @return 見つけたいFileのfileContent
    */
    public String getFileContent(String filePath) {
        File targetFile = openedFiles.get(filePath);
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
        File targetFile = openedFiles.get(filePath);
        if (targetFile == null) return -1;
        int len = targetFile.setFileContent(text);
        if (len != -1) this.fileCache.setFile(filePath, targetFile);
        return len;
    }

    // set method
    /**
    * setFileCacheメソッド
    * FileCacheが持つDirectoryクラスを更新する
    * ClientがOpenをしたり、他のClientのWriteによってキャッシュがUpdateされる場面
    * @param root 更新後のディレクトリの根
    * @return 成功したらtrue、失敗したらfalse
    */
    public boolean setFileCache(Directory root) {
        if (root == null) return false;
        this.fileCache.setRoot(root);
        return true;
    }
}
