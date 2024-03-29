package src.file;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * ディレクトリ
 * @author Keisuke Nakao
 * @author Kaito Kimura
 */

public class Directory {

    protected String dirName;
    protected ArrayList<Directory> directories;
    protected ArrayList<File> files;

    public Directory(
        String dirName,
        ArrayList<Directory> directories,
        ArrayList<File> files
    ) {
        this.dirName = dirName;
        this.directories = directories;
        this.files = files;
    }

    /**
     * getDirNameメソッド
     * ディレクトリの名前を返す
     * @return String
     */
    public String getDirName() {
        return this.dirName;
    }

    /**
     * convertStringToArrayListメソッド
     * filePathをString型 -> ArrayDeque<String>型に変換
     * e.g. "/a/b/test.txt" -> new ArrayDeque<String>(java.util.List.of("a", "b", "test.txt"))
     * @param filePath Fileのパス
     * @return ArrayDeque<String>
     */
    private ArrayDeque<String> convertStringToArrayList(String filePath) {
        String[] filePathArray = filePath.split("/");
        ArrayDeque<String> filePathArrayList = new ArrayDeque<String>(
            Arrays.asList(filePathArray).subList(0, filePathArray.length)
        );
        return filePathArrayList;
    }

    /**
     * getFileメソッド
     * 指定したファイルを取得する
     * @param filePath 見つけたいFileのパス
     * @return File
     */
    public File getFile(String filePath) {
        return getFile(this.convertStringToArrayList(filePath));
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
        setFile(this.convertStringToArrayList(filePath), updatedFile);
    }

    /**
     * getFileメソッド
     * filePathをArrayDeque<String>型に変更したもの
     * 内部で呼び出され、Handler側からは見えない
     * @param filePath 見つけたいFileのパス
     * @return File
     */
    private File getFile(ArrayDeque<String> filePath) {
        if (filePath.isEmpty()) return null;
        final String name = filePath.poll();

        if (filePath.size() == 0) {
            for (File file : this.files) {
                if (name.equals(file.getFileName())) return file;
            }
        }

        for (Directory directory : this.directories) {
            if (name.equals(directory.getDirName())) {
                return directory.getFile(filePath);
            }
        }

        return null;
    }

    /**
     * setFileメソッド
     * filePathをArrayDeque<String>型に変更したもの
     * 内部で呼び出され、Handler側からは見えない
     * @param filePath 見つけたいFileのパス
     * @param updatedFile 変更後のファイル
     * @return void
     */
    private void setFile(ArrayDeque<String> filePath, File updatedFile) {
        if (filePath.isEmpty()) return;
        final String name = filePath.poll();

        if (filePath.size() == 0) {
            for (File file : this.files) {
                if (name.equals(file.getFileName())) {
                    file.setFileContent(updatedFile.getFileContent());
                    return;
                }
            }
            this.files.add(updatedFile);
        }

        for (Directory directory : this.directories) {
            if (name.equals(directory.getDirName())) {
                directory.setFile(filePath, updatedFile);
                return;
            }
        }
        Directory directory = new Directory(
            name,
            new ArrayList<Directory>(),
            new ArrayList<File>()
        );
        directory.setFile(filePath, updatedFile);
        this.directories.add(directory);

        return;
    }
}
