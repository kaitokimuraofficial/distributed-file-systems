package src.file;

import java.io.Serializable;
import java.lang.Math;
import java.util.Arrays;

/**
* ファイル
* @author  kaitokimuraofficial
* @author  Keisuke Nakao
*/

public class File implements Serializable {
    private final int DEFAULTSIZE = 4096;

    private String fileName;
    private byte[] fileContent = new byte[DEFAULTSIZE];
    private Boolean isReadAllowed;
    private Boolean isWriteAllowed;
    private int lastPosition = -1;

    public File(String fileName, Boolean isReadAllowed, Boolean isWriteAllowed) {
        this.fileName = fileName;
        this.isReadAllowed = isReadAllowed;
        this.isWriteAllowed = isWriteAllowed;
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

    public int getLastPosition() {
        return lastPosition;
    }
    
    /**
    * getFileContentメソッド
    * ファイルの内容を表すbyte配列を返す
    * @param 
    * @return fileContentのbyte列
    */
    public byte[] getFileContent() {
        if (isReadAllowed == false) {
            return null;
        }

        if (lastPosition < 0) {
            return null;
        }

        // startからendまでの部分配列を取得
        byte[] data = Arrays.copyOfRange(fileContent, 0, lastPosition);
        return data;
    }

    // setter method
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public void setIsReadAllowed(Boolean isReadAllowed) {
        this.isReadAllowed = isReadAllowed;
    }

    public void setIsWriteAllowed(Boolean isWriteAllowed) {
        this.isWriteAllowed = isWriteAllowed;
    }

    private void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    } 

    /**
    * setFileContentメソッド
    * ファイルの内容を書き換える
    * @param text 書き込む内容
    * @return 書き込んだStringの長さか、失敗したら-1
    */
    public int setFileContent(byte[] data) {
        if (isWriteAllowed == false) {
            return -1;
        }

        for (int i = 0; i < data.length; i++) {
            if (i < DEFAULTSIZE) {
                fileContent[i] = data[i];
            } else {
                break;
            }
        }
        setLastPosition(Math.min(data.length, DEFAULTSIZE));
        return this.lastPosition;
    }
}