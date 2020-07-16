jpackage --name RubberTranslator --input target  --main-jar RubberTranslator-1.0-SNAPSHOT.jar  ^
  --dest target ^
  --type msi ^
  --module-path "E:\JavaProjects\RubberTranslator\target\classes;C:\Users\a2855\.m2\repository\com\1stleg\jnativehook\2.1.0\jnativehook-2.1.0.jar;C:\Users\a2855\.m2\repository\com\google\code\gson\gson\2.8.6\gson-2.8.6.jar;C:\Users\a2855\.m2\repository\com\squareup\okhttp3\okhttp\4.6.0\okhttp-4.6.0.jar;C:\Users\a2855\.m2\repository\it\sauronsoftware\junique\1.0.4\junique-1.0.4.jar;C:\Users\a2855\.m2\repository\net\java\dev\jna\jna\4.1.0\jna-4.1.0.jar;C:\Users\a2855\.m2\repository\net\java\dev\jna\jna-platform\4.1.0\jna-platform-4.1.0.jar;C:\Users\a2855\.m2\repository\org\greenrobot\eventbus\3.1.1\eventbus-3.1.1.jar;C:\Users\a2855\.m2\repository\org\openjfx\javafx-base\11\javafx-base-11-win.jar;C:\Users\a2855\.m2\repository\org\openjfx\javafx-controls\11\javafx-controls-11-win.jar;C:\Users\a2855\.m2\repository\org\openjfx\javafx-fxml\11\javafx-fxml-11-win.jar;C:\Users\a2855\.m2\repository\org\openjfx\javafx-graphics\11\javafx-graphics-11-win.jar;C:\Users\a2855\.m2\repository\org\ow2\asm\asm\8.0.1\asm-8.0.1.jar" ^
  --main-class com.rubbertranslator.App ^
  --add-modules javafx.controls,javafx.graphics,javafx.fxml ^
  --vendor raven ^
  --win-dir-chooser --win-shortcut --win-menu-group "RubberTranslator" --win-menu
 ::java --module-path "C:\Program Files\Java\javafx-sdk-11.0.2\lib" --add-modules javafx.controls,javafx.graphics,javafx.fxml -jar RubberTranslator-1.0-SNAPSHOT.jar
