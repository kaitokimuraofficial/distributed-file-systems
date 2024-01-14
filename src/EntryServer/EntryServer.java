package src.EntryServer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
// import java.util.Date;

// import java.util.HashMap;
// import java.util.Map;

// import src.Client.Client;
import src.File.File;

/**
* クライアントと直線通信を行うファイルサーバーのエントリ
* @author　Kaito Kimura
*/

public class EntryServer {
    public static final int PORT = 8080;
    // private final int BACKLOG;
    // private final ServerSocket clientEntry;
    
    // private Map<Client, Integer> clientIdMap = new HashMap<>();
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ServerSocket s = new ServerSocket(PORT); // ソケットを作成する
        System.out.println("Started: " + s);
        try {
            Socket socket = s.accept(); // コネクション設定要求を待つ
            try {
                System.out.println("Connection accepted: " + socket);
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream()); // データ受信用バッファの設定
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()); // 送信バッファ設定
                while (true) {
                    File receivedFile = (File) in.readObject(); // データの受信
                    if (receivedFile == null) break;
                    System.out.println("Echoing : ");
                    out.writeObject(receivedFile); // データの送信
                }
            } finally {
                System.out.println("closing...");
                socket.close();
            }
        } finally {
            s.close();
        }
    }
}
