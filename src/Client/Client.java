package src.Client;


import src.CacheHandler.CacheHandler;
import src.Mode.Mode;

/**
* 分散ファイルシステムを使用するクライアント
* @author　Kaito Kimura
*/

/**
* EntryServerやFileServerが存在しない仮定でのClientクラスの基本形
* 内部でCacheHandlerをもち、このcacheHandlerに対してread()、write()を行う
*
*
*/
public class Client {
    private CacheHandler cacheHandler;

    public Client() {
        this.cacheHandler = new CacheHandler(0);
    }

    public boolean open(String filePath, Mode fileMode) {
        return cacheHandler.openFileContent(filePath, fileMode);
    }

    public boolean close(String filePath) {
        return cacheHandler.closeFileContent(filePath);
    }

    /**
    * readメソッド
    * Fileの内容を表示する
    * ClientがあるFileの内容を表示したい場面
    * @param filePath 見つけたいFileのパス
    * @return void
    */
    public void read(String filePath) {
        String fileContent = cacheHandler.getFileContent(filePath);
        System.out.println(fileContent);
    }

    /**
    * writeメソッド
    * Fileの内容をString変換して返す
    * ClientがあるFileの内容を書き換える場面
    * @param fileName 見つけたいFileのパス
    * @param text Fileに書き込みたい内容
    * @return void
    */
    public void write(String filePath, String text) {
        int res = cacheHandler.setFileContent(filePath, text);

        // cacheHandlerがsetFileContent()に失敗した
        if (res<0) {
            System.out.println("Failed to write");
            return;
        }
        System.out.println("Success!");
    }
}
