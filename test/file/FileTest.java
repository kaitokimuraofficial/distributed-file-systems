package test.file;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import src.file.File;

/**
* Fileクラスの単体テスト
* @author　Kaito Kimura
*/

public class FileTest {
    private File file1;

    @Before
    public void setup() {
        file1 = new File("test", true, true);
    }

    @Test
    public void testGetFileName() {
        assertEquals("test", file1.getFileName());
    }
    
    @Test
    public void testGetIsReadAllowed() {
        assertEquals(true, file1.getIsReadAllowed());
    }
    
    @Test
    public void testGetIsWriteAllowed() {
        assertEquals(true, file1.getIsWriteAllowed());
    }

    @Test
    public void testSetFileContent() {
        byte[] text = "test".getBytes();
        file1.setFileContent(text);
        assertEquals(4, file1.setFileContent("test".getBytes()));
    }

    @Test
    public void testGetLastPosition() {
        byte[] text = "test".getBytes();
        file1.setFileContent(text);
        assertEquals(4, file1.getLastPosition());
    }

    @Test
    public void testGetFileContentWhenLengthIsWithinLimit() {
        byte[] text = "test".getBytes();
        file1.setFileContent(text);
        assertArrayEquals(text, file1.getFileContent());
    }

    @Test
    public void testGetFileContentWhenLengthIsZero() {
        byte[] text = "".getBytes();
        file1.setFileContent(text);
        assertArrayEquals(text, file1.getFileContent());
    }

    @Test
    public void testGetFileContentWhenLengthIsOverDEFAULTSIZE() {
        byte[] text = "test".repeat(4096).getBytes();
        file1.setFileContent(text);
        byte[] textExpected = "test".repeat(1024).getBytes();
        assertArrayEquals(textExpected, file1.getFileContent());
    }

    @Test
    public void testGetFileContentWithSomeSetContent() {
        byte[] text1 = "test".getBytes();
        byte[] text2 = "I am using Java.".getBytes();
        file1.setFileContent(text1);
        file1.setFileContent(text2);
        assertArrayEquals(text2, file1.getFileContent());
        assertEquals(16, file1.getLastPosition());
    }

    @Test
    public void testSetFileName() {
        file1.setFileName("test");
        assertEquals("test", file1.getFileName());
    }

    @Test
    public void testSetIsReadAllowed() {
        file1.setIsReadAllowed(false);
        assertEquals(false, file1.getIsReadAllowed());
    }

    @Test
    public void testSetIsWriteAllowed() {
        file1.setIsWriteAllowed(false);
        assertEquals(false, file1.getIsWriteAllowed());
    }
}
