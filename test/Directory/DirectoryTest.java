package test.Directory;

import java.util.ArrayDeque;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import src.Directory.Directory;
import src.File.File;

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
        file = new File.Builder(1, "test.txt")
                    .isReadAllowed(true).isWriteAllowed(true).build();
        b = new Directory("b", null, new File[]{file});
        c = new Directory("c", null, null);
        a = new Directory("a", new Directory[]{b, c}, null);
        d = new Directory("d", null, null);
        root = new Directory("root", new Directory[]{a, d}, null);
    }

    @Test
    public void testSearch() {
        File file1 = root.getFile(new ArrayDeque<String>(java.util.List.of("a", "b", "test.txt")));
        assertEquals("test.txt", file1.getFileName());

        File file2 = root.getFile(new ArrayDeque<String>(java.util.List.of("a", "b", "tmp.txt")));
        assertEquals(null, file2);
    }
}
