package src.server;

import java.util.HashSet;
import java.util.Set;

public class FileUserGroup {

    private final Set<Integer> readUsers = new HashSet<>();
    private int currentWrite = -1;

    public void addUser(int userId, boolean canRead, boolean canWrite) {
        if (canRead) {
            readUsers.add(userId);
        }
        if (canWrite) {
            currentWrite = userId;
        }
    }

    public void removeUser(int userId) {
        readUsers.remove(userId);
        if (currentWrite == userId) {
            currentWrite = -1;
        }
    }

    /**
     * 現在書き込み権限付きでファイルを開いているユーザーがいるかどうかを返す
     * @return 書き込み権限付きでファイルを開いているユーザーがいればtrue、そうでなければfalse
     */
    public boolean hasCurrentWrite() {
        return currentWrite != -1;
    }

    /**
     * 書き込み権限付きでファイルを開けるかどうかを返す
     * @return 書き込み権限付きでファイルを開けるならtrue、そうでなければfalse
     */
    public boolean canWrite() {
        return !hasCurrentWrite() && readUsers.isEmpty();
    }

    /**
     * 指定されたユーザーがファイルの読み取り権限を持っているかどうかを返す
     * @param clientId クライアントID
     * @return 指定されたユーザーがファイルの読み取り権限を持っているならtrue、そうでなければfalse
     */
    public boolean allowRead(int clientId) {
        return readUsers.contains(clientId);
    }

    /**
     * 指定されたユーザーがファイルの書き込み権限を持っているかどうかを返す
     * @param clientId クライアントID
     * @return 指定されたユーザーがファイルの書き込み権限を持っているならtrue、そうでなければfalse
     */
    public boolean allowWrite(int clientId) {
        return currentWrite == clientId;
    }
}
