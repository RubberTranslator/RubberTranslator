package com.rubbertranslator.mvp.view.controller.impl;

import com.rubbertranslator.entity.ControllerFxmlPath;
import com.rubbertranslator.enumtype.SceneType;
import com.rubbertranslator.enumtype.TextAreaCursorPos;
import com.rubbertranslator.enumtype.TranslatorType;
import com.rubbertranslator.event.ClipboardContentInputEvent;
import com.rubbertranslator.event.SetKeepTopEvent;
import com.rubbertranslator.event.SwitchSceneEvent;
import com.rubbertranslator.mvp.modules.textinput.ocr.OCRUtils;
import com.rubbertranslator.mvp.presenter.PresenterFactory;
import com.rubbertranslator.mvp.presenter.impl.MultiTranslatePresenter;
import com.rubbertranslator.mvp.view.controller.IMultiTranslateView;
import com.rubbertranslator.system.SystemConfiguration;
import com.rubbertranslator.system.SystemResourceManager;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import sun.rmi.runtime.Log;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CompareModeController implements Initializable, IMultiTranslateView {
    @FXML
    private VBox rootPane;

    @FXML       // google translate
    private TextArea googleTextArea;
    @FXML      // baidu translate
    private TextArea baiduTextArea;
    @FXML
    private TextArea youdaoTextArea;

    @FXML
    private Button backBt;
    @FXML
    private Button translateBt;
    @FXML      // clear text
    private Button clearBt;

    @FXML
    private ToggleButton keepStageTopMenu;
    @FXML
    private ToggleButton incrementalCopyMenu;
    @FXML
    private ToggleButton textFormatMenu;
    @FXML
    private ToggleButton clipboardListenerMenu;
    @FXML
    private ToggleButton dragCopyMenu;

    // window stage 引用
    private Stage appStage;

    // 当前翻译后文本区cursor position
    private TextAreaCursorPos cursorPos = TextAreaCursorPos.START;

    // 是否继续接受来自clipboard的文本
    private boolean keepGetTextFromClipboard = true;

    private MultiTranslatePresenter presenter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        start();
    }

    @Override
    public void start() {
        initParams();
        initClickEvents();
        initListeners();
        // close auto paste and copy
        presenter.closeAndSaveAutoCopyPaste();
    }

    @Override
    public void destroy() {
        // 注册监听
        EventBus.getDefault().unregister(this);
        presenter.restoreAutoCopyPasteConfig();
    }


    private void initParams() {
        presenter = PresenterFactory.getPresenter(SceneType.COMPARE_SCENE);
        SystemResourceManager.initPresenter(presenter);
        presenter.setView(this);
        presenter.initView();
    }

    private void initClickEvents() {
        backBt.setOnAction((event -> presenter.switchScene(SceneType.MAIN_SCENE)));
        incrementalCopyMenu.setOnAction((event -> presenter.incrementalCopySwitch(incrementalCopyMenu.isSelected())));
        clearBt.setOnAction((event -> presenter.clearText()));
        keepStageTopMenu.setOnAction((event -> presenter.setKeepTop(keepStageTopMenu.isSelected())));
        clipboardListenerMenu.setOnAction((event -> presenter.clipboardSwitch(clipboardListenerMenu.isSelected())));
        dragCopyMenu.setOnAction((event -> presenter.dragCopySwitch(dragCopyMenu.isSelected())));
        textFormatMenu.setOnAction((event -> presenter.textFormatSwitch(textFormatMenu.isSelected())));
        // googleTextArea有两个作用，一个显示google翻译结果，另一个是获取当前用户手动输入
        translateBt.setOnAction((event -> presenter.translate(googleTextArea.getText())));
    }

    private void initListeners() {
        // 注册监听
        EventBus.getDefault().register(this);
    }


    @Override
    public void initViews(SystemConfiguration configuration) {
        // set window preSize
        rootPane.setPrefSize(550, 600);

        // 样式加载
        try {
            // 回显
            String path = configuration.getStyleCssPath();
            if (path != null) {
                File file = new File(path);
                if (file.exists()) {
                    rootPane.getStylesheets().setAll(file.toURI().toURL().toString());
                }
            }
        } catch (MalformedURLException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getLocalizedMessage(), e);
        }

        // 置顶
        keepStageTopMenu.setSelected(configuration.isKeepTop());

        // 增量
        incrementalCopyMenu.setSelected(configuration.isIncrementalCopy());
        // 监听版
        clipboardListenerMenu.setSelected(configuration.isOpenClipboardListener());
        // 拖拽
        dragCopyMenu.setSelected(configuration.isDragCopy());
        // 文本格式化
        textFormatMenu.setSelected(configuration.isTryFormat());

        // textArea
        if ("".equals(configuration.getBaiduTranslatorApiKey()) ||
                "".equals(configuration.getBaiduTranslatorSecretKey())) {
            baiduTextArea.setPromptText("您当前未配置百度Api，无法使用百度翻译");
        }
        if ("".equals(configuration.getYouDaoTranslatorApiKey()) ||
                "".equals(configuration.getYouDaoTranslatorSecretKey())) {
            youdaoTextArea.setPromptText("您当前未配置有道Api，无法使用有道翻译");
        }
        // 翻译后位置
        cursorPos = configuration.getTextAreaCursorPos();
    }

    @Override
    public void delayInitViews() {
        appStage = (Stage) rootPane.getScene().getWindow();

        // bind translate shortcut
        rootPane.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.T,
                    KeyCombination.CONTROL_DOWN);

            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    translateBt.fire();
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
    }

    @Override
    public void setKeepTop(boolean isKeep) {
        EventBus.getDefault().post(new SetKeepTopEvent(isKeep));
    }

    @Override
    public void switchScene(SceneType type) {
        EventBus.getDefault().post(new SwitchSceneEvent(type));
    }

    @Override
    public void translateStart() {
        Platform.runLater(() -> {
            keepGetTextFromClipboard = false;
            translateBt.setDisable(true);
        });
    }

    @Override
    public void translateEnd() {
        Platform.runLater(() -> {
            keepGetTextFromClipboard = true;
            translateBt.setDisable(false);
            if (cursorPos == TextAreaCursorPos.END) {
                googleTextArea.end();
                baiduTextArea.end();
                youdaoTextArea.end();
            }
        });
    }

    @Override
    public void clearAllText() {
        googleTextArea.setText("");
        baiduTextArea.setText("");
        youdaoTextArea.setText("");
    }

    @Override
    public void setTranslateResult(TranslatorType type, String translatedText) {
        switch (type) {
            case GOOGLE:
                googleTextArea.setText(translatedText);
                break;
            case BAIDU:
                baiduTextArea.setText(translatedText);
                break;
            case YOUDAO:
                youdaoTextArea.setText(translatedText);
                break;
            default:
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onClipboardContentInput(ClipboardContentInputEvent event) {
        if (event == null ||
                !keepGetTextFromClipboard) return;
        if (event.isTextType()) { // 文字类型
            presenter.translate(event.getText());
        } else {                // 图片类型
            try {
                String text = OCRUtils.ocr(event.getImage());
                if (text != null) {
                    presenter.translate(text);
                }
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "ocr识别错误", e);
            }
        }
    }
}
