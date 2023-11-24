package test.File;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import src.File.File;

/**
* Fileクラスの単体テスト
* @author　Kaito Kimura
*/

public class FileTest {
    File file1 = new File.Builder(1, "test").
    isReadAllowed(true).isWriteAllowed(true).build();

    @Test
    public void isReadAllowedIsTrue() {
        assertEquals(true, file1.getIsReadAllowed());
    }

    @Test
    public void isWriteAllowedIsTrue() {
        assertEquals(true, file1.getIsWriteAllowed());
    }

    @Test
    // 書き込まれるテキストが設定されたデフォルトサイズのバイト配列に収まる場合のテスト
    public void testWriteTextWithinLimit() {
        String testString = "FUCK";
        file1.write(testString);
        assertEquals(4, file1.write(testString));
    }

    @Test
    // テキストを読む
    public void testReadTextWithinLimit() {
        assertEquals(11, file1.read());
    }
}
