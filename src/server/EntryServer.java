package src.server;
import src.file.File;
import src.server.exception.EntryServerException;
import src.util.Mode;

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
    private static final Map<Integer, ObjectOutputStream> clientStreams = new HashMap<>();
    /**
     * ファイルサーバー上の各ファイルについて、各ユーザーの権限を管理する
     */
    private static final Map<String, FileUserGroup> fileUserGroups = new HashMap<>();

    /**
     * キーをホスト名としたファイルサーバーのリスト
     * */
    private static final Map<String, FileServer> fileServers = new HashMap<>();

    private static void initFileServers() {
        String fsRoot = System.getenv("FS_ROOT");
        if (fsRoot == null) {
            System.out.println("FS_ROOT is not set");
            System.exit(1);
        }

        FileServer a = new FileServer(Paths.get(fsRoot));
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
                ObjectOutputStream clientOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
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

    private static String joinFilePath(String hostname, Path p) {
        return hostname + ":" + p.toString();
    }

    /**
     * ファイルの権限グループにユーザーを追加する
     * @param hostname ファイルサーバーのホスト名
     * @param p 開きたいファイルのパス
     * @param clientId クライアントID
     * @param mode ファイルモード
     * @throws EntryServerException ファイルが他のユーザーによって使用中のため、開くことができない場合
     */
    private static void openFile(String hostname, Path p, int clientId, Mode mode) throws EntryServerException {
        String path = joinFilePath(hostname, p);
        FileUserGroup group = fileUserGroups.get(path);
        if (group == null) {
            group = new FileUserGroup();
        }

        if (mode.canWrite()) {
            if (group.canWrite()) {
                group.addUser(clientId, mode.canRead(), mode.canWrite());
                fileUserGroups.put(path, group);
            } else {
                throw new EntryServerException("ファイルが他のユーザーによって使用中のため、書き込みできません。");
            }
        }
        else {
            if (!group.hasWriteUser()) {
                group.addUser(clientId, true, false);
                fileUserGroups.put(path, group);
            } else {
                throw new EntryServerException("ファイルが他のユーザーによって使用中のため、開くことができません。");
            }
        }
    }

    /**
     * ファイルの権限グループからユーザーを削除する
     * @param hostname ファイルサーバーのホスト名
     * @param p 開きたいファイルのパス
     * @param clientId クライアントID
     */
    private static void closeFile(String hostname, Path p, int clientId) {
        String path = joinFilePath(hostname, p);
        FileUserGroup group = fileUserGroups.get(path);
        if (group != null) {
            group.removeUser(clientId);
        }
    }

    /**
     * 指定されたファイルサーバーからファイルを読み込む
     * @param hostname ファイルサーバーのホスト名
     * @param p 読み込みたいファイルのパス
     * @param clientId クライアントID
     * @return 読み込んだファイル
     */
    private static File readFile(String hostname, Path p, int clientId) throws EntryServerException {
        String path = joinFilePath(hostname, p);
        FileUserGroup group = fileUserGroups.get(path);

        if (group != null && group.allowRead(clientId)) {
            FileServer fileServer = fileServers.get(hostname);
            return fileServer != null ? fileServer.readFile(p) : null;
        } else {
            throw new EntryServerException("指定されたファイルを開く権限がありません。");
        }
    }

    /**
     * 指定されたファイルサーバーにファイルを書き込む
     * @param hostname ファイルサーバーのホスト名
     * @param p 書き込みたいファイルのパス
     * @param clientId クライアントID
     * @param superFile 書き込むファイル
     * @return 書き込みに成功すればtrue、失敗すればfalse
     */
    private static boolean writeFile(String hostname, Path p, int clientId, File superFile) throws EntryServerException {
        String path = joinFilePath(hostname, p);
        FileUserGroup group = fileUserGroups.get(path);

        if (group != null && group.allowWrite(clientId)) {
            FileServer fileServer = fileServers.get(hostname);
            return fileServer != null ? fileServer.writeFile(p, superFile) : false;
        } else {
            throw new EntryServerException("指定されたファイルに書き込む権限がありません。");
        }
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

        /**
         * 引数の数が合っていないときの例外処理
         * @throws IOException
         */
        private void throwArgumentMismatchError() throws IOException {
            EntryServerException e = new EntryServerException("引数の数が不正です。");
            clientStreams.get(clientId).writeObject(e);
            clientStreams.get(clientId).flush();
        }

        @Override
        public void run() {
            try (ObjectInputStream clientInputStream = new ObjectInputStream(clientSocket.getInputStream())) {
                while (true) {
                    // クライアントからオブジェクトを受信
                    Object receivedObject = clientInputStream.readObject();
                    System.out.println("クライアント " + clientId + " からオブジェクトを受信: " + receivedObject);

                    // 受け取ったオブジェクトが文字列型の場合
                    if (receivedObject.getClass() == String.class) {
                        String message = (String) receivedObject;
                        String[] rpc = message.split(" ");
                        String hostname;
                        Path p;

                        switch (rpc[0]) {
                            case "read":
                                // read [hostname] [path]
                                if (rpc.length < 3) {
                                    throwArgumentMismatchError();
                                    break;
                                }
                                hostname = rpc[1];
                                p = Paths.get(rpc[2]);
                                try {
                                    File file = readFile(hostname, p, clientId);
                                    clientStreams.get(clientId).writeObject(file);
                                } catch (EntryServerException e) {
                                    clientStreams.get(clientId).writeObject(e);
                                }
                                clientStreams.get(clientId).flush();
                                break;
                            case "write":
                                // write [hostname] [path]
                                // [data]
                                if (rpc.length < 3) {
                                    throwArgumentMismatchError();
                                    break;
                                }
                                hostname = rpc[1];
                                p = Paths.get(rpc[2]);
                                Object data = clientInputStream.readObject();
                                try {
                                    boolean res = writeFile(hostname, p, clientId, (File) data);
                                    clientStreams.get(clientId).writeObject(res);
                                } catch (EntryServerException e) {
                                    clientStreams.get(clientId).writeObject(e);
                                }
                                clientStreams.get(clientId).flush();
                                break;
                            case "open":
                                // open [hostname] [path] [mode]
                                if (rpc.length < 4) {
                                    throwArgumentMismatchError();
                                    break;
                                }
                                hostname = rpc[1];
                                p = Paths.get(rpc[2]);
                                Mode mode = Mode.parseMode(rpc[3]);
                                if (mode == null) break;
                                try {
                                    openFile(hostname, p, clientId, mode);
                                    clientStreams.get(clientId).writeObject(true);
                                } catch (EntryServerException e) {
                                    clientStreams.get(clientId).writeObject(e);
                                }
                                clientStreams.get(clientId).flush();
                                break;
                            case "close":
                                // close [hostname] [path]
                                if (rpc.length < 3) break;
                                hostname = rpc[1];
                                p = Paths.get(rpc[2]);
                                closeFile(hostname, p, clientId);
                                break;
                            default:
                                System.out.println("command not found");
                                break;
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                // クライアントが切断された場合の処理
                System.out.println("クライアント " + clientId + " が切断されました.");
                clientStreams.remove(clientId);
            }
        }

        /**
         * 他の接続クライアントに対してメッセージをブロードキャストする（テスト用メソッド）
         * @param senderClientId 送信者のclientId
         * @param object 送信するObject
         */
        private void broadcastObject(int senderClientId, Object object) {
            for (Map.Entry<Integer, ObjectOutputStream> entry : clientStreams.entrySet()) {
                int receiverClientId = entry.getKey();
                if (receiverClientId != senderClientId) {
                    try {
                        ObjectOutputStream receiverOutputStream = entry.getValue();
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
