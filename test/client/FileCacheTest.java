package test.client;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import src.client.FileCache;
import src.file.File;

/**
* FileCacheクラスの単体テスト
* @author  kaitokimuraofficial
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
        File file = new File(1, "test.txt", true, true);

        File obtainedFile = fileCache.getFile(filePath);
        assertEquals(null, obtainedFile);
        
        fileCache.setFile(filePath, file);

        obtainedFile = fileCache.getFile(filePath);
        assertEquals("test.txt", obtainedFile.getFileName());
    }
}
