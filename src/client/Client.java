package src.client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import src.file.File;
import src.server.EntryServer;
import src.server.exception.EntryServerException;
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

                    out.writeObject(message); // 入力文字列を送信
                    out.flush();

                    if (message.startsWith("read")) {
                        Object receivedObject = in.readObject(); // データ受信
                        System.out.println("receivedObject = " + receivedObject);

                        if (receivedObject.getClass() == File.class) {
                            File receivedFile = (File) receivedObject;
                            System.out.println(new String(receivedFile.getFileContent()));
                        } else {
                            EntryServerException e = (EntryServerException) receivedObject;
                            System.out.println(e.getMessage());
                        }
                    } else if (message.startsWith("write")) {
                        // サンプルデータを更新用として送信
                        File sampleFile = new File("sample.txt", true, true);
                        String sampleMsg = "Haooiehfiwewef";
                        sampleFile.setFileContent(sampleMsg.getBytes());
                        out.writeObject(sampleFile);
                        out.flush();

                        Object receivedObject = in.readObject();
                        if (receivedObject.getClass() == Boolean.class) {
                            boolean isSuccessful = (boolean) receivedObject;
                            System.out.println("isSuccessful = " + isSuccessful);
                        } else {
                            EntryServerException e = (EntryServerException) receivedObject;
                            System.out.println(e.getMessage());
                        }
                    } else if (message.startsWith("open")) {
                        Object receivedObject = in.readObject(); // データ受信

                        if (receivedObject.getClass() == Boolean.class) {
                            boolean isSuccessful = (boolean) receivedObject;
                            System.out.println("isSuccessful = " + isSuccessful);
                        } else {
                            EntryServerException e = (EntryServerException) receivedObject;
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }
        } finally {
            System.out.println("closing...");
            socket.close();
        }
    }
}
