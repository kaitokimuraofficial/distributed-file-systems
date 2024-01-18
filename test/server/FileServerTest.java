package test.server;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import src.file.File;
import src.server.FileServer;

@RunWith(Enclosed.class)
public class FileServerTest {

    public static class readFileをするとき {

        FileServer fileServer;
        Path rootPath;

        @Before
        public void setup() {
            rootPath =
                Paths
                    .get(System.getProperty("user.dir"))
                    .resolve("test/server/root");
            fileServer = new FileServer(rootPath);
        }

        @Test
        public void 存在しないFileをreadしようとするとnullを返す() {
            Path notExistPath = rootPath.resolve("fileNotExist.txt");
            assertEquals(null, fileServer.readFile(notExistPath));
        }

        @Test
        public void 同一ディレクトリ内のFileをreadするとそのFileを返す() {
            Path existPath = rootPath.resolve("fileExist.txt");
            File file = fileServer.readFile(existPath);

            assertEquals("fileExist", new String(file.getFileContent()));
        }

        @Test
        public void 異なるディレクトリ内の存在しないFileをreadするとnullを返す() {
            Path notExistPath = rootPath.resolve("dir1/dir3/fileNotExist.txt");
            assertEquals(null, fileServer.readFile(notExistPath));
        }

        @Test
        public void 異なるディレクトリ内のFileをreadするとそのFileを返す() {
            Path existPath = rootPath.resolve("dir1/dir2/fileExist.txt");
            File file = fileServer.readFile(existPath);
            assertEquals("fileExist", new String(file.getFileContent()));
        }
    }

    public static class writeFileするとき {

        File fileExist;
        File fileNotExist;
        FileServer fileServer;
        Path rootPath;
        String stringData;
        byte[] expectedFileContent;

        @Before
        public void setup() {
            rootPath =
                Paths
                    .get(System.getProperty("user.dir"))
                    .resolve("test/server/root");

            stringData = "a".repeat(100);
            expectedFileContent = stringData.getBytes();

            fileNotExist = new File("fileNotExist.txt", true, true);
            fileNotExist.setFileContent(expectedFileContent);
            fileExist = new File("fileExist.txt", true, true);
            fileExist.setFileContent(expectedFileContent);
            fileServer = new FileServer(rootPath);
        }

        @Test
        public void 同一ディレクトリに存在しないFileをwriteしようとすると新たにFileを作成してtrueを返す() {
            Path notExistPath = rootPath.resolve("fileNotExist.txt");

            assertEquals(
                true,
                fileServer.writeFile(notExistPath, fileNotExist)
            );
            assertEquals(
                stringData,
                new String(fileServer.readFile(notExistPath).getFileContent())
            );
            try {
                Files.deleteIfExists(notExistPath);
                System.out.println("File deleted successfully.");
            } catch (IOException e) {
                System.err.println("Error deleting file: " + e.getMessage());
            }
        }

        @Test
        public void 同一ディレクトリ内のFileをwriteとすると書き換えてtrueを返す() {
            Path existPath = rootPath.resolve("fileExist.txt");

            assertEquals(
                "fileExist",
                new String(fileServer.readFile(existPath).getFileContent())
            );
            assertEquals(true, fileServer.writeFile(existPath, fileExist));
            assertEquals(
                stringData,
                new String(fileServer.readFile(existPath).getFileContent())
            );
            fileExist.setFileContent("fileExist".getBytes());
            fileServer.writeFile(existPath, fileExist);
            System.out.println("test passed successfully");
        }

        @Test
        public void 異なるディレクトリ内の存在しないFileをwriteすると新たにFileを作成してtrueを返す() {
            Path notExistPath = rootPath.resolve("dir1/dir3/fileNotExist.txt");

            assertEquals(
                true,
                fileServer.writeFile(notExistPath, fileNotExist)
            );
            assertEquals(
                stringData,
                new String(fileServer.readFile(notExistPath).getFileContent())
            );
            try {
                Files.deleteIfExists(notExistPath);
                System.out.println("File deleted successfully.");
            } catch (IOException e) {
                System.err.println("Error deleting file: " + e.getMessage());
            }
        }

        @Test
        public void 異なるディレクトリ内のFileをreadすると書き換えてtrueを返す() {
            Path existPath = rootPath.resolve("dir1/dir2/fileExist.txt");

            assertEquals(
                "fileExist",
                new String(fileServer.readFile(existPath).getFileContent())
            );
            assertEquals(true, fileServer.writeFile(existPath, fileExist));
            assertEquals(
                stringData,
                new String(fileServer.readFile(existPath).getFileContent())
            );
            fileExist.setFileContent("fileExist".getBytes());
            fileServer.writeFile(existPath, fileExist);
            System.out.println("test passed successfully");
        }
    }
}
