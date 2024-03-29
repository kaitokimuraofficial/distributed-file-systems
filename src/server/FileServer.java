package src.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * ファイルサーバー
 * @author Aoyagi Tomoya
 */

public class FileServer {

    /** このパスをルートとしてファイルサーバーを立てる */
    private final Path root;

    public FileServer(Path root) {
        this.root = root;
    }

    /**
     * 与えられたパスから、実際のファイルサーバー上のパスを取得する
     * @param filePath 取得したいファイルのパス
     * @return ファイルサーバー上のファイルのパス
     */
    private Path getAbsolutePath(Path filePath) {
        return this.root.resolve(filePath).toAbsolutePath();
    }

    /**
     * 与えられたパスのファイルの内容を読み込み、Fileのインスタンスとして返す
     * @param filePath 読み込みたいファイルのパス
     * @return ファイルの内容
     */
    public src.file.File readFile(Path filePath) {
        Path p = getAbsolutePath(filePath);

        if (!Files.exists(p)) {
            return null;
        }

        try {
            File f = new File(p.toString());
            src.file.File superFile = new src.file.File(
                f.getName(),
                f.canRead(),
                f.canWrite()
            );
            superFile.setFileContent(Files.readAllBytes(p));
            return superFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 与えられたパスのファイルに、ファイルを書き込む
     * @param filePath 書き込みたいファイルのパス
     * @param superFile 書き込むファイル
     * @return 書き込みに成功したかどうか
     */
    public boolean writeFile(Path filePath, src.file.File superFile) {
        Path p = getAbsolutePath(filePath);

        // ファイルが存在しない場合、新規作成する
        if (!Files.exists(p)) {
            File f = new File(p.toString());
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        try {
            Files.write(p, superFile.getFileContent());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
