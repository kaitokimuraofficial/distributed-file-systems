package src.server;
import src.file.File;
import src.server.exception.EntryServerException;
import src.util.Mode;
import src.util.OperationType;

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
     * ファイルサーバー上の各ファイルについて、開かれたあとに書き込まれたかどうかを管理する
     */
    private static final Map<String, Boolean> isDirty = new HashMap<>();

    /**
     * キーをホスト名としたファイルサーバーのリスト
     * */
    private static final Map<String, FileServer> fileServers = new HashMap<>();

    private static void initFileServers() {
        System.out.println("Setting up local file servers ...");
        final String SERVER_HOSTNAME_BASE = "localhost";

        for (int i = 0; ; i++) {
            String fsRoot = System.getenv(String.format("FS_ROOT_%d", i));
            if (fsRoot == null) {
                if (i == 0) {
                    System.out.println("FS_ROOT_0 has to be set.");
                    System.exit(1);
                } else {
                    break;
                }
            }

            FileServer fs = new FileServer(Paths.get(fsRoot));
            fileServers.put(SERVER_HOSTNAME_BASE + i, fs);
            System.out.println(String.format("Registered: %s -> %s", SERVER_HOSTNAME_BASE + i, fsRoot));
        }
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
            if (!group.hasCurrentWrite()) {
                group.addUser(clientId, true, false);
                fileUserGroups.put(path, group);
            } else {
                throw new EntryServerException("ファイルが他のユーザーによって使用中のため、開くことができません。");
            }
        }

        isDirty.put(path, false);
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

        if (isDirty.containsKey(path)) {
            isDirty.put(path, false);
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
            if (fileServer != null && fileServer.writeFile(p, superFile)) {
                isDirty.put(path, true);
                return true;
            }
            return false;
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
        private void throwArgumentMismatchError(OperationType opType, String receivedCommand) throws IOException {
            EntryServerException e = new EntryServerException("引数の数が不正です。");
            clientStreams.get(clientId).writeObject(EntryServerResponse.error(opType, receivedCommand, e));
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
                        if (message.length() == 0) continue;

                        String[] rpc = message.split(" ");
                        String hostname;
                        Path p;

                        switch (rpc[0]) {
                            case "read":
                                // read [hostname] [path]
                                if (rpc.length < 3) {
                                    throwArgumentMismatchError(OperationType.READ, message);
                                    break;
                                }
                                hostname = rpc[1];
                                p = Paths.get(rpc[2]);
                                try {
                                    File file = readFile(hostname, p, clientId);
                                    clientStreams.get(clientId).writeObject(EntryServerResponse.ok(OperationType.READ, message, file));
                                } catch (EntryServerException e) {
                                    clientStreams.get(clientId).writeObject(EntryServerResponse.error(OperationType.READ, message, e));
                                }
                                clientStreams.get(clientId).flush();
                                break;
                            case "write":
                                // write [hostname] [path]
                                // [data]
                                if (rpc.length < 3) {
                                    throwArgumentMismatchError(OperationType.WRITE, message);
                                    break;
                                }
                                hostname = rpc[1];
                                p = Paths.get(rpc[2]);
                                Object data = clientInputStream.readObject();
                                try {
                                    boolean res = writeFile(hostname, p, clientId, (File) data);
                                    clientStreams.get(clientId).writeObject(EntryServerResponse.ok(OperationType.WRITE, message, res));
                                } catch (EntryServerException e) {
                                    clientStreams.get(clientId).writeObject(EntryServerResponse.error(OperationType.WRITE, message, e));
                                }
                                clientStreams.get(clientId).flush();
                                break;
                            case "open":
                                // open [hostname] [path] [mode]
                                if (rpc.length < 4) {
                                    throwArgumentMismatchError(OperationType.OPEN, message);
                                    break;
                                }
                                hostname = rpc[1];
                                p = Paths.get(rpc[2]);
                                Mode mode = Mode.parseMode(rpc[3]);
                                if (mode == null) break;
                                try {
                                    openFile(hostname, p, clientId, mode);
                                    clientStreams.get(clientId).writeObject(EntryServerResponse.ok(OperationType.OPEN, message, true));
                                } catch (EntryServerException e) {
                                    clientStreams.get(clientId).writeObject(EntryServerResponse.error(OperationType.OPEN, message, e));
                                }
                                clientStreams.get(clientId).flush();
                                break;
                            case "close":
                                // close [hostname] [path]
                                if (rpc.length < 3) {
                                    throwArgumentMismatchError(OperationType.CLOSE, message);
                                    break;
                                }
                                hostname = rpc[1];
                                p = Paths.get(rpc[2]);

                                // closeしたユーザーがwrite権限を持っていたら、他のユーザーのキャッシュを無効にする
                                String path = joinFilePath(hostname, p);
                                FileUserGroup group = fileUserGroups.get(path);
                                if (group != null && group.allowWrite(clientId) && isDirty.get(path)) {
                                    broadcastObject(clientId, String.format("invalidate %s %s", hostname, p));
                                }

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

                // closeされていないファイルをcloseする
                for (Map.Entry<String, FileUserGroup> entry : fileUserGroups.entrySet()) {
                    FileUserGroup group = entry.getValue();
                    group.removeUser(clientId);
                }
            }
        }

        /**
         * 他の接続クライアントに対してメッセージをブロードキャストする
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
