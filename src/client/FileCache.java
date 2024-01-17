package src.client;

import java.time.LocalDateTime;
import java.util.ArrayList;

import src.file.Directory;
import src.file.File;

/**
* ファイルのキャッシュ
* @author kaitokimuraofficial
* @author Keisuke Nakao
*/

public class FileCache {
    private Directory root;
    private LocalDateTime lastUpdatedDate;

    public FileCache() {
        this.root = new Directory("root", new ArrayList<Directory>(), new ArrayList<File>());
        this.lastUpdatedDate = LocalDateTime.now();
    }

    /**
    * getLastUpdatedDateメソッド
    * キャッシュが最後に変更された日時を返す
    * @return Date
    */
    public LocalDateTime getLastUpdatedDate() {
        return this.lastUpdatedDate;
    }

    /**
    * getFileメソッド
    * 指定したファイルを取得する
    * @param filePath 見つけたいFileのパス
    * @return File
    */
    public File getFile(String filePath) {
        return this.root.getFile(filePath);
    }

    /**
    * setFileメソッド
    * 指定したファイルを更新する
    * ファイルが存在しない場合、新たに作成する
    * @param filePath 見つけたいFileのパス
    * @param updatedFile 変更後のファイル
    * @return void
    */
    public void setFile(String filePath, File updatedFile) {
        this.root.setFile(filePath, updatedFile);
        this.lastUpdatedDate = LocalDateTime.now();
    }

    public void disableCache(String filePath) {
        this.root.disableCache(filePath);
    }

    /**
    * setRootメソッド
    * FileCacheが保持するディレクトリthis.rootを更新する
    * @param root 更新後のディレクトリの根
    * @return void
    */
    public void setRoot(Directory root) {
        this.root = root;
    }
}
