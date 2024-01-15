package src.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
// import java.util.Date;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

// import java.util.HashMap;
// import java.util.Map;

// import src.Client.Client;
// import src.File.File;

/**
 * クライアントと直線通信を行うファイルサーバーのエントリ
 * @author Kaito Kimura
 * @author Tomoya Aoyagi
 */

public class EntryServer {

    public static final int PORT = 8080;
    // private final int BACKLOG;
    // private final ServerSocket clientEntry;

    // private Map<Client, Integer> clientIdMap = new HashMap<>();

    /** 次のクライアントに対して割り当てるID */
    private static int clientIdCounter = 0;
    /** 接続されているクライアントのStreamを保持 */
    private static final Map<Integer, ObjectOutputStream> clientStreams =
        new HashMap<>();

    /**
     * キーをホスト名としたファイルサーバーのリスト
     * */
    private static final Map<String, FileServer> fileServers = new HashMap<>();

    private static void initFileServers() {
        FileServer a = new FileServer(Paths.get(System.getenv("FS_ROOT")));
        fileServers.put("localhost", a);
    }

    /**
     * サーバー起動時の処理
     */
    private static void launchServer() {
        initFileServers();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("サーバーが起動しました。");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("新しいクライアントが接続しました.");

                int clientId = clientIdCounter++;
                ObjectOutputStream clientOutputStream = new ObjectOutputStream(
                    clientSocket.getOutputStream()
                );
                clientStreams.put(clientId, clientOutputStream);

                // クライアントIDを送信
                clientOutputStream.writeObject(clientId);

                // clientOutputStream.writeObject(readFile("localhost", Paths.get("hoge.txt"), clientId));

                // クライアントとの通信をハンドルするスレッドを起動
                new Thread(new ClientHandler(clientSocket, clientId)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launchServer();
    }

    /**
     * 指定されたファイルサーバーからファイルを読み込む
     * @param hostname ファイルサーバーのホスト名
     * @param p 読み込みたいファイルのパス
     * @param clientId クライアントID
     * @return 読み込んだファイルの内容
     */
    private static byte[] readFile(String hostname, Path p, int clientId) {
        FileServer fileServer = fileServers.get(hostname);
        return fileServer != null ? fileServer.readFile(p) : null;
    }

    /**
     * 指定されたファイルサーバーにファイルを書き込む
     * @param hostname ファイルサーバーのホスト名
     * @param p 書き込みたいファイルのパス
     * @param clientId クライアントID
     * @param data 書き込む内容
     * @return 書き込みに成功すればtrue、失敗すればfalse
     */
    private static boolean writeFile(
        String hostname,
        Path p,
        int clientId,
        byte[] data
    ) {
        FileServer fileServer = fileServers.get(hostname);
        return fileServer != null ? fileServer.writeFile(p, data) : false;
    }

    /**
     * 接続された単一のクライアントについてメッセージの送受信を行うクラス
     * */
    private static class ClientHandler implements Runnable {

        private final Socket clientSocket;
        private final int clientId;

        public ClientHandler(Socket clientSocket, int clientId) {
            this.clientSocket = clientSocket;
            this.clientId = clientId;
        }

        @Override
        public void run() {
            try {
                ObjectInputStream clientInputStream = new ObjectInputStream(
                    clientSocket.getInputStream()
                );

                while (true) {
                    // クライアントからオブジェクトを受信
                    Object receivedObject = clientInputStream.readObject();
                    System.out.println(
                        "クライアント " +
                        clientId +
                        " からオブジェクトを受信: " +
                        receivedObject
                    );

                    if (receivedObject.getClass() == String.class) {
                        String message = (String) receivedObject;
                        String[] rpc = message.split(" ");
                        String hostname;
                        Path p;

                        switch (rpc[0]) {
                            case "read":
                                if (rpc.length < 3) break;
                                hostname = rpc[1];
                                p = Paths.get(rpc[2]);
                                byte[] fileContent = readFile(
                                    hostname,
                                    p,
                                    clientId
                                );
                                clientStreams
                                    .get(clientId)
                                    .writeObject(fileContent);
                                clientStreams.get(clientId).flush();
                                break;
                            case "write":
                                if (rpc.length < 3) break;
                                hostname = rpc[1];
                                p = Paths.get(rpc[2]);
                                Object data = clientInputStream.readObject();
                                boolean res = writeFile(
                                    hostname,
                                    p,
                                    clientId,
                                    (byte[]) data
                                );
                                clientStreams.get(clientId).writeObject(res);
                                clientStreams.get(clientId).flush();
                                break;
                            default:
                                System.out.println("error");
                                break;
                        }
                    }
                    // オブジェクトを他のクライアントにブロードキャスト
                    // broadcastObject(clientId, receivedObject);
                }
            } catch (IOException | ClassNotFoundException e) {
                // クライアントが切断された場合の処理
                System.out.println(
                    "クライアント " + clientId + " が切断されました."
                );
                clientStreams.remove(clientId);
            }
        }

        /**
         * 他の接続クライアントに対してメッセージをブロードキャストする（テスト用メソッド）
         * @param senderClientId 送信者のclientId
         * @param object 送信するObject
         */
        private void broadcastObject(int senderClientId, Object object) {
            for (Map.Entry<
                Integer,
                ObjectOutputStream
            > entry : clientStreams.entrySet()) {
                int receiverClientId = entry.getKey();
                if (receiverClientId != senderClientId) {
                    try {
                        ObjectOutputStream receiverOutputStream =
                            entry.getValue();
                        receiverOutputStream.writeObject(object);
                        receiverOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
