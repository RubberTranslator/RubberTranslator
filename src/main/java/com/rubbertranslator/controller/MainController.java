package com.rubbertranslator.controller;

import com.rubbertranslator.App;
import com.rubbertranslator.event.ClipboardContentInputEvent;
import com.rubbertranslator.event.TranslateCompleteEvent;
import com.rubbertranslator.event.TranslatorFacadeEvent;
import com.rubbertranslator.modules.history.HistoryEntry;
import com.rubbertranslator.modules.textinput.ocr.OCRUtils;
import com.rubbertranslator.modules.translate.Language;
import com.rubbertranslator.modules.translate.TranslatorType;
import com.rubbertranslator.system.SystemConfiguration;
import com.rubbertranslator.system.SystemResourceManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
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
public class MainController {

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

    // 专注模式
    @FXML
    private Menu focusMenu;

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
    private MenuItem wiki;
    @FXML
    private MenuItem issues;


    /**
     * 组件初始化完成后，会调用这个方法
     */
    @FXML
    public void initialize() {
        initListeners();
        initViews();
    }

    private void initListeners() {
        // 注册翻译事件模型
        EventBus.getDefault().register(this);
    }

    /**
     * translatorEvent事件接收
     * @param event translatorEvent事件接收
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void translatorFacadeEvent(TranslatorFacadeEvent event) {
        if(event == null) return;
        Platform.runLater(()->{
            if(event.isProcessStart()){ // 处理开始
                translateBt.setText("翻译中");
                translateBt.setDisable(true);
            }else{      // 处理结束
                translateBt.setText("翻译");
                translateBt.setDisable(false);
            }
        });
    }

    private void initViews() {
        // 基础设置
        new BasicSettingMenu().init();
        // 高级设置
        new AdvancedSettingMenu().init();
        // 帮助
        initHelpingMenu();
        // 专注模式
        initFocusModeMenu();
        // 历史
        initHistoryMenu();
        // 清空
        initClearMenu();
    }


    /**
     * 基础设置
     */
    private class BasicSettingMenu {

        public void init() {
            initBasicSettingMenu(SystemResourceManager.getConfigurationProxy());
            Logger.getLogger(BasicSettingMenu.class.getName()).info("初始化基础设置成功");
        }

        /**
         * 基础设置
         *
         * @param configuration 系统配置
         */
        private void initBasicSettingMenu(SystemConfiguration configuration) {
            initTranslatorType(configuration.getTranslatorConfig().getCurrentTranslator());
            initSrcDestLanguage(configuration.getTranslatorConfig().getSourceLanguage(), configuration.getTranslatorConfig().getDestLanguage());
            initBasicSettingOthers(configuration);
        }

        private void initBasicSettingOthers(final SystemConfiguration configuration) {
            // 设置onActionListener
            clipboardListenerMenu.setOnAction((actionEvent) ->
                    configuration.getTextInputConfig().setOpenClipboardListener(clipboardListenerMenu.isSelected())
            );

            dragCopyMenu.setOnAction((actionEvent) ->
                    configuration.getTextInputConfig().setDragCopy(dragCopyMenu.isSelected()));
            incrementalCopyMenu.setOnAction((actionEvent ->
                    configuration.getTextProcessConfig().getTextPreProcessConfig().setIncrementalCopy(incrementalCopyMenu.isSelected())));
            autoCopyMenu.setOnAction((actionEvent -> {
                if (!autoCopyMenu.isSelected()) {
                    autoPasteMenu.setSelected(false);
                    configuration.getAfterProcessorConfig().setAutoPaste(false);
                }
                configuration.getAfterProcessorConfig().setAutoCopy(autoCopyMenu.isSelected());
            }));
            autoPasteMenu.setOnAction((actionEvent -> {
                // 自动粘贴依赖于自动复制
                if (autoPasteMenu.isSelected()) {
                    autoCopyMenu.setSelected(true);
                    configuration.getAfterProcessorConfig().setAutoCopy(true);
                }
                configuration.getAfterProcessorConfig().setAutoPaste(autoPasteMenu.isSelected());
            }));
            textFormatMenu.setOnAction((actionEvent ->
                    configuration.getTextProcessConfig().getTextPreProcessConfig().setTryToFormat(textFormatMenu.isSelected())));
            keepTopMenu.setOnAction((actionEvent -> {
                // XXX: UI模块的相关功能，没有静态代理管理，需要手动界面生效功能，然后动态代理自动持久化设置
                App.setKeepTop(keepTopMenu.isSelected());
                configuration.getUiConfig().setKeepTop(keepTopMenu.isSelected());
            }));

            // ui回显
            // 监听剪切板
            clipboardListenerMenu.setSelected(configuration.getTextInputConfig().isOpenClipboardListener());
            // 拖拽复制
            dragCopyMenu.setSelected(configuration.getTextInputConfig().isDragCopy());
            // 增量复制
            incrementalCopyMenu.setSelected(configuration.getTextProcessConfig().getTextPreProcessConfig().isIncrementalCopy());
            // 自动复制
            autoCopyMenu.setSelected(configuration.getAfterProcessorConfig().isAutoCopy());
            // 自动粘贴
            autoPasteMenu.setSelected(configuration.getAfterProcessorConfig().isAutoPaste());
            // 保持段落格式
            textFormatMenu.setSelected(configuration.getTextProcessConfig().getTextPreProcessConfig().isTryToFormat());
            // 置顶
            keepTopMenu.setSelected(configuration.getUiConfig().isKeepTop());

            // 置顶在UI模块，UI模块不在系统资源管理范围内，xxx:考虑重构
            App.setKeepTop(keepTopMenu.isSelected());
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
                SystemConfiguration.TranslatorConfig translatorConfig = SystemResourceManager.getConfigurationProxy().getTranslatorConfig();
                if (newValue == googleTranslator) {
                    translatorConfig.setCurrentTranslator(TranslatorType.GOOGLE);
                } else if (newValue == baiduTranslator) {
                    translatorConfig.setCurrentTranslator(TranslatorType.BAIDU);
                } else if (newValue == youdaoTranslator) {
                    translatorConfig.setCurrentTranslator(TranslatorType.YOUDAO);
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
                SystemConfiguration.TranslatorConfig translatorConfig = SystemResourceManager.getConfigurationProxy().getTranslatorConfig();
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

                if (isSrc) {
                    translatorConfig.setSourceLanguage(language);
                } else {
                    translatorConfig.setDestLanguage(language);
                }
            });
        }
    }

    /**
     * 高级设置
     */

    private interface ApiInfoChangedListener {
        void onApiChanged(ApiInfo newValue);
    }

    private static class ApiInfo {
        public String apiKey;
        public String secretKey;

        public ApiInfo(String apiKey, String secretKey) {
            this.apiKey = apiKey;
            this.secretKey = secretKey;
        }

        public String getApiKey() {
            return apiKey;
        }

        public String getSecretKey() {
            return secretKey;
        }
    }


    private class ApiDialog {

        private final String dialogTitle;
        private final ApiInfo apiInfo;
        private final String titleClickUrl;
        private final ApiInfoChangedListener listener;

        public ApiDialog(String dialogTitle, String titleClickUrl, ApiInfo apiInfo, ApiInfoChangedListener listener) {
            this.dialogTitle = dialogTitle;
            this.titleClickUrl = titleClickUrl;
            this.apiInfo = apiInfo;
            this.listener = listener;
        }

        public TextField apiKeyTf;
        public TextField secretKeyTf;

        private Node create() {
            // 主内容
            VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            vBox.setSpacing(10);
            // 26、13、171
            Label title = new Label(dialogTitle);
            title.setStyle("-fx-text-fill: #2196f3;" +
                    "-fx-font-weight: bold");
            title.setOnMouseClicked((event -> {
                try {
                    Desktop.getDesktop().browse(new URI(titleClickUrl));
                } catch (IOException | URISyntaxException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "错误url:" + titleClickUrl, e);
                }
            }));
            Label apiKeyLabel = new Label("API_KEY");
            apiKeyTf = new TextField();
            apiKeyTf.setText(apiInfo.getApiKey());
            Label secretLabel = new Label("SECRET_KEY");
            secretKeyTf = new TextField();
            secretKeyTf.setText(apiInfo.getSecretKey());
            vBox.getChildren().addAll(title, apiKeyLabel, apiKeyTf, secretLabel, secretKeyTf);
            return vBox;
        }

        public void showDialog() {
            Dialog<ApiInfo> dialog = new Dialog<>();
            // 确定和取消
            ButtonType confirmBt = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelBt = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(confirmBt, cancelBt);
            dialog.getDialogPane().setContent(create());
            dialog.initOwner(rootPane.getScene().getWindow());

            // 结果转换器
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == confirmBt) {
                    return new ApiInfo(apiKeyTf.getText(), secretKeyTf.getText());
                }
                return null;
            });
            // 处理结果
            Optional<ApiInfo> result = dialog.showAndWait();
            result.ifPresent(ocrInfo -> {
                // 获取更新
                String apiKey = ocrInfo.apiKey;
                String secretKey = ocrInfo.secretKey;
                if ("".equals(apiKey) || "".equals(secretKey)) {
                    originTextArea.setText("Api信息不完整,请填写所有字段");
                } else {
                    listener.onApiChanged(new ApiInfo(apiKey, secretKey));
                }
            });
        }
    }

    private class AdvancedSettingMenu {
        public void init() {
            initAdvancedSettingMenu(SystemResourceManager.getConfigurationProxy());
            Logger.getLogger(BasicSettingMenu.class.getName()).info("初始化高级设置成功");
        }

        private void initAdvancedSettingMenu(SystemConfiguration configuration) {
            initOCR(configuration.getTextInputConfig());
            initProcessFilter();
            initWordsReplacer();
            initTranslationHistoryNumMenu(configuration.getHistoryConfig());
            initCustomCss(configuration.getUiConfig());
            initApiMenu(configuration.getTranslatorConfig());
        }

        private void initCustomCss(SystemConfiguration.UIConfig configuration) {
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
                    File newFile = fileChooser.showOpenDialog(rootPane.getScene().getWindow());
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

        private void initOCR(SystemConfiguration.TextInputConfig configuration) {
            ocrMenu.setOnAction((actionEvent -> new ApiDialog("百度OCR",  // 标题
                    "https://ai.baidu.com/tech/ocr",
                    new ApiInfo(configuration.getBaiduOcrApiKey(), configuration.getBaiduOcrSecretKey()),   // 回显所需信息
                    (newValue) -> {   // 用户确定后的回调
                        configuration.setBaiduOcrApiKey(newValue.getApiKey().trim());
                        configuration.setBaiduOcrSecretKey(newValue.getSecretKey().trim());
                    })
                    .showDialog()));
        }

        /**
         * ocr 百度和有道的api secret key menu
         */
        private void initApiMenu(SystemConfiguration.TranslatorConfig configuration) {
            baiduApiMenu.setOnAction((actionEvent -> new ApiDialog("百度翻译",  // 标题
                    "http://api.fanyi.baidu.com/",
                    new ApiInfo(configuration.getBaiduTranslatorApiKey(), configuration.getBaiduTranslatorSecretKey()),   // 回显所需信息
                    (newValue) -> {   // 用户确定后的回调
                        configuration.setBaiduTranslatorApiKey(newValue.getApiKey().trim());
                        configuration.setBaiduTranslatorSecretKey(newValue.getSecretKey().trim());
                    })
                    .showDialog()));
            youdaoApiMenu.setOnAction((actionEvent -> new ApiDialog("有道翻译", // 标题
                    "https://ai.youdao.com/?keyfrom=old-openapi",
                    new ApiInfo(configuration.getYouDaoTranslatorApiKey(), configuration.getYouDaoTranslatorSecretKey()),   // 回显所需信息
                    (newValue) -> {   // 用户确定后的回调
                        configuration.setYouDaoTranslatorApiKey(newValue.getApiKey().trim());
                        configuration.setYouDaoTranslatorSecretKey(newValue.getSecretKey().trim());
                    })
                    .showDialog()));
        }

        private void initProcessFilter() {
            filterMenu.setOnAction((actionEvent -> {
                try {
                    Stage stage = new Stage();
                    Scene scene = new Scene(App.loadFXML(ControllerConstant.FILTER_CONTROLLER_FXML));
                    stage.initOwner(rootPane.getScene().getWindow());
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
                    Scene scene = new Scene(App.loadFXML(ControllerConstant.WORDS_REPLACER_CONTROLLER_FXML));
                    stage.initOwner(rootPane.getScene().getWindow());
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "词组替换页面打开失败", e);
                }
            }));
        }


        private void initTranslationHistoryNumMenu(SystemConfiguration.HistoryConfig configuration) {
            historyNumMenu.setOnAction((actionEvent -> {
                TextInputDialog dialog = new TextInputDialog("" + configuration.getHistoryNum());
                dialog.setTitle("设置");
                dialog.setHeaderText("翻译历史数量设置");
                dialog.setContentText("输入保存历史数量(不超过100):");
                dialog.initOwner(rootPane.getScene().getWindow());
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
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/ravenxrz/RubberTranslator"));
            } catch (IOException | URISyntaxException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "项目主页打开失败", e);
            }
        });
        // wiki
        wiki.setOnAction((actionEvent) -> {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/ravenxrz/RubberTranslator/wiki"));
            } catch (IOException | URISyntaxException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "使用帮助打开失败", e);
            }
        });
        // issue
        issues.setOnAction((actionEvent -> {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/ravenxrz/RubberTranslator/issues"));
            } catch (IOException | URISyntaxException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "issues打开失败", e);
            }
        }));
    }

    private void initFocusModeMenu() {
        Label label = new Label("专注模式");
        label.setOnMouseClicked((this::switchToFocusMode));
        focusMenu.setText("");
        focusMenu.setGraphic(label);
    }

    private void switchToFocusMode(MouseEvent event) {
        try {
            App.setRoot(ControllerConstant.FOCUS_CONTROLLER_FXML);
            EventBus.getDefault().unregister(this);
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "专注模式打开失败", e);
        }
    }

    private void initHistoryMenu() {
        Label pre = new Label("上一条");
        Label next = new Label("下一条");
        pre.setOnMouseClicked((event -> {
            HistoryEntry entry = SystemResourceManager.getFacade().getHistory().previous();
            if (entry != null) {
                updateTextArea(entry.getOrigin(), entry.getTranslation());
            }
        }));
        next.setOnMouseClicked(event -> {
            HistoryEntry entry = SystemResourceManager.getFacade().getHistory().next();
            if (entry != null) {
                updateTextArea(entry.getOrigin(), entry.getTranslation());
            }
        });
        preHistoryMenu.setText("");
        nextHistoryMenu.setText("");
        preHistoryMenu.setGraphic(pre);
        nextHistoryMenu.setGraphic(next);
    }

    private void initClearMenu() {
        Label label = new Label("清空");
        label.setOnMouseClicked((event -> {
            updateTextArea("", "");
            // 通知facade清空后续模块
            SystemResourceManager.getFacade().clear();
        }));
        clearMenu.setText("");
        clearMenu.setGraphic(label);
    }


    /**
     * 翻译文本控制区域
     *
     * @param origin      原文
     * @param translation 译文
     */
    private void updateTextArea(String origin, String translation) {
        originTextArea.setText(origin);
        translatedTextArea.setText(translation);
    }


    @FXML
    public void onBtnTranslateClick() {
        String originText = originTextArea.getText();
        processTranslate(originText);
    }


    private void processTranslate(String text) {
        SystemResourceManager.getFacade().process(text);
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onClipboardContentInput(ClipboardContentInputEvent event){
        if (event == null) return;
        if(event.isTextType()){
            processTranslate(event.getText());
        }else{
            try {
                String text = OCRUtils.ocr(event.getImage());
                if (text != null) {
                    processTranslate(text);
                }
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "ocr识别错误", e);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onComplete(TranslateCompleteEvent event) {
        if (event == null) return;
        // 不管从哪里会回调，回到UI线程
        Platform.runLater(() -> {
            updateTextArea(event.getOrigin(), event.getTranslation());
        });
    }


}
