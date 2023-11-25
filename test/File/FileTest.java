package test.File;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import java.nio.charset.StandardCharsets;

import org.junit.Before;
import org.junit.Test;

import src.File.File;

/**
* Fileクラスの単体テスト
* @author　Kaito Kimura
*/

public class FileTest {
    private File file1;

    @Before
    public void setup() {
        file1 = new File.Builder(1, "test").
        isReadAllowed(true).isWriteAllowed(true).build();
        file1.setFileContent("FUCK");
    }

    @Test
    public void testGetCreatedBy() {
        assertEquals(1, file1.getCreatedBy());
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
    public void testGetLastModiiedBy() {
        assertEquals(1, file1.getCreatedBy());
    }
    
    @Test
    public void testSetFileContent() {
        assertEquals(4, file1.setFileContent("FUCK"));
    }

    @Test
    public void testGetFileContentWhenLengthIsWithinLimit() {
        String text = "SHIT";
        file1.setFileContent(text);
        assertArrayEquals(text.getBytes(StandardCharsets.US_ASCII), file1.getFileContent());
    }

    @Test
    public void testGetFileContentWhenLengthIsZero() {
        String text = "";
        file1.setFileContent(text);
        assertArrayEquals(text.getBytes(StandardCharsets.US_ASCII), file1.getFileContent());
    }

    @Test
    public void testGetFileContentWhenLengthIsOverDEFAULTSIZE() {
        String text = "FUCK".repeat(4096);
        file1.setFileContent(text);
        String textExpected = "FUCK".repeat(1024);
        assertArrayEquals(textExpected.getBytes(StandardCharsets.US_ASCII), file1.getFileContent());
    }

    @Test
    public void testSetFileName() {
        file1.setFileName("FUCK");
        assertEquals("FUCK", file1.getFileName());
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
