package src.client;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import src.file.Directory;
import src.file.File;
import src.util.Mode;

/**
 * ファイルのキャッシュを操作・管理するクラス
 * @author Kaito Kimura
 * @author Keisuke Nakao
 */

public class CacheHandler {

    private final FileCache fileCache;
    private final int ownedBy;
    private final Map<String, Mode> openedFiles = new HashMap<>();

    public CacheHandler(int ownedBy) {
        this.fileCache = new FileCache();
        this.ownedBy = ownedBy;
    }

    /**
     * getFileメソッド
     * FileCacheのDirectoryクラスからfilePathのFileを見つけて返す
     * @param filePath 見つけたいFileのパス
     * @return 見つけたいFile
     */
    public File getFile(String filePath) {
        return this.fileCache.getFile(filePath);
    }

    // getter method
    public int getOwnedBy() {
        return this.ownedBy;
    }

    /**
     * openFileContentメソッド
     * 指定されたパスのFileを開き、readできる状態にする
     * @param filePath openしたいFileのパス
     */
    public boolean openFile(String filePath, File serverFile, Mode fileMode) {
        if (openedFiles.containsKey(filePath)) return true;
        File targetFile = this.getFile(filePath);

        if (targetFile == null || !targetFile.getIsCacheValid()) {
            File file;
            Path p = Paths.get(filePath);
            file =
                (serverFile == null)
                    ? new File(p.getFileName().toString(), true, true)
                    : serverFile;

            fileCache.setFile(filePath, file);
        }

        openedFiles.put(filePath, fileMode);
        return openedFiles.containsKey(filePath);
    }

    /**
     * closeFileContentメソッド
     * 指定されたパスのFileをopenedMapsから削除する
     * @param filePath closeしたいFileのパス
     */
    public boolean closeFile(String filePath) {
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
        File targetFile = this.getFile(filePath);
        return new String(targetFile.getFileContent());
    }

    /**
     * setFileContentメソッド
     * Fileの内容を変更する
     * ClientがWriteしている場面
     * @param filePath 見つけたいFileのパス
     * @return 成功したら書き込んだtextの文字数、失敗したら-1
     */
    public int setFileContent(String filePath, String text) {
        File targetFile = this.getFile(filePath);
        if (targetFile == null) return -1;
        int len = targetFile.setFileContent(text.getBytes());
        if (len != -1) this.fileCache.setFile(filePath, targetFile);
        return len;
    }

    /**
     * setIsCacheValidメソッド
     * FileのisCacheValidを変更する
     * @param filePath 見つけたいFileのパス
     * @return 成功したか失敗したかをbool値で返す
     */
    public boolean setIsCacheValid(String filePath, boolean bool) {
        File targetFile = this.getFile(filePath);
        if (targetFile == null) return false;
        targetFile.setIsCacheValid(bool);
        this.fileCache.setFile(filePath, targetFile);
        return true;
    }

    public boolean isOperationAllowed(String filePath, String operation) {
        if (!openedFiles.containsKey(filePath)) return false;

        Mode fileMode = openedFiles.get(filePath);

        if (operation.equals("read")) return fileMode.canRead(); else if (
            operation.equals("write")
        ) return fileMode.canWrite(); else return false;
    }

    public Mode getOpenedFileMode(String filePath) {
        return openedFiles.get(filePath);
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
