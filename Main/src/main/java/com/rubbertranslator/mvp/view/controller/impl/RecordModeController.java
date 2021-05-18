package com.rubbertranslator.mvp.view.controller.impl;

import com.rubbertranslator.enumtype.*;
import com.rubbertranslator.event.*;
import com.rubbertranslator.mvp.presenter.PresenterFactory;
import com.rubbertranslator.mvp.presenter.impl.RecordViewPresenter;
import com.rubbertranslator.mvp.view.IRecordView;
import com.rubbertranslator.system.ProgramPaths;
import com.rubbertranslator.system.SystemConfiguration;
import com.rubbertranslator.system.SystemResourceManager;
import com.rubbertranslator.utils.ExploreUtil;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date  2021/2/4 9:04
 */
public class RecordModeController implements Initializable, IRecordView {
    @FXML
    private VBox rootPane;

    @FXML
    private TextArea originTextArea;

    @FXML
    private TextArea translateTextArea;

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
    private ToggleButton originRecordMode;
    @FXML
    private ToggleButton translateRecordMode;
    @FXML
    private ToggleButton bilingualRecordMode;

    @FXML   // 保持置顶
    private ToggleButton keepStageTopMenu;

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

    @FXML
    private Button correctEntryMenu;

    @FXML
    private Button deleteEntryBt;

    @FXML
    private Button openExportDirBt;

    @FXML
    private ToggleButton startEndMenu;

    @FXML
    private Text historyNumText;

    // 是否继续接受来自clipboard的文本
    private boolean keepGetTextFromClipboard = true;

    // 导出类型组
    private List<RecordModeType> types = new ArrayList<>();


    // presenter
    private RecordViewPresenter presenter;

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
        presenter.restoreAutoCopyPasteConfig();
        presenter.clearHistoryConfig();
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
                Logger.getLogger(this.getClass().getName()).info("未选择有效翻译引擎");
                break;
        }
        // 记录模式--默认为译文
        translateRecordMode.setSelected(true);
        types.add(RecordModeType.TRANSLATE_RECORD_MODE);
        presenter.setRecordModeType(types);
        // 置顶
        keepStageTopMenu.setSelected(configuration.isKeepTop());
        // 文本格式化
        textFormatMenu.setSelected(configuration.isTryFormat());
        // 监听版
        clipboardListenerMenu.setSelected(configuration.isOpenClipboardListener());
        // 拖拽
        dragCopyMenu.setSelected(configuration.isDragCopy());
    }

    @Override
    public void delayInitViews() {
        // bind short cut for translateBt
        rootPane.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.T,
                    KeyCombination.CONTROL_DOWN);

            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    if (!startEndMenu.isSelected()) {
                        originTextArea.setText("请先点击【开始】记录");
                        return;
                    }
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
        presenter = PresenterFactory.getPresenter(SceneType.RECORD_SCENE);
        // 拿到presenter，首先注入mode模块，所有mode由systemresource持有
        SystemResourceManager.initPresenter(presenter);
        presenter.setView(this);
        presenter.initView();
        // close auto paste and copy
        presenter.closeAndSaveAutoCopyPaste();
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
        translateBt.setOnAction((event -> {
            if (!startEndMenu.isSelected()) {
                originTextArea.setText("请先点击【开始】记录");
                return;
            }
            presenter.translate(originTextArea.getText());
        }));
        translatorGroup.selectedToggleProperty().addListener(this::onTranslatorTypeChanged);
        // 记录模式
        originRecordMode.setOnAction(this::onRecordModeChanged);
        translateRecordMode.setOnAction(this::onRecordModeChanged);
        bilingualRecordMode.setOnAction(this::onRecordModeChanged);
        keepStageTopMenu.setOnAction((event -> presenter.setKeepTop(keepStageTopMenu.isSelected())));
        textFormatMenu.setOnAction((event -> presenter.textFormatSwitch(textFormatMenu.isSelected())));
        clipboardListenerMenu.setOnAction((event -> presenter.clipboardSwitch(clipboardListenerMenu.isSelected())));
        dragCopyMenu.setOnAction((event -> presenter.dragCopySwitch(dragCopyMenu.isSelected())));
        preHistoryBt.setOnAction((event -> presenter.setHistoryEntry(HistoryEntryIndex.PRE_HISTORY)));
        nextHistoryBt.setOnAction((event -> presenter.setHistoryEntry(HistoryEntryIndex.NEXT_HISTORY)));
        correctEntryMenu.setOnAction((event -> presenter.correctCurrentEntry(originTextArea.getText(), translateTextArea.getText())));
        deleteEntryBt.setOnAction((event -> presenter.deleteCurrentEntry()));
        openExportDirBt.setOnAction((event -> openExportDir()));
        startEndMenu.setOnAction((event -> presenter.record(startEndMenu.isSelected())));
    }

    private void openExportDir() {
        ExploreUtil.openExplore(ProgramPaths.exportDir, s -> originTextArea.setText(s));
    }

    private void onRecordModeChanged(ActionEvent actionEvent) {
        types.clear();
        if (originRecordMode.isSelected()) types.add(RecordModeType.ORIGIN_RECORD_MODE);
        if (translateRecordMode.isSelected()) types.add(RecordModeType.TRANSLATE_RECORD_MODE);
        if (bilingualRecordMode.isSelected()) types.add(RecordModeType.BILINGUAL_RECORD_MODE);
        presenter.setRecordModeType(types);
    }

    private void onTranslatorTypeChanged(ObservableValue<? extends Toggle> observableValue, Toggle oldValue, Toggle newValue) {
        if (newValue == googleTranslator) {
            presenter.setTranslatorType(TranslatorType.GOOGLE);
        } else if (newValue == baiduTranslator) {
            presenter.setTranslatorType(TranslatorType.BAIDU);
        } else if (newValue == youdaoTranslator) {
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
        if (startEndMenu.isSelected()) {
            originTextArea.setText("请先关闭记录,再返回,否则可能会丢失数据");
        } else {
            EventBus.getDefault().post(new SwitchSceneEvent(type));
        }
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
            EventBus.getDefault().post(new SetWindowUnTransparentEvent());
        });
    }

    @Override
    public void setText(String originText, String translatedText) {
        originTextArea.setText(originText);
        translateTextArea.setText(translatedText);
    }

    @Override
    public void recordStart(String recordPath) {
        // 初始化--清空
        historyNumText.setText("0/0");
        originTextArea.setText("");
        translateTextArea.setText("");
    }

    @Override
    public void recordEnd(String recordPath) {
        historyNumText.setText(0 + "");
        originTextArea.setText("已导出至:" + recordPath);
        translateTextArea.setText("");
    }


    @Override
    public void setHistoryNum(int current, int total) {
        historyNumText.setText(current + "/" + total);
    }

    @Override
    public void correctCallBack(String msg) {
        String text = msg + "\n" + originTextArea.getText();
        originTextArea.setText(text);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onClipboardContentInput(ClipboardContentInputEvent event) {
        if (event == null || !keepGetTextFromClipboard) return;
        if (!startEndMenu.isSelected()) {
            originTextArea.setText("请先点击【开始】记录");
            return;
        }
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
                    clipboardListenerMenu.fire();
                    break;
                case HotKey.F6:
                    dragCopyMenu.fire();
                default:
            }
        });
    }

}
