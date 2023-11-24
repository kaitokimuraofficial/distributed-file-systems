package docsTemplate;

/**
* Javadoc用コメントのテストプログラム
* @author　Kaito Kimura
*/
 
public class JavadocTemplate{
 
   /**
   * doubleFuncメソッド
   * 引数を2倍にして返す
   * @param number 整数値
   * @return 引数を2倍した整数値
   */
   public static int doubleFunc(int number){
      // 引数の10を2倍して返します
      return number * 2;
   }
   
   /**
   * mainメソッド
   * @param args オプション
   * doubleメソッドに引数を渡して結果を出力する
   */
   public static void main(String[] args){
      // numberにdouble関数の結果を代入します
      int number = doubleFunc(10);
      // 結果を出力します
      System.out.println(number);
   }
}