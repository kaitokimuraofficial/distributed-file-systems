package src.util;

/**
 * ファイルの読み書きモードを表す列挙型
 * @author Aoyagi Tomoya
 */
public enum Mode {
    READ(true, false),
    WRITE(false, true),
    RW(true, true);

    /** 読み出し可能かを表すフラグ */
    private final boolean canRead;
    /** 書き込み可能かを表すフラグ */
    private final boolean canWrite;

    private Mode(boolean canRead, boolean canWrite) {
        this.canRead = canRead;
        this.canWrite = canWrite;
    }

    /**
     * 権限が読み出し可能かを返す
     */
    public boolean canRead() {
        return canRead;
    }

    /**
     * 権限が書き込み可能かを返す
     */
    public boolean canWrite() {
        return canWrite;
    }
}
