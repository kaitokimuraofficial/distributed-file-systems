package src.util;

/**
 * ファイルの読み書きモードを表す列挙型
 * @author Aoyagi Tomoya
 */
public enum Mode {
    READ(true, false, "r"),
    WRITE(false, true, "w"),
    RW(true, true, "rw");

    /** 読み出し可能かを表すフラグ */
    private final boolean canRead;
    /** 書き込み可能かを表すフラグ */
    private final boolean canWrite;
    private final String abbrev;

    private Mode(boolean canRead, boolean canWrite, String abbrev) {
        this.canRead = canRead;
        this.canWrite = canWrite;
        this.abbrev = abbrev;
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

    /**
     * 文字列からModeの値を取得する
     * @param abbrev FileModeを表す文字列
     * @return Modeの値
     */
    public static Mode parseMode(String abbrev) {
        for (Mode mode : Mode.values()) {
            if (mode.abbrev.equals(abbrev)) {
                return mode;
            }
        }
        return null;
    }

}