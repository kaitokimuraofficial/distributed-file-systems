package src.File;

import java.util.Arrays;
import java.util.Date;
import java.lang.Math;
import java.nio.charset.StandardCharsets;

/**
* 1つのファイルに関する情報や内容を持つ
* @author　Kaito Kimura
*/

public class File {
    private final int DEFAULTSIZE;
    private final Date creationDate;
    private final int createdBy;

    private String fileName;
    private Date lastModifiedDate;
    private int lastModiiedBy;
    private int readCount = 0;
    private int writeCount = 0;
    private Boolean isReadAllowed;
    private Boolean isWriteAllowed;
    
    private byte[] fileContent;
    private int lastPosition = -1;

    public static class Builder {
        private final int DEFAULTSIZE = 4096;
        private final Date creationDate;
        private final int createdBy;

        private String fileName;
        private Boolean isReadAllowed = false;
        private Boolean isWriteAllowed = false;
        private byte[] fileContent = new byte[DEFAULTSIZE];

        public Builder(int createdBy, String fileName) {
            this.creationDate = new Date();
            this.createdBy = createdBy;
            this.fileName = fileName;
        }

        public Builder isReadAllowed(Boolean val) {
            isReadAllowed = val; return this;
        }

        public Builder isWriteAllowed(Boolean val) {
            isWriteAllowed = val; return this;
        }

        public File build() {
            return new File(this);
        }
    }

    private File(Builder builder) {
        DEFAULTSIZE = builder.DEFAULTSIZE;
        creationDate = builder.creationDate;
        createdBy = builder.createdBy;

        fileName = builder.fileName;
        lastModifiedDate = builder.creationDate;
        lastModiiedBy = builder.createdBy;
        isReadAllowed = builder.isReadAllowed;
        isWriteAllowed = builder.isWriteAllowed;
        fileContent = builder.fileContent;
    }

    // getter method
    public Date getCreationDate() {
        return creationDate;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public String getFileName() {
        return fileName;
    }
    
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }
    
    public int getLastModiiedBy() {
        return lastModiiedBy;
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