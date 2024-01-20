package test.client;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import src.client.CacheHandler;
import src.file.File;
import src.util.Mode;

@RunWith(Enclosed.class)
public class CacheHandlerTest {

    public static class OpenされたFileを操作するとき {

        final int OWNER = 0;
        CacheHandler cacheHandler;
        File file;
        Mode mode;

        @Before
        public void setup() {
            cacheHandler = new CacheHandler(OWNER);
            file = new File("test.txt", true, true);
            file.setFileContent("a".repeat(100).getBytes());
            mode = Mode.RW;
        }

        @Test
        public void CacheHandlerのisOperationAllowedが正しく動く() {
            assertEquals(
                false,
                cacheHandler.isOperationAllowed("test.txt", "read")
            );
            assertEquals(
                false,
                cacheHandler.isOperationAllowed("test.txt", "write")
            );
            cacheHandler.openFile("test.txt", file, mode);
            assertEquals(
                true,
                cacheHandler.isOperationAllowed("test.txt", "read")
            );
            assertEquals(
                true,
                cacheHandler.isOperationAllowed("test.txt", "write")
            );
        }

        @Test
        public void CacheHandlerのgetOpenedFileModeが正しく動く() {
            assertEquals(null, cacheHandler.getOpenedFileMode("test.txt"));
            cacheHandler.openFile("test.txt", file, mode);
            assertEquals(mode, cacheHandler.getOpenedFileMode("test.txt"));
        }

        @Test
        public void 存在しないFileに対してsetIsCachedをするとfalseを返し存在するものはtrueを返す() {
            assertEquals(
                false,
                cacheHandler.setIsCacheValid("notExist.txt", true)
            );
            cacheHandler.openFile("test.txt", file, mode);
            assertEquals(true, cacheHandler.setIsCacheValid("test.txt", false));
        }
    }

    public static class FileをCloseするとき {

        final int OWNER = 0;
        CacheHandler cacheHandler;
        File file;
        Mode mode;

        @Before
        public void setup() {
            cacheHandler = new CacheHandler(OWNER);
            file = new File("exist.txt", true, true);
            file.setFileContent("a".repeat(100).getBytes());
            mode = Mode.RW;
            cacheHandler.openFile("exist.txt", file, mode);
        }

        @Test
        public void 存在しないFileをcloseするとopenedFilesはそのままでtrueを返す() {
            assertEquals(null, cacheHandler.getOpenedFileMode("notExist.txt"));
            assertEquals(true, cacheHandler.closeFile("notExist.txt"));
        }

        @Test
        public void OpenされたFileをcloseするとopenedFilesからfileを削除してtrueを返す() {
            assertEquals(mode, cacheHandler.getOpenedFileMode("exist.txt"));
            assertEquals(true, cacheHandler.closeFile("exist.txt"));
            assertEquals(null, cacheHandler.getOpenedFileMode("exist.txt"));
        }
    }
}
