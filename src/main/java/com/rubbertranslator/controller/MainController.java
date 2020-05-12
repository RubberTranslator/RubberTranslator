package com.rubbertranslator.controller;

import com.rubbertranslator.App;
import com.rubbertranslator.modules.TranslatorFacade;
import com.rubbertranslator.modules.history.HistoryEntry;
import com.rubbertranslator.modules.system.SystemConfiguration;
import com.rubbertranslator.modules.system.SystemResourceManager;
import com.rubbertranslator.modules.textinput.TextInputListener;
import com.rubbertranslator.modules.textinput.ocr.OCRUtils;
import com.rubbertranslator.modules.translate.Language;
import com.rubbertranslator.modules.translate.TranslatorType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

import java.awt.*;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/6 20:49
 */
public class MainController implements TranslatorFacade.TranslatorFacadeListener, TextInputListener {

    @FXML
    private AnchorPane anchorPane;

    /**
     * --------------------主功能区-----------------------------
     */
    @FXML
    private TextArea originTextArea;
    @FXML
    private TextArea translatedTextArea;

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

    @FXML // 保持段落格式
    private RadioMenuItem keepParagraphMenu;
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

    /**
     * 组件初始化完成后，会调用这个方法
     */
    @FXML
    public void initialize() {
        initListeners();
        initViews();
    }

    private void initListeners() {
        // 注册文本变化监听
        SystemResourceManager.getClipBoardListenerThread().setTextInputListener(this);
        // 注册翻译完成监听
        SystemResourceManager.getFacade().setFacadeListener(this);
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
            Logger.getLogger(BasicSettingMenu.class.getName()).info("初始化功能开关成功");
        }

        private void initBasicSettingOthers(SystemConfiguration configuration) {
            // 设置onActionListener
            clipboardListenerMenu.setOnAction((actionEvent) ->
                    SystemResourceManager.getConfigurationProxy().getTextInputConfig().setOpenClipboardListener(clipboardListenerMenu.isSelected())
            );

            dragCopyMenu.setOnAction((actionEvent) ->
                    SystemResourceManager.getConfigurationProxy().getTextInputConfig().setDragCopy(dragCopyMenu.isSelected()));
            incrementalCopyMenu.setOnAction((actionEvent ->
                    SystemResourceManager.getConfigurationProxy().getTextProcessConfig().getTextPreProcessConfig().setIncrementalCopy(incrementalCopyMenu.isSelected())));
            autoCopyMenu.setOnAction((actionEvent -> {
                if (!autoCopyMenu.isSelected()) {
                    autoPasteMenu.setSelected(false);
                    SystemResourceManager.getConfigurationProxy().getAfterProcessorConfig().setAutoPaste(false);
                }
                SystemResourceManager.getConfigurationProxy().getAfterProcessorConfig().setAutoCopy(autoCopyMenu.isSelected());
            }));
            autoPasteMenu.setOnAction((actionEvent -> {
                // 自动粘贴依赖于自动复制
                if (autoPasteMenu.isSelected()) {
                    autoCopyMenu.setSelected(true);
                    SystemResourceManager.getConfigurationProxy().getAfterProcessorConfig().setAutoCopy(true);
                }
                SystemResourceManager.getConfigurationProxy().getAfterProcessorConfig().setAutoPaste(autoPasteMenu.isSelected());
            }));
            keepParagraphMenu.setOnAction((actionEvent ->
                    SystemResourceManager.getFacade().getTextPreProcessor().setTryToKeepParagraph(keepParagraphMenu.isSelected())));
            keepTopMenu.setOnAction((actionEvent -> {
                App.setKeepTop(keepTopMenu.isSelected());
                // XXX: UI模块的相关功能，需要手动实现保存
                SystemResourceManager.getConfigurationProxy().getUiConfig().setKeepTop(keepTopMenu.isSelected());
            }));


            // 监听剪切板
            clipboardListenerMenu.setSelected(configuration.getTextInputConfig().isOpenClipboardListener());
            clipboardListenerMenu.fire();
            // 拖拽复制
            dragCopyMenu.setSelected(configuration.getTextInputConfig().isDragCopy());
            dragCopyMenu.fire();
            // 增量复制
            incrementalCopyMenu.setSelected(configuration.getTextProcessConfig().getTextPreProcessConfig().isIncrementalCopy());
            incrementalCopyMenu.fire();
            // 自动复制
            autoCopyMenu.setSelected(configuration.getAfterProcessorConfig().isAutoPaste());
            autoCopyMenu.fire();
            // 自动粘贴
            autoPasteMenu.setSelected(configuration.getAfterProcessorConfig().isAutoPaste());
            autoPasteMenu.fire();
            // 保持段落格式
            keepParagraphMenu.setSelected(configuration.getUiConfig().isKeepTop());
            keepParagraphMenu.fire();
            // 置顶
            keepTopMenu.setSelected(configuration.getUiConfig().isKeepTop());
            keepTopMenu.fire();
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
                // TODO: fxml中如何引用枚举？ 这里暂时采用硬编码
                SystemConfiguration.TranslatorConfig translatorConfig = SystemResourceManager.getConfigurationProxy().getTranslatorConfig();
                if (newValue == googleTranslator) {
                    translatorConfig.setCurrentTranslator(TranslatorType.GOOGLE);
                } else if (newValue == baiduTranslator) {
                    translatorConfig.setCurrentTranslator(TranslatorType.BAIDU);
                } else if (newValue == youdaoTranslator) {
                    translatorConfig.setCurrentTranslator(TranslatorType.YOUDAO);
                }
            });
        }

        private void initSrcDestLanguage(Language src, Language dest) {
            // src
            initLanguage(src, srcSimpleChinese, srcTraditionalChinese, srcEnglish, srcFrench, srcJapanese);
            srcDestLanguageChooseEvent(true, sourceLanguageGroup, srcSimpleChinese, srcTraditionalChinese, srcEnglish, srcFrench, srcJapanese);
            // dest
            initLanguage(dest, destSimpleChinese, destTraditionalChinese, destEnglish, destFrench, destJapanese);
            srcDestLanguageChooseEvent(false, destLanguageGroup, destSimpleChinese, destTraditionalChinese, destEnglish, destFrench, destJapanese);

        }

        private void initLanguage(Language type, RadioMenuItem simpleChinese, RadioMenuItem traditional,
                                  RadioMenuItem english, RadioMenuItem french, RadioMenuItem japanese) {
            switch (type) {
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
                if (newValue == simpleChinese) {
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
    private class AdvancedSettingMenu {
        public void init() {
            initAdvancedSettingMenu(SystemResourceManager.getConfigurationProxy());
        }

        private void initAdvancedSettingMenu(SystemConfiguration configuration) {
            initOCR(configuration);
            initTranslationHistoryNumMenu(configuration);
        }

        private void initOCR(SystemConfiguration configuration){

            class OCRInfo{
                public String apiKey;
                public String secretKey;

                public OCRInfo(String apiKey, String secretKey) {
                    this.apiKey = apiKey;
                    this.secretKey = secretKey;
                }
            }

            class OCRDialogContent{
                public TextField apiKeyTf;
                public TextField secretKeyTf;
                public Node create(){
                    // 主内容
                    VBox vBox = new VBox();
                    vBox.setAlignment(Pos.CENTER);
                    vBox.setSpacing(10);
                    Label baiduOCRLabel = new Label("BaiduOCR");
                    // TODO baiduocrlabel 点击跳转到baidu ocr key界面
                    Label apiKeyLabel = new Label("API_KEY");
                    apiKeyTf = new TextField();
                    apiKeyTf.setText(configuration.getTextInputConfig().getBaiduOcrApiKey());
                    Label secretLabel = new Label("SECRET_KEY");
                    secretKeyTf = new TextField();
                    secretKeyTf.setText(configuration.getTextInputConfig().getBaiduOcrSecretKey());
                    vBox.getChildren().addAll(baiduOCRLabel,apiKeyLabel,apiKeyTf,secretLabel,secretKeyTf);
                    return vBox;
                }
            }

            ocrMenu.setOnAction((actionEvent -> {
                Dialog<OCRInfo> dialog = new Dialog<>();
                // 确定和取消
                ButtonType confirmBt = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
                ButtonType cancelBt = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
                dialog.getDialogPane().getButtonTypes().addAll(confirmBt,cancelBt);
                // 设置界面
                OCRDialogContent dialogContent = new OCRDialogContent();
                dialog.getDialogPane().setContent(dialogContent.create());
                dialog.initOwner(anchorPane.getScene().getWindow());

                // 结果转换器
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == confirmBt) {
                        return new OCRInfo(dialogContent.apiKeyTf.getText(),dialogContent.secretKeyTf.getText());
                    }
                    return null;
                });
                // 处理结果
                Optional<OCRInfo> result = dialog.showAndWait();
                result.ifPresent(ocrInfo -> {
                    // 获取更新
                    String apiKey = ocrInfo.apiKey;
                    String secretKey = ocrInfo.secretKey;
                    if("".equals(apiKey) || "".equals(secretKey)){
                        originTextArea.setText("OCR信息不完整,请填写所有字段");
                    }else{
                        SystemResourceManager.getConfigurationProxy().getTextInputConfig().setBaiduOcrApiKey(apiKey);
                        SystemResourceManager.getConfigurationProxy().getTextInputConfig().setBaiduOcrSecretKey(secretKey);
                    }
                });
            }));
        }



        private void initTranslationHistoryNumMenu(SystemConfiguration configuration){
            historyNumMenu.setOnAction((actionEvent ->{
                TextInputDialog dialog = new TextInputDialog(""+configuration.getHistoryConfig().getHistoryNum());
                dialog.setTitle("设置");
                dialog.setHeaderText("翻译历史数量设置");
                dialog.setContentText("输入保存历史数量(不超过100):");
                dialog.initOwner(anchorPane.getScene().getWindow());
                // Traditional way to get the response value.
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(s -> {
                    try{
                        int value = Integer.parseInt(s);
                        if(value<0){
                            throw new NumberFormatException();
                        }
                        // 更新设置
                        configuration.getHistoryConfig().setHistoryNum(value);
                    }catch (NumberFormatException e){
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
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getLogger(MainController.class.getName()).warning(e.getMessage());
        }
    }

    private void initHistoryMenu() {
        Label pre = new Label("上一条");
        Label next = new Label("下一条");
        pre.setOnMouseClicked((event -> {
            HistoryEntry entry = SystemResourceManager.getFacade().getHistory().previous();
            updateTextArea(entry.getOrigin(), entry.getTranslation());
        }));
        next.setOnMouseClicked(event -> {
            HistoryEntry entry = SystemResourceManager.getFacade().getHistory().next();
            updateTextArea(entry.getOrigin(), entry.getTranslation());
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
     * @param origin 原文
     * @param translation 译文
     */
    private void updateTextArea(String origin, String translation) {
        originTextArea.setText(origin);
        translatedTextArea.setText(translation);
    }


    @FXML
    public void onBtnTranslateClick(ActionEvent actionEvent) {
        String originText = originTextArea.getText();
        processTranslate(originText);
    }


    private void processTranslate(String text) {
        SystemResourceManager.getFacade().process(text);
    }

    @Override
    public void onTextInput(String text) {
        Logger.getLogger(this.getClass().getName()).info("Controller执行翻译");
        processTranslate(text);
    }

    @Override
    public void onImageInput(Image image) {
        try {
            String text = OCRUtils.ocr(image);
            if (text != null) {
                onTextInput(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getLogger(this.getClass().getName()).warning(e.getMessage());
        }
    }

    @Override
    public void onComplete(String origin, String translation) {
        // 不管从哪里会回调，回到UI线程
        Platform.runLater(() -> {
            updateTextArea(origin, translation);
        });
    }
}
