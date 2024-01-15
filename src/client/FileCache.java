package src.client;

import src.file.FileContainer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.time.LocalDateTime;
import java.util.ArrayList;


/**
* ファイルのキャッシュ
* @author  kaitokimuraofficial
* @author　kei-0917
*/

public class FileCache {
    private final Path root;

    public FileCache(Path root) {
        this.root = root;
    }

    /**
     * 与えられたパスから、実際のファイルサーバー上のパスを取得する
     * @param filePath 取得したいファイルのパス
     * @return キャッシュ上のファイルのパス
     */
    private Path getAbsolutePath(Path filePath) {
        return this.root.resolve(filePath).toAbsolutePath();
    }

    /**
     * 与えられたパスのファイルの内容を読み込み、バイト列として返す
     * @param filePath 読み込みたいファイルのパス
     * @return ファイルの内容
     */
    public byte[] readFile(Path filePath) {
        Path p = getAbsolutePath(filePath);

        if (!Files.exists(p)) {
            return null;
        }

        try {
            return Files.readAllBytes(p);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 与えられたパスのファイルに、バイト列を書き込む
     * @param filePath 書き込みたいファイルのパス
     * @param data 書き込む内容
     * @return 書き込みに成功したかどうか
     */
    public boolean writeFile(Path filePath, byte[] data) {
        Path p = getAbsolutePath(filePath);

        // ファイルが存在しない場合、新規作成する
        if (!Files.exists(p)) {
            FileContainer f = new FileContainer(p.toString(), new byte[]);
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        try {
            Files.write(p, data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
}
