#
# 采用maven jfx插件打包方式（废弃，已改用jpackage打包)
# mac打包程序， mac采用maven打包，然后再做调整
# 1. 分别在Launcher和Main目录下执行 mvn jfx:native
# 2. 然后运行本脚本
#
#

# 拷贝Main模块到Launcher模块
cp -rf ./Main/target/jfx/native/Main.app/Contents/MacOS/Main ./Launcher/target/jfx/native/RubberTranslator.app/Contents/MacOS/
cp -rf ./Main/target/jfx/native/Main.app/Contents/Java/* ./Launcher/target/jfx/native/RubberTranslator.app/Contents/Java/

