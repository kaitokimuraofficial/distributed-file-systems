package src.Directory;

import java.util.Arrays;
import java.util.ArrayDeque;
import java.util.ArrayList;

import src.File.File;

/**
* ディレクトリ
* @author　kei-0917
*/

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

    public ArrayDeque<String> convertStringToArrayList(String filePath) {
        String[] filePathArray = filePath.split("/");
        ArrayDeque<String> filePathArrayList = new ArrayDeque<String>(Arrays.asList(filePathArray).subList(1, filePathArray.length));
        return filePathArrayList;
    }

    public File getFile(String filePath) {
        return getFile(this.convertStringToArrayList(filePath));
    }

    public void setFile(String filePath, File updatedFile) {
        setFile(this.convertStringToArrayList(filePath), updatedFile);
    }

    private File getFile(ArrayDeque<String> filePath) {
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

    private void setFile(ArrayDeque<String> filePath, File updatedFile) {
        if (filePath.isEmpty()) return;
        final String name = filePath.poll();

        if (filePath.size() > 0) {
            boolean foundDirectory = false;
            for (Directory directory : this.directories) {
                if (name.equals(directory.getDirName())) {
                    foundDirectory = true;
                    directory.setFile(filePath, updatedFile);
                    break;
                }
            }
            if (!foundDirectory) {
                Directory directory = new Directory(name, new ArrayList<Directory>(), new ArrayList<File>());
                directory.setFile(filePath, updatedFile);
                this.directories.add(directory);
            }
        }
        else {
            for (File file : this.files) {
                if (name.equals(file.getFileName())) {
                    file.setFileContent(new String(updatedFile.getFileContent()));
                    return;
                }
            }
            this.files.add(updatedFile);
        }
    }
}
