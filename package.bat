jpackage --name RubberTranslator --input target  --main-jar RubberTranslator-1.0-SNAPSHOT.jar  ^
  --dest target ^
  --type app-image ^
  --module-path "C:\Program Files\Java\javafx-sdk-11.0.2\jmods" ^
  --main-class com.rubbertranslator.App ^
  --add-modules javafx.controls,javafx.graphics,javafx.fxml ^
  --vendor raven
 ::--win-dir-chooser --win-shortcut --win-menu-group "RubberTranslator" --win-menu
 ::java --module-path "C:\Program Files\Java\javafx-sdk-11.0.2\lib" --add-modules javafx.controls,javafx.graphics,javafx.fxml -jar RubberTranslator-1.0-SNAPSHOT.jar

