package src.Directory;

import java.util.ArrayDeque;
import java.util.ArrayList;

import src.File.File;

public class Directory {
    protected String dirName;
    protected ArrayList<Directory> directories;
    protected ArrayList<File> files;
    
    public Directory(String dirName, ArrayList<Directory> directories, ArrayList<File> files) {
        this.dirName = dirName;
        this.directories = directories;
        this.files = files;
    }

    public String getDirName() {
        return this.dirName;
    }

    public File getFile(ArrayDeque<String> filePath) {
        File obtainedFile = null;

        if (filePath.isEmpty()) return null;
        final String name = filePath.poll();
        
        if (filePath.size() > 0) {
            for (Directory directory : this.directories) {
                if (name.equals(directory.getDirName())) {
                    obtainedFile = directory.getFile(filePath);
                }
            }
        }
        else {
            for (File file : this.files) {
                if (name.equals(file.getFileName())) {
                    return file;
                }
            }
        }

        return obtainedFile;
    }
}
