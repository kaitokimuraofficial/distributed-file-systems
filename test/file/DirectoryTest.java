package test.file;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import src.file.Directory;
import src.file.File;

/**
 * Directoryクラスの単体テスト
 * @author  kei-0917
 */

@RunWith(Enclosed.class)
public class DirectoryTest {

    public static class Directoryの中身が空のとき {

        File file;
        Directory root, a;

        @Before
        public void setUp() {
            root =
                new Directory(
                    "root",
                    new ArrayList<Directory>(),
                    new ArrayList<File>()
                );
        }

        @Test
        public void getFileをするとnullが返される() {
            File file1 = root.getFile("test.txt");
            assertEquals(file1, null);
        }

        @Test
        public void 存在しないファイルをsetFileをすると新規にファイルが作成される() {
            String filePath = "/a/b/test.txt";
            file = new File("test.txt", true, true);

            assertEquals(null, root.getFile(filePath));
            root.setFile(filePath, file);
            assertEquals("test.txt", root.getFile(filePath).getFileName());
        }
    }

    public static class Directoryの中身が空でないとき {

        /*
        Example Tree Structure: 
                   root
                  /    \
                 a      d
               /   \
              b     c
              |
          test.txt
        */

        File file;
        Directory root, a, b, c, d;

        @Before
        public void setUp() {
            file = new File("test.txt", true, true);
            b =
                new Directory(
                    "b",
                    new ArrayList<Directory>(),
                    new ArrayList<File>(List.of(file))
                );
            c =
                new Directory(
                    "c",
                    new ArrayList<Directory>(),
                    new ArrayList<File>()
                );
            a =
                new Directory(
                    "a",
                    new ArrayList<Directory>(List.of(b, c)),
                    new ArrayList<File>()
                );
            d =
                new Directory(
                    "d",
                    new ArrayList<Directory>(),
                    new ArrayList<File>()
                );
            root =
                new Directory(
                    "root",
                    new ArrayList<Directory>(List.of(a, d)),
                    new ArrayList<File>()
                );
        }

        @Test
        public void getFileが指定されたパスにあるファイルを返す() {
            File file1 = root.getFile("a/b/test.txt");
            assertEquals("test.txt", file1.getFileName());
        }

        @Test
        public void 指定されたパスが無効なときにgetFileはnullを返す() {
            File file1 = root.getFile("a/b/temp.txt");
            assertEquals(null, file1);
        }
    }
}
