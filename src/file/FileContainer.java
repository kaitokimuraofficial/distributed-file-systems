package src.file;

import java.io.File;

/**
 * ファイル
 * @author Keisuke Nakao
 */

public class FileContainer extends File {

    private byte[] fileContent;

    public FileContainer(String pathName, byte[] fileContent) {
        super(pathName);
        this.fileContent = fileContent;
    }

    public byte[] getFileContent() {
        return this.fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }
}
