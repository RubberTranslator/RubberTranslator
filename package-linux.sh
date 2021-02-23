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
jpackage=/home/raven/Software/jdk-14.0.2/bin/jpackage
jfxmods_path=/home/raven/Software/javafx-jmods-11.0.2

# Launcher package
${jpackage} --name Launcher --input out/artifacts/Launcher_jar  --main-jar Launcher.jar \
 --java-options "-Dprism.verbose=true" \
  --dest out \
  --type app-image \
  --main-class Launcher \
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
cp -r out/Main/* out/Launcher

# 改名
mv out/Launcher out/RubberTranslator

# 打包成deb -- 暂时还是不考虑安装包，因为安装包在自动升级时可能会出现错误（配置可能不兼容)
#${jpackage} --name RubberTranslator \
#   --dest out \
#   --type deb  \
#   --app-image out/RubberTranslator \
#   --vendor raven \
#   --linux-shortcut \
#   --linux-menu-group "RubberTranslator" \
#   --app-version "3.5.1"
