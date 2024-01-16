package src.server.exception;

/**
 * エントリサーバーで発生した例外を表すクラス
 * @author Tomoya Aoyagi
 */
public class EntryServerException extends Exception {
    private static final long serialVersionUID = 1L;

    public EntryServerException(String message) {
        super(message);
    }
}
