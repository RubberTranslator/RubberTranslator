REM 打包流程
REM 1. 在IDEA中 Build-->Build Artifacts
REM 2. 设置jpackage和javafx mods变量到合适路径
REM 3. 执行本脚本即可
REM 注：打包后的软件包在 out/RubberTranslator 路径下

set jpackage="C:\Program Files\Java\jdk-14.0.1\bin\jpackage.exe"
set jfxmods="C:\Program Files\Java\javafx-sdk-11.0.2\jmods"

:: 清空文件夹
rm -rf out/Launcher
rm -rf out/Main
rm -rf out/RubberTranslator

:: Launcher package
%jpackage% --name Launcher --input out/artifacts/Launcher_jar  --main-jar Launcher.jar  ^
  --dest out ^
  --type app-image ^
  --main-class Launcher ^
  --vendor raven

:: Main package
%jpackage% --name Main --input out/artifacts/Main_jar  --main-jar Main.jar  ^
  --dest out ^
  --type app-image ^
  --module-path %jfxmods% ^
  --main-class com.rubbertranslator.App ^
  --add-modules javafx.controls,javafx.graphics,javafx.fxml ^
  --vendor raven

:: 移动
mv out/Launcher/app/* out/Main/app
mv out/Launcher/Launcher* out/Main

:: 改名
mv out/Main out/RubberTranslator

:: copy 必读文件
cp misc/必读.txt out/
