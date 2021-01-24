# 打包流程
# 1. 在IDEA中 Build-->Build Artifacts
# 2. 设置jpackage和javafx mods变量到合适路径
# 3. 执行本脚本即可
# 注：打包后的软件包在 out/RubberTranslator 路径下

# 清空文件夹
rm -rf out/Launcher
rm -rf out/Main
rm -rf out/RubberTranslator

# set jpackage and jfxmod path
jpackage=/home/raven/Downloads/jdk-14.0.2/bin/jpackage
jfxmods_path=/home/raven/Downloads/javafx-jmods-11.0.2

# Launcher package
${jpackage} --name Launcher --input out/artifacts/Launcher_jar  --main-jar Launcher.jar \
 --java-options "-Dprism.verbose=true" \
  --dest out \
  --type app-image \
  --module-path ${jfxmods_path} \
  --main-class Launcher \
  --add-modules javafx.controls,javafx.graphics,javafx.fxml \
  --vendor raven

# Main package
${jpackage} --name Main --input out/artifacts/Main_jar  --main-jar Main.jar  \
  --java-options "-Dprism.verbose=true" \
  --dest out \
  --type app-image \
  --module-path ${jfxmods_path} \
  --main-class com.rubbertranslator.App \
  --add-modules javafx.controls,javafx.graphics,javafx.fxml \
  --vendor raven

# 移动
mv out/Launcher/bin/* out/Main/bin
mv out/Launcher/lib/app/* out/Main/lib/app

# 改名
mv out/Main out/RubberTranslator