package com.rubbertranslator.mvp.view.controller.impl;

import com.rubbertranslator.enumtype.*;
import com.rubbertranslator.event.*;
import com.rubbertranslator.mvp.modules.history.HistoryEntry;
import com.rubbertranslator.mvp.presenter.PresenterFactory;
import com.rubbertranslator.mvp.presenter.impl.FocusViewPresenter;
import com.rubbertranslator.mvp.view.controller.IFocusView;
import com.rubbertranslator.system.SystemConfiguration;
import com.rubbertranslator.system.SystemResourceManager;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/9 21:51
 */
public class FocusModeController implements Initializable, IFocusView {
    @FXML
    private VBox rootPane;

    @FXML
    private TextArea textArea;

    @FXML // 退回到主界面
    private Button backBt;
    @FXML  // 翻译按钮
    private Button translateBt;

    @FXML   // 翻译组
    private ToggleGroup translatorGroup;
    @FXML
    private ToggleButton googleTranslator;
    @FXML
    private ToggleButton baiduTranslator;
    @FXML
    private ToggleButton youdaoTranslator;
    @FXML
    private ToggleButton noneTranslator;

    @FXML // 增量复制
    private ToggleButton incrementalCopyMenu;

    @FXML   // 自动复制
    private ToggleButton autoCopyMenu;
    @FXML   // 自动粘贴
    private ToggleButton autoPasteMenu;

    @FXML // 文本格式化
    private ToggleButton textFormatMenu;

    @FXML // 监听剪切板
    private ToggleButton clipboardListenerMenu;
    @FXML
    private ToggleButton dragCopyMenu;

    @FXML // 历史记录
    private Button preHistoryBt;
    @FXML
    private Button nextHistoryBt;

    @FXML   // 保持置顶
    private ToggleButton keepStageTopMenu;

    @FXML   // 清空
    private Button clearBt;

    @FXML
    private Button translateModeBt;

    @FXML   // 复制原文
    private Button copyOriginBt;
    @FXML   // 复制译文
    private Button copyTranslationBt;

    private TextAreaCursorPos cursorPos = TextAreaCursorPos.START;

    // 是否继续接受来自clipboard的文本
    private boolean keepGetTextFromClipboard = true;

    // presenter
    private FocusViewPresenter presenter;

    enum TranslateMode {
        ORIGIN,         // 原文
        TRANSLATED,     // 译文
        PARA_COMPARE    // 段落对比
    }

    private TranslateMode translateMode = TranslateMode.TRANSLATED;

    /**
     * 组件初始化完成后，会调用这个方法
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        start();
    }

    @Override
    public void start() {
        initParams();
        initClickEvents();
        initListeners();
    }

    @Override
    public void destroy() {
        EventBus.getDefault().unregister(this);
    }

    /**
     * 初始化view，根据配置，回显view
     *
     * @param configuration
     */
    @Override
    public void initViews(SystemConfiguration configuration) {
        // set window preSize
        rootPane.setPrefSize(configuration.getLastSize().getX(), configuration.getLastSize().getY());

        // 置顶
        keepStageTopMenu.setSelected(configuration.isKeepTop());
        // 翻译引擎
        switch (configuration.getCurrentTranslator()) {
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
                break;
        }
        // 增量
        incrementalCopyMenu.setSelected(configuration.isIncrementalCopy());
        // 自动复制
        autoCopyMenu.setSelected(configuration.isAutoCopy());
        // 自动粘贴
        autoPasteMenu.setSelected(configuration.isAutoPaste());
        // 监听版
        clipboardListenerMenu.setSelected(configuration.isOpenClipboardListener());
        // 拖拽
        dragCopyMenu.setSelected(configuration.isDragCopy());
        // 文本格式化
        textFormatMenu.setSelected(configuration.isTryFormat());
        // 翻译后位置
        cursorPos = configuration.getTextAreaCursorPos();
        // 翻译模式 [原文|译文|段落对比], 默认为译文
        translateModeBt.setText("显示译文");
    }

    @Override
    public void delayInitViews() {
        // bind short cut for translateBt
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

    /**
     * 初始化参数
     */
    private void initParams() {
        presenter = PresenterFactory.getPresenter(SceneType.FOCUS_SCENE);
        // 拿到presenter，首先注入mode模块，所有mode由systemresource持有
        SystemResourceManager.initPresenter(presenter);
        presenter.setView(this);
        presenter.initView();
    }

    /**
     * 注册监听
     */
    private void initListeners() {
        // 注册监听
        EventBus.getDefault().register(this);
    }


    /**
     * 点击事件
     */
    private void initClickEvents() {
        backBt.setOnAction((event -> presenter.switchScene(SceneType.MAIN_SCENE)));
        translatorGroup.selectedToggleProperty().addListener(this::onTranslatorTypeChanged);
        incrementalCopyMenu.setOnAction((event -> presenter.incrementalCopySwitch(incrementalCopyMenu.isSelected())));
        preHistoryBt.setOnAction((event -> presenter.setHistoryEntry(HistoryEntryIndex.PRE_HISTORY)));
        nextHistoryBt.setOnAction((event -> presenter.setHistoryEntry(HistoryEntryIndex.NEXT_HISTORY)));
        clearBt.setOnAction((event -> presenter.clearText()));
        keepStageTopMenu.setOnAction((event -> presenter.setKeepTop(keepStageTopMenu.isSelected())));
        autoCopyMenu.setOnAction((event -> presenter.autoCopySwitch(autoCopyMenu.isSelected())));
        autoPasteMenu.setOnAction((event -> presenter.autoPasteSwitch(autoPasteMenu.isSelected())));
        copyOriginBt.setOnAction((event -> presenter.copyOriginText()));
        copyTranslationBt.setOnAction((event -> presenter.copyTranslatedText()));
        clipboardListenerMenu.setOnAction((event -> presenter.clipboardSwitch(clipboardListenerMenu.isSelected())));
        dragCopyMenu.setOnAction((event -> presenter.dragCopySwitch(dragCopyMenu.isSelected())));
        textFormatMenu.setOnAction((event -> presenter.textFormatSwitch(textFormatMenu.isSelected())));
        translateBt.setOnAction((event -> presenter.translate(textArea.getText())));
        translateModeBt.setOnAction((event -> presenter.switchTranslateMode()));
    }

    private void onTranslatorTypeChanged(ObservableValue<? extends Toggle> observableValue, Toggle oldValue, Toggle newValue) {
        if (newValue == googleTranslator) {
            presenter.setTranslatorType(TranslatorType.GOOGLE);
        } else if (newValue == baiduTranslator) {
            presenter.setTranslatorType(TranslatorType.BAIDU);
        } else if (newValue == youdaoTranslator) {
            presenter.setTranslatorType(TranslatorType.YOUDAO);
        } else if (newValue == noneTranslator) {
            presenter.setTranslatorType(TranslatorType.YOUDAO);
        } else {
            // 走到这个分支，说明用户点击了当前已经选中的按钮
            oldValue.setSelected(true);
        }
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
    public void autoCopy(boolean open) {
        if (!open) {
            autoPasteMenu.setSelected(false);
        }
    }

    @Override
    public void autoPaste(boolean open) {
        // 自动粘贴依赖于自动复制
        if (open) {
            autoCopyMenu.setSelected(true);
        }
    }

    @Override
    public void switchTranslateMode(HistoryEntry entry) {
        // TODO: it's better to use a "index" variable, but it's simple enough now
        final String[] showText = {"显示原文", "显示译文", "段落对比"};

        // 状态转移
        switch (translateMode) {
            case ORIGIN:
                translateModeBt.setText(showText[1]);
                translateMode = TranslateMode.TRANSLATED;
                break;
            case TRANSLATED:
                translateModeBt.setText(showText[2]);
                translateMode = TranslateMode.PARA_COMPARE;
                break;
            case PARA_COMPARE:
                translateModeBt.setText(showText[0]);
                translateMode = TranslateMode.ORIGIN;
                break;
            default:
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Unknown translate mode in focus mode");
                break;
        }

        this.setText(entry.getOrigin(), entry.getTranslation());
//
//        if (showOrigin.equals(currentState)) { // 需要显示原文
//            textArea.setText(entry.getOrigin());
//            // 下一个状态：显示译文
//            translateModeBt.setText(showTranslation);
//        } else if (showTranslation.equals(currentState)) {
//            textArea.setText(entry.getTranslation());
//            // 下一个状态：显示原文
//            translateModeBt.setText(showOrigin);
//        }
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onClipboardContentInput(ClipboardContentInputEvent event) {
        if (event == null || !keepGetTextFromClipboard) return;
        if (event.isTextType) { // 文字类型
            presenter.translate(event.text);
        } else {                // 图片类型
            presenter.translate(event.image);
        }
    }

    @Override
    public void translateStart() {
        Platform.runLater(() -> {
            keepGetTextFromClipboard = false;
            translateBt.setDisable(true);
            translateModeBt.setDisable(true);
        });
    }

    @Override
    public void translateEnd() {
        Platform.runLater(() -> {
            keepGetTextFromClipboard = true;
            translateBt.setDisable(false);
            translateModeBt.setDisable(false);
            if (cursorPos == TextAreaCursorPos.END) {
                textArea.end();
            }
            EventBus.getDefault().post(new SetWindowUnTransparentEvent());
        });
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onHotKeyInput(HotKeyEvent event) {
        if (event == null) return;
        Platform.runLater(() -> {
            switch (event.hotKeyValue) {
                case HotKey.F5:
                    clipboardListenerMenu.fire();
                    break;
                case HotKey.F6:
                    dragCopyMenu.fire();
                default:
            }
        });
    }

    /**
     * 结合originText和translateText 生成按照"段落“分割的对照翻译结果
     * @param originText     原文
     * @param translatedText 译文
     * @return 段落分割对照文本
     * @note 分割规则，按照\n分割
     */
    private String paraCompareText(String originText, String translatedText) {
        StringBuilder sb = new StringBuilder();
        String[] splitOriginText = originText.split("\n");
        String[] splitTranslateText = translatedText.replaceAll("\t","").split("\n");
        if (splitOriginText.length != splitTranslateText.length) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, String.format("[paragraph compare translate mode]: 不精确的结果，原文拆分组数和译文拆分组数不相同，原文:%s\n译文:%s", originText, translatedText));
            sb.append("当前段落对比结果可能不准确：");
        }
        for (int i = 0; i < splitOriginText.length && i < splitTranslateText.length; i++) {
            sb.append(splitOriginText[i]).append('\n').append(splitTranslateText[i]).append("\n\n");
        }
        return sb.toString();
    }


    @Override
    public void setText(String originText, String translatedText) {
        switch (translateMode) {
            case ORIGIN:
                textArea.setText(originText);
                break;
            case TRANSLATED:
                textArea.setText(translatedText);
                break;
            case PARA_COMPARE:
                textArea.setText(paraCompareText(originText, translatedText));
                break;
        }
    }
}
