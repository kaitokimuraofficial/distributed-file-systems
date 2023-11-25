package src.File;

import java.util.Date;

/**
* 分散ファイルシステムで管理されるファイル
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

    public int write(String text) {
        if (isWriteAllowed == false) {
            return -1;
        }

        byte[] strBytes = text.getBytes();

        for (int i = 0; i < strBytes.length; i++) {
            if (i < DEFAULTSIZE) {
                fileContent[i] = strBytes[i];
            } else {
                break; // 配列のサイズを超えたら終了
            }
        }
        return strBytes.length;
    }

    public int read() {
        if (isReadAllowed == false) {
            return -1;
        }
        String str = fileContent.toString();
        System.out.println(str);
        return str.length();
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
}