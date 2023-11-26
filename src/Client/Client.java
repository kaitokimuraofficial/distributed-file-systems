package src.Client;


import src.CacheExecuter.CacheExecuter;

/**
* 分散ファイルシステムを使用するクライアント
* @author　Kaito Kimura
*/

/**
* EntryServerやFileServerが存在しない仮定でのClientクラスの基本形
* 内部でCacheExecuterをもち、このcacheExecuterに対してread()、write()を行う
*
*
*/
public class Client {
    private CacheExecuter cacheExecuter;

    public Client() {

    }

    /**
    * readメソッド
    * Fileの内容を表示する
    * ClientがあるFileの内容を表示したい場面
    * @param filePath 見つけたいFileのパス
    * @return void
    */
    public void read(String filePath) {
        String fileContent = cacheExecuter.getFileContent(filePath);
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
        int res = cacheExecuter.setFileContent(filePath, text);

        // cacheExecuterがsetFileContent()に失敗した
        if (res<0) {
            System.out.println("Failed to write");
            return;
        }
        System.out.println("Success!");
    }
}
