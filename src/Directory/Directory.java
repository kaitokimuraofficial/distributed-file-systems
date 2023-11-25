package src.Directory;

import java.util.ArrayDeque;

import src.File.File;

public class Directory {
    private String dirName;
    private Directory[] directories;
    private File[] files;
    
    public Directory(String dirName, Directory[] directories, File[] files) {
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
            for (Directory directory : directories) {
                if (name.equals(directory.getDirName())) {
                    obtainedFile = directory.getFile(filePath);
                }
            }
        }
        else {
            for (File file : files) {
                if (name.equals(file.getFileName())) {
                    return file;
                }
            }
        }

        return obtainedFile;
    }
}
