package src;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Date;

import java.util.HashMap;
import java.util.Map;

import src.Client.Client;

/**
* クライアントと直線通信を行うファイルサーバーのエントリ
* @author　Kaito Kimura
*/

public class EntryServer {
    private final int PORT;
    private final int BACKLOG;
    private final ServerSocket clientEntry;
    
    private Map<Client, Integer> clientIdMap = new HashMap<>();
    
    public static class Builder {
        private static final int PORT = 8080;
        private static final int BACKLOG = 8081;

        private ServerSocket clientEntry;

        public Builder() {
            try {
                this.clientEntry = new ServerSocket(PORT, BACKLOG);
            } catch(IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        public EntryServer build() {
            return new EntryServer(this);
        }
    }

    private EntryServer(Builder builder) {
        PORT = builder.PORT;
        BACKLOG = builder.BACKLOG;
        clientEntry = builder.clientEntry;
    }

    public void start() {
        
    }

}
