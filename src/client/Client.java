package src.client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import src.file.File;
import src.server.EntryServer;
import src.util.Mode;

/**
* 分散ファイルシステムを使用するクライアント
* @author Kaito Kimura
* @author Keisuke Nakao
*/

public class Client {
    private static CacheHandler cacheHandler;
    private static int clientId;

    private Client() {}

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

            // クライアントIDを受信
            int cid = (int) in.readObject();
            clientId = cid;
            cacheHandler = new CacheHandler(clientId);
            System.out.println("あなたのクライアントIDは " + cid + " です.");

            try (BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in))) {
                while (true) {
                    System.out.print(">> ");
                    String message = keyboard.readLine();
                    if (message.equals("quit")) break;

                    out.writeObject(message); // データ送信
                    out.flush();

                    if (message.startsWith("read")) {
                        Object receivedObject = in.readObject(); // データ受信
                        System.out.println("receivedObject = " + receivedObject);

                        byte[] bytes = (byte[]) receivedObject;
                        System.out.println(new String(bytes));
                    } else if (message.startsWith("write")) {
                        String sampleMsg = "Haooiehfiwewef";
                        out.writeObject(sampleMsg.getBytes());
                        out.flush();
                        boolean isSuccessful = (boolean) in.readObject();
                        System.out.println("isSuccessful = " + isSuccessful);
                    }
                }
            }
        } finally {
            System.out.println("closing...");
            socket.close();
        }
    }
}
