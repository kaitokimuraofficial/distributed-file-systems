package test.file;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.experimental.runners.Enclosed;
import org.junit.Test;
import org.junit.runner.RunWith;

import src.file.File;

/**
* Fileクラスの単体テスト
* @author Kaito Kimura
* @author Keisuke Nakao
*/

@RunWith(Enclosed.class)
public class FileTest {

    public static class FileのfileContentが空のとき {
        File file;
        @Before
        public void setup() {
            file = new File("test", true, true);
        }

        @Test
        public void getFileContentが空のバイト配列を返す() {
            assertEquals(Arrays.equals(file.getFileContent(), new byte[0]), true);
        }

        @Test
        public void DEFAULTSIZE未満の長さのバイト列をsetFileContentで書き込める() {
            int expectedLastPosition = 100;
            String stringData = "a".repeat(expectedLastPosition);
            byte[] expectedFileContent = stringData.getBytes();
            System.out.println(expectedFileContent.length);
            assertEquals(file.setFileContent(expectedFileContent), 100);
            assertEquals(new String(file.getFileContent()), stringData);
        }

        @Test
        public void DEFAULTSIZEの長さのバイト列をsetFileContentで書き込める() {
            int expectedLastPosition = 4096;
            String stringData = "a".repeat(expectedLastPosition);
            byte[] expectedFileContent = stringData.getBytes();
            assertEquals(file.setFileContent(expectedFileContent), 4096);
            assertEquals(new String(file.getFileContent()), stringData);
        }

        @Test
        public void DEFAULTSIZEより大きい長さのバイト列をsetFileContentで書き込むと4096までしか書き込めない() {
            int expectedLastPosition = 4100;
            String stringData = "a".repeat(expectedLastPosition);
            byte[] expectedFileContent = "a".repeat(4096).getBytes();
            assertEquals(file.setFileContent(stringData.getBytes()), 4096);
            assertEquals(new String(file.getFileContent()), new String(expectedFileContent));
        }
    }

    public static class FileのfileContentが空でないとき {
        File file;
        @Before
        public void setup() {
            String stringData = "a".repeat(100);
            byte[] expectedFileContent = stringData.getBytes();
            file = new File("test", true, true);
            file.setFileContent(expectedFileContent);
        }

        @Test
        public void setFileContentで書き換える() {
            int expectedLastPosition = 200;
            String stringData = "a".repeat(expectedLastPosition);
            byte[] expectedFileContent = stringData.getBytes();
            System.out.println(expectedFileContent.length);
            assertEquals(file.setFileContent(expectedFileContent), 200);
            assertEquals(new String(file.getFileContent()), stringData);
        }
    }

    public static class FileのisReadAllowedがfalseのとき {
        File file;
        @Before
        public void setup() {
            file = new File("test", false, true);
        }

        @Test
        public void getFileContentをすると空のバイト配列を返す() {
            assertEquals(Arrays.equals(file.getFileContent(), new byte[0]), true);
        }
    }

    public static class FileのisWriteAllowedがfalseのとき {
        File file;
        @Before
        public void setup() {
            file = new File("test", true, false);
        }

        @Test
        public void setFileContentをするとnullが返される() {
            String stringData = "a".repeat(100);
            byte[] byteData = stringData.getBytes();
            assertEquals(file.setFileContent(byteData), -1);
        }
    }
}
