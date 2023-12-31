package test.FileCache;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import src.File.File;
import src.FileCache.FileCache;

/**
* FileCacheクラスの単体テスト
* @author　Kaito Kimura
* @author　kei-0917
*/

public class FileCacheTest {
    /*
    Example Tree Structure of File Server: 
                   root
                  /    \
                 a      d
               /   \
              b     c
              |
          test.txt
    
    Initial Tree Structure of File Cache: 
                   root
    
    Final Tree Structure of File Cache: 
                   root
                  /
                 a
               /
              b
              |
          test.txt
    */

    FileCache fileCache;

    @Before
    public void setUp() {
        fileCache = new FileCache();
    }

    @Test
    public void testSetFile() {
        String filePath = "/a/b/test.txt";
        File file = new File.Builder(1, "test.txt")
                        .isReadAllowed(true).isWriteAllowed(true).build();

        File obtainedFile = fileCache.getFile(filePath);
        assertEquals(null, obtainedFile);
        
        fileCache.setFile(filePath, file);

        obtainedFile = fileCache.getFile(filePath);
        assertEquals("test.txt", obtainedFile.getFileName());
    }
}
