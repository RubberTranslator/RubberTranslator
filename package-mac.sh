# 打包流程
# 1. 在IDEA中 Build-->Build Artifacts
# 2. 设置jpackage和javafx mods变量到合适路径
# 3. 执行本脚本即可
# 注：打包后的软件包在 out/RubberTranslator 路径下

# 清空文件夹
rm -rf out/RubberTranslator.app
rm -rf out/Launcher.app
rm -rf out/Main.app

# set jpackage and jfxmod path
jpackage=/Users/raven/Software/jdk-14.0.1.jdk/Contents/Home/bin/jpackage
jfxmods_path=/Users/raven/Software/javafx-jmods-11.0.2

# Launcher package
${jpackage} --name Launcher --input out/artifacts/Launcher_jar  --main-jar Launcher.jar \
  --dest out \
  --type app-image \
  --module-path ${jfxmods_path} \
  --main-class Launcher \
  --add-modules javafx.controls,javafx.graphics,javafx.fxml \
  --vendor raven \
  --java-options '--enable-preview'

# Main package
${jpackage} --name Main --input out/artifacts/Main_jar  --main-jar Main.jar  \
  --dest out \
  --type app-image \
  --module-path ${jfxmods_path} \
  --main-class com.rubbertranslator.App \
  --add-modules javafx.controls,javafx.graphics,javafx.fxml \
  --vendor raven  \
  --java-options '--enable-preview'

## 拷贝
# app dir
cp -rf out/Main.app/Contents/app/*  out/Launcher.app/Contents/app/
# MacOS dir
cp -rf out/Main.app/Contents/MacOS/* out/Launcher.app/Contents/MacOS/
# runtime dir
cp -rf out/Main.app/Contents/runtime/* out/Launcher.app/Contents/runtime/
# resource dir
cp -rf out/Main.app/Contents/Resources out/Launcher.app/Contents/Resources/

##
### 改名
#mv out/Launcher.app out/RubberTranslator.app