package src.client;

import java.util.ArrayList;

import src.file.Directory;
import src.file.File;

/**
 * ファイルのキャッシュ
 * @author Kaito Kimura
 * @author Keisuke Nakao
 */

public class FileCache {

    private Directory root;

    public FileCache() {
        this.root =
            new Directory(
                "root",
                new ArrayList<Directory>(),
                new ArrayList<File>()
            );       
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
    }
}
