@echo off
setlocal enabledelayedexpansion

rem スクリプトを実行したディレクトリを取得
set "scriptDir=%~dp0"

rem コンパイル対象のディレクトリをスクリプトのディレクトリに設定
set "targetDir=%scriptDir%\src"

rem コンパイルオプションを指定（必要に応じて変更）
set "compileOptions=-encoding UTF-8"

rem 指定ディレクトリ以下のすべてのJavaファイルに対してjavacを実行
for /r "%targetDir%" %%i in (*.java) do (
    javac %compileOptions% "%%i"
    if !errorlevel! neq 0 (
        echo Compile Error
        exit /b 1
    )
)

echo Compile Tasks Complete Successfully
exit /b 0
