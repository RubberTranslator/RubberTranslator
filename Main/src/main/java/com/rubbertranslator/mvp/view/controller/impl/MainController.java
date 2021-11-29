package com.rubbertranslator.mvp.view.controller.impl;

import com.rubbertranslator.entity.ApiInfo;
import com.rubbertranslator.entity.AppearanceSetting;
import com.rubbertranslator.enumtype.*;
import com.rubbertranslator.event.*;
import com.rubbertranslator.mvp.presenter.PresenterFactory;
import com.rubbertranslator.mvp.presenter.impl.MainViewPresenter;
import com.rubbertranslator.mvp.view.controller.ISingleTranslateView;
import com.rubbertranslator.mvp.view.custom.ApiDialog;
import com.rubbertranslator.mvp.view.custom.AppearanceSettingDialog;
import com.rubbertranslator.mvp.view.custom.OpacitySettingDialog;
import com.rubbertranslator.mvp.view.custom.SponsorDialog;
import com.rubbertranslator.system.*;
import com.rubbertranslator.utils.ExploreUtil;
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
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
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
    @FXML
    private RadioMenuItem noneTranslator;

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
    @FXML
    private RadioMenuItem srcPolish;
    @FXML
    private RadioMenuItem srcDutch;
    @FXML
    private RadioMenuItem srcBulgaria;
    @FXML
    private RadioMenuItem srcGreek;
    @FXML
    private RadioMenuItem srcPortuguese;
    @FXML
    private RadioMenuItem srcRussian;
    @FXML
    private RadioMenuItem srcArabic;
    @FXML
    private RadioMenuItem srcThai;
    @FXML
    private RadioMenuItem srcSpanish;
    @FXML
    private RadioMenuItem srcKorean;

    private final Map<Language, RadioMenuItem> srcLang2BtMap = new HashMap<>();


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
    @FXML
    private RadioMenuItem destPolish;
    @FXML
    private RadioMenuItem destDutch;
    @FXML
    private RadioMenuItem destBulgaria;
    @FXML
    private RadioMenuItem destGreek;
    @FXML
    private RadioMenuItem destPortuguese;
    @FXML
    private RadioMenuItem destRussian;
    @FXML
    private RadioMenuItem destArabic;
    @FXML
    private RadioMenuItem destThai;
    @FXML
    private RadioMenuItem destSpanish;
    @FXML
    private RadioMenuItem destKorean;

    private final Map<Language, RadioMenuItem> destLang2BtMap = new HashMap<>();


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
    @FXML  // 失去焦点
    private RadioMenuItem lossFocusTransparent;
    @FXML
    private RadioMenuItem minimizedWindowCancelListen;


    // 专注模式
    @FXML
    private MenuItem focusModeMenu;
    // 对比模式
    @FXML
    private MenuItem compareModeMenu;
    // 记录模式
    @FXML
    private MenuItem recordModeMenu;

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
    @FXML
    private MenuItem appSettings;
    @FXML // 百度&有道api 设置
    private MenuItem baiduApiMenu;
    @FXML
    private MenuItem youdaoApiMenu;
    @FXML
    private MenuItem opacitySettingMenu;


    /**
     * -------------------------帮助-----------------------------
     */
    @FXML
    private MenuItem homePage;
    @FXML
    private MenuItem useAge;
    //    @FXML
//    private MenuItem downloadUrl;
    @FXML
    private MenuItem issues;
    @FXML
    private MenuItem openConfigDirMenu;
    @FXML
    private MenuItem openLogDirMenu;
    @FXML
    private MenuItem openRecordDirMenu;
    @FXML
    private MenuItem sponsorMenu;
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
            EventBus.getDefault().post(new SetWindowUnTransparentEvent());
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
        // 历史
        initHistoryMenu();
        // 清空
        initClearMenu();
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
            lossFocusTransparent.setOnAction((actionEvent) ->
                    configuration.setLossFocusTransparent(lossFocusTransparent.isSelected()));
            minimizedWindowCancelListen.setOnAction((actionEvent) ->
            {
                configuration.setMinimizedCancelListen(minimizedWindowCancelListen.isSelected());
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
            // 失去focus，透明化
            lossFocusTransparent.setSelected(configuration.isLossFocusTransparent());
            // 窗口最小化，取消监听
            minimizedWindowCancelListen.setSelected(configuration.isMinimizedCancelListen());
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
                case NONE:
                    noneTranslator.setSelected(true);
            }
            // 监听
            translatorGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == googleTranslator) {
                    presenter.setTranslatorType(TranslatorType.GOOGLE);
                } else if (newValue == baiduTranslator) {
                    presenter.setTranslatorType(TranslatorType.BAIDU);
                } else if (newValue == youdaoTranslator) {
                    presenter.setTranslatorType(TranslatorType.YOUDAO);
                } else if (newValue == noneTranslator) {
                    presenter.setTranslatorType(TranslatorType.NONE);
                } else {
                    oldValue.setSelected(true);
                }
            });
        }

        private void initSrcDestLanguage(Language src, Language dest) {
            // xxx: 源、目标语言的设置方式有些违反单一职责，但是降低了重复代码
            srcLang2BtMap.put(Language.AUTO, srcAuto);
            srcLang2BtMap.put(Language.CHINESE_SIMPLIFIED, srcSimpleChinese);
            srcLang2BtMap.put(Language.CHINESE_TRADITIONAL, srcTraditionalChinese);
            srcLang2BtMap.put(Language.ENGLISH, srcEnglish);
            srcLang2BtMap.put(Language.FRENCH, srcFrench);
            srcLang2BtMap.put(Language.JAPANESE, srcJapanese);
            srcLang2BtMap.put(Language.KOREAN, srcKorean);
            srcLang2BtMap.put(Language.SPANISH, srcSpanish);
            srcLang2BtMap.put(Language.THAI, srcThai);
            srcLang2BtMap.put(Language.ARABIC, srcArabic);
            srcLang2BtMap.put(Language.RUSSIAN, srcRussian);
            srcLang2BtMap.put(Language.PORTUGUESE, srcPortuguese);
            srcLang2BtMap.put(Language.GREEK, srcGreek);
            srcLang2BtMap.put(Language.BULGARIA, srcBulgaria);
            srcLang2BtMap.put(Language.DUTCH, srcDutch);
            srcLang2BtMap.put(Language.POLISH, srcPolish);

            destLang2BtMap.put(Language.CHINESE_SIMPLIFIED, destSimpleChinese);
            destLang2BtMap.put(Language.CHINESE_TRADITIONAL, destTraditionalChinese);
            destLang2BtMap.put(Language.ENGLISH, destEnglish);
            destLang2BtMap.put(Language.FRENCH, destFrench);
            destLang2BtMap.put(Language.JAPANESE, destJapanese);
            destLang2BtMap.put(Language.KOREAN, destKorean);
            destLang2BtMap.put(Language.SPANISH, destSpanish);
            destLang2BtMap.put(Language.THAI, destThai);
            destLang2BtMap.put(Language.ARABIC, destArabic);
            destLang2BtMap.put(Language.RUSSIAN, destRussian);
            destLang2BtMap.put(Language.PORTUGUESE, destPortuguese);
            destLang2BtMap.put(Language.GREEK, destGreek);
            destLang2BtMap.put(Language.BULGARIA, destBulgaria);
            destLang2BtMap.put(Language.DUTCH, destDutch);
            destLang2BtMap.put(Language.POLISH, destPolish);

            // src
            srcLang2BtMap.getOrDefault(src, srcAuto).setSelected(true);
            srcDestLanguageChooseEvent(true, sourceLanguageGroup, srcLang2BtMap);
            // dest
            destLang2BtMap.getOrDefault(dest, destSimpleChinese).setSelected(true);
            srcDestLanguageChooseEvent(false, destLanguageGroup, destLang2BtMap);

        }


        private void srcDestLanguageChooseEvent(boolean isSrc, ToggleGroup languageGroup, Map<Language, RadioMenuItem> lang2BtMap) {
            languageGroup.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> {
                Language language = Language.AUTO;
                for (Map.Entry<Language, RadioMenuItem> entry : lang2BtMap.entrySet()) {
                    if (entry.getValue() == newValue) {
                        language = entry.getKey();
                        break;
                    }
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
            initAppearanceSetting(configuration);
            initApiMenu(configuration);
            initOpacitySettings(configuration);
        }


        private void initAppearanceSetting(SystemConfiguration configuration) {
            // 点击事件
            appSettings.setOnAction((actionEvent -> {
                AppearanceSetting setting = new AppearanceSetting();
                setting.appFontSize = configuration.getAppFontSize();
                setting.textFontSize = configuration.getTextFontSize();
                new AppearanceSettingDialog(appStage, setting,
                        newSettings -> {
                            if (newSettings == null) return;
                            configuration.setAppFontSize(newSettings.appFontSize);
                            configuration.setTextFontSize(newSettings.textFontSize);
                            // Notify appStage to update app settings
                            EventBus.getDefault().post(new AppearanceSettingUpdateEvent());
                        }).showDialog();
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

        private void initOpacitySettings(SystemConfiguration configuration) {
            opacitySettingMenu.setOnAction((actionEvent) -> new OpacitySettingDialog(appStage, configuration.getOpacityValue(), configuration::setOpacityValue).showDialog());
        }
    }

    /**
     * 帮助设置
     */

    private void initHelpingMenu() {
        //
        homePage.setOnAction((actionEvent) ->
        {
            openUrl("https://github.com/RubberTranslator/RubberTranslator");
        });
        // wiki
        useAge.setOnAction((actionEvent) -> {
            openUrl("https://rubbertranslator.github.io/docs/index.html");
        });
        // download url
//        downloadUrl.setOnAction((actionEvent -> {
//            openUrl("https://ravenxrz.lanzous.com/b01bezbcf");
//        }));
        // issue
        issues.setOnAction((actionEvent -> {
            openUrl("https://jq.qq.com/?_wv=1027&k=8HGTc7h5");
        }));
        // open dir window
        openConfigDirMenu.setOnAction((event) -> {
            ExploreUtil.openExplore(ProgramPaths.configFileDir, s -> originTextArea.setText(s));
        });
        openLogDirMenu.setOnAction((event) -> {
            ExploreUtil.openExplore(ProgramPaths.logFileDir, s -> originTextArea.setText(s));
        });
        openRecordDirMenu.setOnAction((event) -> {
            ExploreUtil.openExplore(ProgramPaths.exportDir, s -> originTextArea.setText(s));
        });

        sponsorMenu.setOnAction((event) -> {
            openSponsorDialog();
        });

        // versionText
        versionText.setText(SystemConfigurationManager.getCurrentVersion());

        // focus
        focusModeMenu.setOnAction((event) -> presenter.switchScene(SceneType.FOCUS_SCENE));

        // compare
        compareModeMenu.setOnAction((event -> presenter.switchScene(SceneType.COMPARE_SCENE)));

        // record
        recordModeMenu.setOnAction(event -> presenter.switchScene(SceneType.RECORD_SCENE));
    }

    private void openSponsorDialog() {
        SponsorDialog.show();
    }

    private void openUrl(String url) {
        if (Desktop.isDesktopSupported()) {
            SystemResourceManager.getExecutor().execute(() -> {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (IOException | URISyntaxException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, url + " open failed", e);
                }
            });
        } else {
            originTextArea.setText("当前平台不支持打开该页面");
        }
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
        if (event.isTextType) {
            presenter.translate(event.text);
        } else {
            presenter.translate(event.image);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onHotKeyInput(HotKeyEvent event) {
        if (event == null) return;
        Platform.runLater(() -> {
            switch (event.hotKeyValue) {
                case HotKey.F5:
                    clipboardListenerMenu.setSelected(!clipboardListenerMenu.isSelected());
                    clipboardListenerMenu.fire();
                    break;
                case HotKey.F6:
                    dragCopyMenu.setSelected(!dragCopyMenu.isSelected());
                    dragCopyMenu.fire();
                default:
            }
        });

    }

}
