package src.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import src.CacheHandler.CacheHandler;
import src.EntryServer.EntryServer;
import src.Mode.Mode;
import src.File.File;

/**
* 分散ファイルシステムを使用するクライアント
* @author　Kaito Kimura
*/

/**
* EntryServerやFileServerが存在しない仮定でのClientクラスの基本形
* 内部でCacheHandlerをもち、このcacheHandlerに対してread()、write()を行う
*
*
*/
public class Client {
    private CacheHandler cacheHandler;

    public Client() {
        this.cacheHandler = new CacheHandler(0);
    }

    public boolean open(String filePath, Mode fileMode) {
        return cacheHandler.openFileContent(filePath, fileMode);
    }

    public boolean close(String filePath) {
        return cacheHandler.closeFileContent(filePath);
    }

    /**
    * readメソッド
    * Fileの内容を表示する
    * ClientがあるFileの内容を表示したい場面
    * @param filePath 見つけたいFileのパス
    * @return void
    */
    public void read(String filePath) {
        String fileContent = cacheHandler.getFileContent(filePath);
        System.out.println(fileContent);
    }

    /**
    * writeメソッド
    * Fileの内容をString変換して返す
    * ClientがあるFileの内容を書き換える場面
    * @param fileName 見つけたいFileのパス
    * @param text Fileに書き込みたい内容
    * @return void
    */
    public void write(String filePath, String text) {
        int res = cacheHandler.setFileContent(filePath, text);

        // cacheHandlerがsetFileContent()に失敗した
        if (res<0) {
            System.out.println("Failed to write");
            return;
        }
        System.out.println("Success!");
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        InetAddress addr = InetAddress.getByName("localhost"); // IP アドレスへの変換
        System.out.println("addr = " + addr);
        Socket socket = new Socket(addr, EntryServer.PORT); // ソケットの生成
        try {
            System.out.println("socket = " + socket);

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()); // 送信バッファ設定
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream()); // データ受信用バッファの設定

            File file = new File(0, "a.txt", true, true);

            for(int i = 0; i < 10; i++) {
                out.writeObject(file); // データ送信
                File receivedFile = (File) in.readObject(); // データ受信
                System.out.println(receivedFile.getFileName());
            }
            out.writeObject(null);
        } finally {
            System.out.println("closing...");
            socket.close();
        }
    }
}
