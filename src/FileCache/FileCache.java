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

    /**
    * getLastUpdatedDateメソッド
    * キャッシュが最後に変更された日時を返す
    * @return Date
    */
    public Date getLastUpdatedDate() {
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
        this.lastUpdatedDate = new Date();
    }
}
