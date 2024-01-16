# スクリプトを実行したディレクトリを取得
scriptDir="$(cd "$(dirname "$0")" && pwd)"

# コンパイル対象のディレクトリをスクリプトのディレクトリに設定
targetDir="$scriptDir/src"

# コンパイルオプションを指定（必要に応じて変更）
compileOptions="-encoding UTF-8"

# 指定ディレクトリ以下のすべてのJavaファイルに対してjavacを実行
find "$targetDir" -name "*.java" | while read -r javaFile; do
    javac $compileOptions "$javaFile"
    if [ $? -ne 0 ]; then
        echo "Compile Error"
        exit 1
    fi
done

echo "Compile Tasks Complete Successfully"
exit 0