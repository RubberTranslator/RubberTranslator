package com.rubbertranslator.mvp.view.controller.impl;

import com.rubbertranslator.entity.ApiInfo;
import com.rubbertranslator.entity.ControllerFxmlPath;
import com.rubbertranslator.enumtype.*;
import com.rubbertranslator.event.ClipboardContentInputEvent;
import com.rubbertranslator.event.SetKeepTopEvent;
import com.rubbertranslator.event.SwitchSceneEvent;
import com.rubbertranslator.mvp.presenter.PresenterFactory;
import com.rubbertranslator.mvp.presenter.impl.MainViewPresenter;
import com.rubbertranslator.mvp.view.controller.ISingleTranslateView;
import com.rubbertranslator.mvp.view.custom.ApiDialog;
import com.rubbertranslator.system.SystemConfiguration;
import com.rubbertranslator.system.SystemConfigurationManager;
import com.rubbertranslator.system.SystemResourceManager;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/6 20:49
 */
public class MainController implements ISingleTranslateView {

    @FXML
    private AnchorPane rootPane;

    /**
     * --------------------主功能区-----------------------------
     */
    @FXML
    private TextArea originTextArea;
    @FXML
    private TextArea translatedTextArea;
    @FXML
    private Button translateBt;

    /**
     * -------------------基本设置------------------------------
     */
    @FXML   // 翻译引擎类型
    private ToggleGroup translatorGroup;
    @FXML
    private RadioMenuItem googleTranslator;
    @FXML
    private RadioMenuItem baiduTranslator;
    @FXML
    private RadioMenuItem youdaoTranslator;

    @FXML // 源语言类型
    private ToggleGroup sourceLanguageGroup;
    @FXML
    private RadioMenuItem srcAuto;
    @FXML
    private RadioMenuItem srcSimpleChinese;
    @FXML
    private RadioMenuItem srcTraditionalChinese;
    @FXML
    private RadioMenuItem srcEnglish;
    @FXML
    private RadioMenuItem srcJapanese;
    @FXML
    private RadioMenuItem srcFrench;


    @FXML // 目标语言
    private ToggleGroup destLanguageGroup;
    @FXML
    private RadioMenuItem destSimpleChinese;
    @FXML
    private RadioMenuItem destTraditionalChinese;
    @FXML
    private RadioMenuItem destEnglish;
    @FXML
    private RadioMenuItem destJapanese;
    @FXML
    private RadioMenuItem destFrench;


    @FXML // 监听剪切板
    private RadioMenuItem clipboardListenerMenu;
    @FXML // 拖拽复制
    private RadioMenuItem dragCopyMenu;
    @FXML // 增量复制
    private RadioMenuItem incrementalCopyMenu;

    @FXML   // 自动复制
    private RadioMenuItem autoCopyMenu;
    @FXML   // 自动粘贴
    private RadioMenuItem autoPasteMenu;

    @FXML // 文本格式化
    private RadioMenuItem textFormatMenu;
    @FXML // 置顶
    private RadioMenuItem keepTopMenu;
    @FXML  // 翻译后文本光标位置
    private ToggleGroup textCursorPosGroup;
    @FXML
    private RadioMenuItem cursorStart;
    @FXML
    private RadioMenuItem cursorEnd;


    // 专注模式
    @FXML
    private Menu focusModeMenu;
    // 对比模式
    @FXML
    private Menu compareModeMenu;

    // 历史记录
    @FXML
    private Menu preHistoryMenu;
    @FXML
    private Menu nextHistoryMenu;

    // 清空
    @FXML
    private Menu clearMenu;

    /**
     * --------------------------高级设置-------------------------
     */
    @FXML   // ocr
    private MenuItem ocrMenu;
    @FXML // 过滤器
    private MenuItem filterMenu;
    @FXML // 词组替换
    private MenuItem translationWordsReplacerMenu;
    @FXML   // 翻译历史数量菜单
    private MenuItem historyNumMenu;
    @FXML
    private MenuItem customCssMenu;
    @FXML // 百度&有道api 设置
    private MenuItem baiduApiMenu;
    @FXML
    private MenuItem youdaoApiMenu;

    /**
     * -------------------------帮助-----------------------------
     */
    @FXML
    private MenuItem homePage;
    @FXML
    private MenuItem useAge;
    @FXML
    private MenuItem issues;
    @FXML
    private MenuItem versionText;

    /**
     * -------------------------其他辅助变量-----------------------------
     */
    // window stage 引用
    private Stage appStage;

    // 当前翻译后光标位置
    private TextAreaCursorPos cursorPos = TextAreaCursorPos.START;

    // 是否继续接受来自clipboard的文本
    private boolean keepGetTextFromClipboard = true;

    // presenter
    private MainViewPresenter presenter;

    /**
     * 组件初始化完成后，会调用这个方法
     */
    @FXML
    public void initialize() {
        start();
    }

    @Override
    public void start() {
        initListeners();
        initParams();
    }

    @Override
    public void destroy() {
        EventBus.getDefault().unregister(this);
    }

    private void initParams() {
        presenter = PresenterFactory.getPresenter(SceneType.MAIN_SCENE);
        SystemResourceManager.initPresenter(presenter);
        presenter.setView(this);
        presenter.initView();
    }

    private void initListeners() {
        // 注册翻译事件模型
        EventBus.getDefault().register(this);
    }

    @Override
    public void switchScene(SceneType type) {
        EventBus.getDefault().post(new SwitchSceneEvent(type));
    }

    @Override
    public void setKeepTop(boolean isKeep) {
        EventBus.getDefault().post(new SetKeepTopEvent(isKeep));
    }

    @Override
    public void translateStart() {
        Platform.runLater(() -> {
            keepGetTextFromClipboard = false;
            translateBt.setText("翻译中");
            translateBt.setDisable(true);
        });

    }

    @Override
    public void translateEnd() {
        Platform.runLater(() -> {
            keepGetTextFromClipboard = true;
            translateBt.setText("翻译(Ctrl+T)");
            translateBt.setDisable(false);
            if (cursorPos == TextAreaCursorPos.END) {
                originTextArea.end();
                translatedTextArea.end();
            }
        });
    }

    @Override
    public void setText(String originText, String translatedText) {
        Platform.runLater(() -> {
            originTextArea.setText(originText);
            translatedTextArea.setText(translatedText);
        });

    }

    @Override
    public void initViews(SystemConfiguration configuration) {
        // set window preSize
        rootPane.setPrefSize(configuration.getLastSize().getX(), configuration.getLastSize().getY());

        // 基础设置
        new BasicSettingMenu().init(configuration);
        // 高级设置
        new AdvancedSettingMenu().init(configuration);
        // 帮助
        initHelpingMenu();
        // 专注模式
        initFocusModeMenu();
        // 对比模式
        initCompareModeMenu();
        // 历史
        initHistoryMenu();
        // 清空
        initClearMenu();
        // window preSize
    }

    @Override
    public void delayInitViews() {
        // inject appStage
        appStage = (Stage) rootPane.getScene().getWindow();

        // bind key short for "translate button"
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
    public void autoCopy(boolean isOpen) {
        if (!isOpen) {
            autoPasteMenu.setSelected(false);
        }
    }

    @Override
    public void autoPaste(boolean isOpen) {
        // 自动粘贴依赖于自动复制
        if (isOpen) {
            autoCopyMenu.setSelected(true);
        }
    }

    @Override
    public void setTextAreaCursorPos(TextAreaCursorPos pos) {
        cursorPos = pos;
    }

    /**
     * 基础设置
     */
    private class BasicSettingMenu {

        public void init(SystemConfiguration configuration) {
            initBasicSettingMenu(configuration);
        }

        /**
         * 基础设置
         *
         * @param configuration 系统配置
         */
        private void initBasicSettingMenu(SystemConfiguration configuration) {
            initTranslatorType(configuration.getCurrentTranslator());
            initSrcDestLanguage(configuration.getSourceLanguage(), configuration.getDestLanguage());
            initBasicSettingOthers(configuration);
        }

        private void initBasicSettingOthers(final SystemConfiguration configuration) {
            // 设置onActionListener
            clipboardListenerMenu.setOnAction((actionEvent) -> presenter.clipboardSwitch(clipboardListenerMenu.isSelected()));
            dragCopyMenu.setOnAction((actionEvent) -> presenter.dragCopySwitch(dragCopyMenu.isSelected()));
            incrementalCopyMenu.setOnAction((actionEvent -> presenter.incrementalCopySwitch(incrementalCopyMenu.isSelected())));
            autoCopyMenu.setOnAction((actionEvent -> presenter.autoCopySwitch(autoCopyMenu.isSelected())));
            autoPasteMenu.setOnAction((actionEvent -> presenter.autoPasteSwitch(autoPasteMenu.isSelected())));
            textFormatMenu.setOnAction((actionEvent -> presenter.textFormatSwitch(textFormatMenu.isSelected())));
            keepTopMenu.setOnAction((actionEvent -> presenter.setKeepTop(keepTopMenu.isSelected())));
            textCursorPosGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == cursorStart) {
                    presenter.setTextCursorPos(TextAreaCursorPos.START);
                } else if (newValue == cursorEnd) {
                    presenter.setTextCursorPos(TextAreaCursorPos.END);
                }
            });

            // ui回显
            // 监听剪切板
            clipboardListenerMenu.setSelected(configuration.isOpenClipboardListener());
            // 拖拽复制
            dragCopyMenu.setSelected(configuration.isDragCopy());
            // 增量复制
            incrementalCopyMenu.setSelected(configuration.isIncrementalCopy());
            // 自动复制
            autoCopyMenu.setSelected(configuration.isAutoCopy());
            // 自动粘贴
            autoPasteMenu.setSelected(configuration.isAutoPaste());
            // 保持段落格式
            textFormatMenu.setSelected(configuration.isTryFormat());
            // 置顶
            keepTopMenu.setSelected(configuration.isKeepTop());
            // 翻译后文本光标位置
            if (configuration.getTextAreaCursorPos() == TextAreaCursorPos.START) {
                cursorPos = TextAreaCursorPos.START;
                cursorStart.setSelected(true);
            } else {
                cursorPos = TextAreaCursorPos.END;
                cursorEnd.setSelected(true);
            }
        }

        private void initTranslatorType(TranslatorType type) {
            // view
            switch (type) {
                case GOOGLE:
                    googleTranslator.setSelected(true);
                    break;
                case BAIDU:
                    baiduTranslator.setSelected(true);
                    break;
                case YOUDAO:
                    youdaoTranslator.setSelected(true);
                    break;
            }
            // 监听
            translatorGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == googleTranslator) {
                    presenter.setTranslatorType(TranslatorType.GOOGLE);
                } else if (newValue == baiduTranslator) {
                    presenter.setTranslatorType(TranslatorType.BAIDU);
                } else if (newValue == youdaoTranslator) {
                    presenter.setTranslatorType(TranslatorType.YOUDAO);
                } else {
                    oldValue.setSelected(true);
                }
            });
        }

        private void initSrcDestLanguage(Language src, Language dest) {
            // xxx: 源、目标语言的设置方式有些违反单一职责，但是降低了重复代码
            // src
            initLanguage(src, srcAuto, srcSimpleChinese, srcTraditionalChinese, srcEnglish, srcFrench, srcJapanese);
            srcDestLanguageChooseEvent(true, sourceLanguageGroup, srcSimpleChinese, srcTraditionalChinese, srcEnglish, srcFrench, srcJapanese);
            // dest
            initLanguage(dest, null, destSimpleChinese, destTraditionalChinese, destEnglish, destFrench, destJapanese);
            srcDestLanguageChooseEvent(false, destLanguageGroup, destSimpleChinese, destTraditionalChinese, destEnglish, destFrench, destJapanese);

        }

        private void initLanguage(Language type, RadioMenuItem auto, RadioMenuItem simpleChinese, RadioMenuItem traditional,
                                  RadioMenuItem english, RadioMenuItem french, RadioMenuItem japanese) {
            switch (type) {
                case AUTO:
                    auto.setSelected(true);
                    break;
                case CHINESE_SIMPLIFIED:
                    simpleChinese.setSelected(true);
                    break;
                case CHINESE_TRADITIONAL:
                    traditional.setSelected(true);
                    break;
                case ENGLISH:
                    english.setSelected(true);
                    break;
                case FRENCH:
                    french.setSelected(true);
                    break;
                case JAPANESE:
                    japanese.setSelected(true);
                    break;
            }
        }


        private void srcDestLanguageChooseEvent(boolean isSrc, ToggleGroup languageGroup, RadioMenuItem simpleChinese, RadioMenuItem traditional,
                                                RadioMenuItem english, RadioMenuItem french, RadioMenuItem japanese) {
            languageGroup.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> {
                Language language = Language.AUTO;
                if (newValue == srcAuto) {    // 此判断多余，但是为了完整性，还是加上
                    language = Language.AUTO;
                } else if (newValue == simpleChinese) {
                    language = Language.CHINESE_SIMPLIFIED;
                } else if (newValue == traditional) {
                    language = Language.CHINESE_TRADITIONAL;
                } else if (newValue == english) {
                    language = Language.ENGLISH;
                } else if (newValue == french) {
                    language = Language.FRENCH;
                } else if (newValue == japanese) {
                    language = Language.JAPANESE;
                }

                presenter.setTranslatorLanguage(isSrc, language);
            });
        }
    }


    private class AdvancedSettingMenu {
        public void init(SystemConfiguration configuration) {
            initOCR(configuration);
            initProcessFilter();
            initWordsReplacer();
            initTranslationHistoryNumMenu(configuration);
            initCustomCss(configuration);
            initApiMenu(configuration);
        }

        private void initCustomCss(SystemConfiguration configuration) {
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

            // 点击事件
            customCssMenu.setOnAction((actionEvent -> {
                try {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.getExtensionFilters().add(
                            new FileChooser.ExtensionFilter("css文件", "*.css"));
                    File newFile = fileChooser.showOpenDialog(appStage);
                    if (newFile == null) return;
                    // 应用
                    rootPane.getStylesheets().setAll(newFile.toURI().toURL().toString());
                    // 应用持久化
                    configuration.setStyleCssPath(newFile.getAbsolutePath());
                } catch (MalformedURLException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getLocalizedMessage(), e);
                }
            }));
        }

        private void initOCR(SystemConfiguration configuration) {
            ocrMenu.setOnAction((actionEvent ->
                    new ApiDialog("百度OCR",  // 标题
                            "https://ai.baidu.com/tech/ocr",
                            new ApiInfo(configuration.getBaiduOcrApiKey(), configuration.getBaiduOcrSecretKey()) /*  回显所需信息  */,
                            appStage,
                            apiInfo -> {
                                if (apiInfo == null) return;
                                presenter.setOcrApi(apiInfo);
                            })
                            .showDialog()));
        }

        /**
         * ocr 百度和有道的api secret key menu
         */
        private void initApiMenu(SystemConfiguration configuration) {
            baiduApiMenu.setOnAction((actionEvent -> new ApiDialog("百度翻译",  // 标题
                    "http://api.fanyi.baidu.com/",
                    new ApiInfo(configuration.getBaiduTranslatorApiKey(), configuration.getBaiduTranslatorSecretKey()),   // 回显所需信息
                    appStage,
                    apiInfo -> {   // 用户确定后的回调
                        if (apiInfo == null) return;
                        presenter.setBaiduTranslatorApi(apiInfo);
                    })
                    .showDialog()));
            youdaoApiMenu.setOnAction((actionEvent -> new ApiDialog("有道翻译", // 标题
                    "https://ai.youdao.com/?keyfrom=old-openapi",
                    new ApiInfo(configuration.getYouDaoTranslatorApiKey(), configuration.getYouDaoTranslatorSecretKey()),   // 回显所需信息
                    appStage,
                    apiInfo -> {   // 用户确定后的回调
                        if (apiInfo == null) return;
                        presenter.setYoudaoTranslatorApi(apiInfo);
                    })
                    .showDialog()));
        }

        private void initProcessFilter() {
            filterMenu.setOnAction((actionEvent -> {
                try {
                    Stage stage = new Stage();
                    FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource(ControllerFxmlPath.FILTER_CONTROLLER_FXML));
                    Scene scene = new Scene(fxmlLoader.load());
                    stage.initOwner(appStage);
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "过滤器页面错误", e);
                }
            }));
        }

        private void initWordsReplacer() {
            translationWordsReplacerMenu.setOnAction((actionEvent -> {
                try {
                    Stage stage = new Stage();
                    FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource(ControllerFxmlPath.WORDS_REPLACER_CONTROLLER_FXML));
                    Scene scene = new Scene(fxmlLoader.load());
                    stage.initOwner(appStage);
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "词组替换页面打开失败", e);
                }
            }));
        }


        private void initTranslationHistoryNumMenu(SystemConfiguration configuration) {
            historyNumMenu.setOnAction((actionEvent -> {
                TextInputDialog dialog = new TextInputDialog("" + configuration.getHistoryNum());
                dialog.setTitle("设置");
                dialog.setHeaderText("翻译历史数量设置");
                dialog.setContentText("输入保存历史数量(不超过100):");
                dialog.initOwner(appStage);
                // Traditional way to get the response value.
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(s -> {
                    try {
                        int value = Integer.parseInt(s);
                        if (value < 0) {
                            throw new NumberFormatException();
                        }
                        // 更新设置
                        configuration.setHistoryNum(value);
                    } catch (NumberFormatException e) {
                        originTextArea.setText("翻译历史数仅限数字且大于0");
                    }
                });
            }));
        }

    }

    /**
     * 帮助设置
     */
    private void initHelpingMenu() {
        //
        homePage.setOnAction((actionEvent) ->
        {
            // 这里用new Thread有点蠢，不过为了避免为卡死，暂时这样吧
            if (Desktop.isDesktopSupported()) {
                SystemResourceManager.getExecutor().execute(() -> {
                    try {
                        Desktop.getDesktop().browse(new URI("https://github.com/ravenxrz/RubberTranslator"));
                    } catch (IOException | URISyntaxException e) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "项目主页打开失败", e);
                    }
                });
            } else {
                originTextArea.setText("当前平台不支持打开该页面");
            }

        });
        // wiki
        useAge.setOnAction((actionEvent) -> {
            if (Desktop.isDesktopSupported()) {
                SystemResourceManager.getExecutor().execute(() -> {
                    try {
                        Desktop.getDesktop().browse(new URI("https://www.ravenxrz.ink/archives/a79932ef.html"));
                    } catch (IOException | URISyntaxException e) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "使用帮助打开失败", e);
                    }
                });
            } else {
                originTextArea.setText("当前平台不支持打开该页面");
            }
        });
        // issue
        issues.setOnAction((actionEvent -> {
            if (Desktop.isDesktopSupported()) {
                SystemResourceManager.getExecutor().execute(() -> {
                            try {
                                Desktop.getDesktop().browse(new URI("https://github.com/ravenxrz/RubberTranslator/issues"));
                            } catch (IOException | URISyntaxException e) {
                                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "issues打开失败", e);
                            }
                        }
                );
            } else {
                originTextArea.setText("当前平台不支持打开该页面");
            }
        }));
        // versionText
        versionText.setText(SystemConfigurationManager.getCurrentVersion());
    }

    private void initFocusModeMenu() {
        Label label = new Label("专注模式");
        label.setOnMouseClicked((event -> presenter.switchScene(SceneType.FOCUS_SCENE)));
        focusModeMenu.setText("");
        focusModeMenu.setGraphic(label);
    }

    private void initCompareModeMenu() {
        Label label = new Label("对比模式");
        label.setOnMouseClicked((event -> presenter.switchScene(SceneType.COMPARE_SCENE)));
        compareModeMenu.setText("");
        compareModeMenu.setGraphic(label);
    }

    private void initHistoryMenu() {
        Label pre = new Label("上一条");
        Label next = new Label("下一条");
        pre.setOnMouseClicked((event -> presenter.setHistoryEntry(HistoryEntryIndex.PRE_HISTORY)));
        next.setOnMouseClicked(event -> presenter.setHistoryEntry(HistoryEntryIndex.NEXT_HISTORY));
        preHistoryMenu.setText("");
        nextHistoryMenu.setText("");
        preHistoryMenu.setGraphic(pre);
        nextHistoryMenu.setGraphic(next);
    }

    private void initClearMenu() {
        Label label = new Label("清空");
        label.setOnMouseClicked((event -> presenter.clearText()));
        clearMenu.setText("");
        clearMenu.setGraphic(label);
    }


    @FXML
    public void onBtnTranslateClick() {
        presenter.translate(originTextArea.getText());
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onClipboardContentInput(ClipboardContentInputEvent event) {
        if (event == null ||
                !keepGetTextFromClipboard) return;
        if (event.isTextType()) {
            presenter.translate(event.getText());
        } else {
            presenter.translate(event.getImage());
        }
    }

}
