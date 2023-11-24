package test.FileCache;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import src.File.File;
import src.FileCache.FileCache;

/**
* FileCacheクラスの単体テスト
* @author　Kaito Kimura
*/

public class FileCacheTest {
    File file1;
    FileCache fileCache1;

    @Before
    public void setUp() {
        file1 = new File.Builder(1, "test")
                .isReadAllowed(true).isWriteAllowed(true).build();
        fileCache1 = new FileCache.Builder().build();
        fileCache1.setFile(file1);
    }

    @Test
    public void testGetDEFAULTSIZE() {
        assertEquals(4096, fileCache1.getDEFAULTSIZE());
    }

    @Test
    public void testGetFile() {
        assertEquals(file1, fileCache1.getFile());
    }

    @Test
    public void testSetFileCach() {
        File file2 = new File.Builder(1, "test2").
        isReadAllowed(true).isWriteAllowed(true).build();
        fileCache1.setFile(file2);
        assertEquals(file2, fileCache1.getFile());
    }
}
