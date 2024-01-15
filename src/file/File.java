package src.file;

import java.io.Serializable;
import java.lang.Math;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
* ファイル
* @author  kaitokimuraofficial
*/

public class File implements Serializable {
    private final int createdBy;
    private final LocalDateTime creationDate;
    private final int DEFAULTSIZE = 4096;

    private String fileName;
    private byte[] fileContent = new byte[DEFAULTSIZE];
    private Boolean isReadAllowed;
    private Boolean isWriteAllowed;
    private int lastModifiedBy;
    private LocalDateTime lastModifiedDate;
    private int lastPosition = -1;
    private int readCount = 0;
    private int writeCount = 0;

    public File(int createdBy, String fileName, Boolean isReadAllowed, Boolean isWriteAllowed) {
        this.createdBy = createdBy;
        this.creationDate = LocalDateTime.now();
        this.fileName = fileName;
        this.isReadAllowed = isReadAllowed;
        this.isWriteAllowed = isWriteAllowed;
        this.lastModifiedDate = this.creationDate;
        this.lastModifiedBy = this.createdBy;
    }

    // getter method
    public int getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getFileName() {
        return fileName;
    }
    
    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }
    
    public int getLastModifiedBy() {
        return lastModifiedBy;
    }

    public int getReadCount() {
        return readCount;
    }

    public int getWriteCount() {
        return writeCount;
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
        byte[] asciiBytes = Arrays.copyOfRange(fileContent, 0, lastPosition);
        return asciiBytes;
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
    public int setFileContent(String text) {
        if (isWriteAllowed == false) {
            return -1;
        }

        byte[] asciiBytes = text.getBytes(StandardCharsets.US_ASCII);

        for (int i = 0; i < asciiBytes.length; i++) {
            if (i < DEFAULTSIZE) {
                fileContent[i] = asciiBytes[i];
            } else {
                break;
            }
        }
        setLastPosition(Math.min(asciiBytes.length, DEFAULTSIZE));
        return text.length();
    }
}