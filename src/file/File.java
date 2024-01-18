package src.file;

import java.io.Serializable;
import java.lang.Math;
import java.util.Arrays;

/**
 * ファイル
 * @author kaitokimuraofficial
 * @author Keisuke Nakao
 */

public class File implements Serializable {

    private final int DEFAULTSIZE = 4096;

    private String fileName;
    private byte[] fileContent = new byte[DEFAULTSIZE];
    private boolean isReadAllowed;
    private boolean isWriteAllowed;
    private boolean isCacheValid;
    private int lastPosition = -1;

    public File(
        String fileName,
        boolean isReadAllowed,
        boolean isWriteAllowed
    ) {
        this.fileName = fileName;
        this.isReadAllowed = isReadAllowed;
        this.isWriteAllowed = isWriteAllowed;
        this.isCacheValid = true;
    }

    // getter method
    public String getFileName() {
        return fileName;
    }

    public boolean getIsReadAllowed() {
        return isReadAllowed;
    }

    public boolean getIsWriteAllowed() {
        return isWriteAllowed;
    }

    public boolean getIsCacheValid() {
        return isCacheValid;
    }

    public int getLastPosition() {
        return lastPosition;
    }

    /**
     * getFileContentメソッド
     * ファイルの内容を表すバイト列を返す
     * @param
     * @return fileContentのバイト列
     */
    public byte[] getFileContent() {
        if (isReadAllowed == false) {
            return new byte[0];
        }

        if (lastPosition < 0) {
            return new byte[0];
        }

        // 0からlastPositionまでの部分配列を取得
        byte[] data = Arrays.copyOfRange(fileContent, 0, lastPosition);
        return data;
    }

    // setter method
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setIsReadAllowed(boolean isReadAllowed) {
        this.isReadAllowed = isReadAllowed;
    }

    public void setIsWriteAllowed(boolean isWriteAllowed) {
        this.isWriteAllowed = isWriteAllowed;
    }

    public void setIsCacheValid(boolean isCacheValid) {
        this.isCacheValid = isCacheValid;
    }

    private void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }

    /**
     * setFileContentメソッド
     * ファイルの内容を書き換える
     * @param data 書き込むバイト列
     * @return 書き込んだバイト列の長さか、失敗したら-1
     */
    public int setFileContent(byte[] byteData) {
        if (isWriteAllowed == false) {
            return -1;
        }

        for (int i = 0; i < byteData.length; i++) {
            if (i < DEFAULTSIZE) {
                fileContent[i] = byteData[i];
            } else {
                break;
            }
        }
        setLastPosition(Math.min(byteData.length, DEFAULTSIZE));
        return this.lastPosition;
    }
}
