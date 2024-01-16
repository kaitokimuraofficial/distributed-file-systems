package src.server.exception;

import java.io.Serial;

/**
 * エントリサーバーで発生した例外を表すクラス
 * @author Tomoya Aoyagi
 */
public class EntryServerException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public EntryServerException(String message) {
        super(message);
    }
}
