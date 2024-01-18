package src.client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import src.file.File;
import src.server.EntryServer;
import src.server.EntryServerResponse;
import src.server.exception.EntryServerException;
import src.util.Mode;
import src.util.OperationType;

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

            // 受け取ったメッセージの処理は全部こっちでやる
            new Thread(new ReceivedObjectHandler(in)).start();

            try (BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in))) {
                while (true) {
                    System.out.print(">> ");
                    String message = keyboard.readLine();

                    String[] messageParts = message.trim().split("\\s+");

                    String operation = messageParts[0];

                    if (operation.equals("quit")) break;

                    if (messageParts.length <= 2) {
                        System.out.println("引数の数が不正です。");
                        continue;
                    }

                    String hostname = messageParts[1];
                    String filePath = messageParts[2];
                    String mode = (messageParts.length == 4)
                        ? messageParts[3]
                        : "rw";


                    if (operation.equals("open")) {
                        if (cacheHandler.getIsFileOpened(filePath)) {
                            System.out.println("指定されたファイルは既に開かれています。");
                            continue;
                        }

                        // open
                        out.writeObject(message); // 入力文字列を送信
                        out.flush();

                        if (!mode.equals("w") && !cacheHandler.getIsCacheValid(filePath)) {
                            out.writeObject("read" + " " + hostname + " " + filePath); // 入力文字列を送信
                            out.flush();
                        }
                    } else if (operation.equals("read")) {
                        if (cacheHandler.isOperationAllowed(filePath, operation)) {
                            String content = cacheHandler.getFileContent(filePath);
                            System.out.println(content);
                        } else {
                            System.out.println("指定されたファイルを読み込む権限がありません。");
                        }
                    } else if (operation.equals("write")) {
                        if (cacheHandler.isOperationAllowed(filePath, operation)) {
                            String content = keyboard.readLine();
                            cacheHandler.setFileContent(filePath, content);
                        } else {
                            System.out.println("指定されたファイルに書き込む権限がありません。");
                        }
                    } else if (operation.equals("close")) {
                        if (!cacheHandler.getIsFileOpened(filePath)) continue;

                        // write
                        if (cacheHandler.getOpenedFileMode(filePath).canWrite()) {
                            out.writeObject("write" + " " + hostname + " " + filePath); // 入力文字列を送信
                            out.flush();

                            File cacheFile = cacheHandler.getFile(filePath);

                            File sendFile = new File(cacheFile.getFileName(), cacheFile.getIsReadAllowed(), cacheFile.getIsWriteAllowed());
                            sendFile.setFileContent(cacheFile.getFileContent());

                            out.writeObject(sendFile);
                            out.flush();
                        }

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

    /**
     * サーバーから受け取ったメッセージを処理する用のスレッド
     */
    private static class ReceivedObjectHandler implements Runnable {
        private final ObjectInputStream in;
        private String openMode;

        public ReceivedObjectHandler(ObjectInputStream in) {
            this.in = in;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Object receivedObject = in.readObject(); // データ受信
                    System.out.println("receivedObject = " + receivedObject);

                    if (receivedObject.getClass() == String.class) {
                        // キャッシュを無効化する処理
                        String receivedCommand = (String) receivedObject;
                        String[] args = receivedCommand.split("\\s+");

                        String operation = args[0];
                        // String hostname = args[1];
                        String filePath = args[2];
                        
                        if (operation.equals("invalidate")) {
                            if (cacheHandler.getFile(filePath) == null) continue;
                            System.out.println(receivedCommand);
                            cacheHandler.setIsCacheValid(filePath, false);
                        }
                    } else if (receivedObject.getClass() == EntryServerResponse.class) {
                        EntryServerResponse response = (EntryServerResponse) receivedObject;
                        Object data = response.getData();

                        // 操作に失敗したとき
                        if (!response.isSuccessful()) {
                            EntryServerException e = (EntryServerException) data;
                            System.out.println(e.getMessage());
                            continue;
                        }

                        String sentCommand = response.getReceivedCommand();
                        String[] args = sentCommand.split("\\s+");

                        switch (response.getOpType()) {
                            case OPEN:
                                System.out.println("file opened successfully");
                                String filePath = args[2];
                                this.openMode = args[3];
                                if (openMode.equals("w") || cacheHandler.getIsCacheValid(filePath)) cacheHandler.openFile(args[2], null, Mode.parseMode(this.openMode));
                                break;
                            case READ:
                                File receivedFile = (File) data;
                                System.out.println(new String(receivedFile.getFileContent()));
                                cacheHandler.openFile(args[2], receivedFile, Mode.parseMode(this.openMode));
                                break;
                            case WRITE:
                                System.out.println("file updated successfully");
                                break;
                            default:
                                break;
                        }
                    }
                }
            } catch (SocketException e) {
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
