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

    public boolean open(String filePath, File serverFile, Mode fileMode) {
        return cacheHandler.openFile(filePath, serverFile, fileMode);
    }

    public boolean close(String filePath) {
        return cacheHandler.closeFile(filePath);
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

                    String[] messageParts = message.split(" ");
                    String opetarion = messageParts[0];
                    String hostname = messageParts[1];
                    String filePath = messageParts[2];
                    String mode = "rw";
                    if (messageParts.length == 4) mode = messageParts[3];

                    if (opetarion.equals("quit")) break;

                    if (opetarion.equals("open")) {
                        // open
                        out.writeObject(message); // 入力文字列を送信
                        out.flush();

                        Object receivedObject = in.readObject(); // データ受信
                        boolean isSuccessful = false;
                        if (receivedObject.getClass() == Boolean.class) {
                            isSuccessful = (boolean) receivedObject;
                            System.out.println("isSuccessful = " + isSuccessful);
                        } else {
                            EntryServerException e = (EntryServerException) receivedObject;
                            System.out.println(e.getMessage());
                        }
                        if (!isSuccessful) continue;
                        
                        // read
                        out.writeObject("read" + " " + hostname + " " + filePath); // 入力文字列を送信
                        out.flush();

                        receivedObject = in.readObject(); // データ受信
                        System.out.println("receivedObject = " + receivedObject);

                        File receivedFile = null;
                        if (receivedObject.getClass() == File.class) {
                            receivedFile = (File) receivedObject;
                            System.out.println(new String(receivedFile.getFileContent()));
                        } else {
                            EntryServerException e = (EntryServerException) receivedObject;
                            System.out.println(e.getMessage());
                        }
                        cacheHandler.openFile(filePath, receivedFile, Mode.parseMode(mode));
                    } else if (opetarion.equals("read")) {
                        if (cacheHandler.isOperationAllowed(filePath, opetarion)) {
                            String content = cacheHandler.getFileContent(filePath);
                            System.out.println(content);
                        } else {
                            System.out.println("指定されたファイルを読み込む権限がありません。");
                        }
                    } else if (opetarion.equals("write")) {
                        if (cacheHandler.isOperationAllowed(filePath, opetarion)) {
                            String content = keyboard.readLine();
                            cacheHandler.setFileContent(filePath, content);
                        } else {
                            System.out.println("指定されたファイルに書き込む権限がありません。");
                        }
                    } else if (opetarion.equals("close")) {
                        // write
                        out.writeObject("write" + " " + hostname + " " + filePath); // 入力文字列を送信
                        out.flush();

                        File file = cacheHandler.getFile(filePath);
                        out.writeObject(file);
                        out.flush();

                        // close
                        cacheHandler.closeFile(filePath);

                        out.writeObject(message);
                        out.flush();
                    }
                }
            }
        } finally {
            System.out.println("closing...");
            socket.close();
        }
    }
}
