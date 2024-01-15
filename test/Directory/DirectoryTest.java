package test.Directory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import src.file.Directory;
import src.file.File;

/**
* Directoryクラスの単体テスト
* @author  kei-0917
*/

public class DirectoryTest {
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
        file = new File(1, "test.txt", true, true);
        b = new Directory("b", null, new ArrayList<File>(List.of(file)));
        c = new Directory("c", null, null);
        a = new Directory("a", new ArrayList<Directory>(List.of(b, c)), null);
        d = new Directory("d", null, null);
        root = new Directory("root", new ArrayList<Directory>(List.of(a, d)), null);
    }

    @Test
    public void testGetFile() {
        File file1 = root.getFile("/a/b/test.txt");
        assertEquals("test.txt", file1.getFileName());

        File file2 = root.getFile("/a/b/temp.txt");
        assertEquals(null, file2);
    }

    // setFile メソッドのテストは, FileCache クラスのテストで書いたので略.
}
