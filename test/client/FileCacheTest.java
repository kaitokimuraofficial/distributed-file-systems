package test.client;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import src.file.File;
import src.client.FileCache;

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
    String filePath;
    File file;

    @Before
    public void setUp() {
        fileCache = new FileCache();
        filePath = "/a/b/test.txt";
        file = new File("test.txt", true, true);
    }

    @Test
    public void 存在しないファイルをsetFileをすると新規にファイルが作成される() {            
        File obtainedFile = fileCache.getFile(filePath);
        assertEquals(null, obtainedFile);
        fileCache.setFile(filePath, file);
        obtainedFile = fileCache.getFile(filePath);
        assertEquals("test.txt", obtainedFile.getFileName());
    }
}
