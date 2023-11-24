package test.CacheExecuter;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import src.CacheExecuter.CacheExecuter;
import src.File.File;

public class CacheExecuterTest {
    File file1;
    CacheExecuter cacheExecuter1;

    @Before
    public void setUp() {
        file1 = new File.Builder(1, "test")
                .isReadAllowed(true).isWriteAllowed(true).build();
        cacheExecuter1 = new CacheExecuter.Builder(0).build();
        cacheExecuter1.setFile(file1);
    }

    @Test
    public void testGetFile() {
        assertEquals(file1, cacheExecuter1.getFile());
    }

    @Test
    public void testGetOwnedBy() {
        assertEquals(0, cacheExecuter1.getOwnedBy());
    }

    @Test
    public void testSetFile() {
        File file2 = new File.Builder(1, "test2")
                .isReadAllowed(true).isWriteAllowed(true).build();
        cacheExecuter1.setFile(file2);
        assertEquals(file2, cacheExecuter1.getFile());
    }
}
