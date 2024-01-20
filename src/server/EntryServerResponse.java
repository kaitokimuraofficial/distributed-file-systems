package src.server;

import java.io.Serializable;
import src.util.OperationType;

/**
 * EntryServerからClientに返すレスポンスを表すクラス
 */
public class EntryServerResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    private final OperationType opType;
    /** Clientから受け取った文字列 */
    private final String receivedCommand;
    private final boolean isSuccessful;
    private final Object data;

    private EntryServerResponse(
        OperationType opType,
        String receivedCommand,
        boolean isSuccessful,
        Object data
    ) {
        this.opType = opType;
        this.receivedCommand = receivedCommand;
        this.isSuccessful = isSuccessful;
        this.data = data;
    }

    /**
     * 正常終了時のレスポンスを返す
     * @param opType 操作の種類
     * @param receivedCommand 受信したコマンド
     * @param data レスポンスとして返すデータ
     * @return EntryServerResponse
     */
    public static EntryServerResponse ok(
        OperationType opType,
        String receivedCommand,
        Object data
    ) {
        return new EntryServerResponse(opType, receivedCommand, true, data);
    }

    /**
     * 例外発生時のレスポンスを返す
     * @param opType 操作の種類
     * @param receivedCommand 受信したコマンド
     * @param e 発生した例外
     * @return EntryServerResponse
     */
    public static EntryServerResponse error(
        OperationType opType,
        String receivedCommand,
        Exception e
    ) {
        return new EntryServerResponse(opType, receivedCommand, false, e);
    }

    public OperationType getOpType() {
        return opType;
    }

    public String getReceivedCommand() {
        return receivedCommand;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public Object getData() {
        return data;
    }
}
