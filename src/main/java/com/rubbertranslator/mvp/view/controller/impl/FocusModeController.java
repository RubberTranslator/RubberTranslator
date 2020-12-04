package com.rubbertranslator.mvp.view.controller.impl;

import com.rubbertranslator.entity.ControllerFxmlPath;
import com.rubbertranslator.enumtype.HistoryEntryIndex;
import com.rubbertranslator.enumtype.SceneType;
import com.rubbertranslator.event.ClipboardContentInputEvent;
import com.rubbertranslator.event.SetKeepTopEvent;
import com.rubbertranslator.event.SwitchSceneEvent;
import com.rubbertranslator.mvp.modules.history.HistoryEntry;
import com.rubbertranslator.mvp.modules.textinput.ocr.OCRUtils;
import com.rubbertranslator.enumtype.TranslatorType;
import com.rubbertranslator.mvp.presenter.PresenterFactory;
import com.rubbertranslator.mvp.presenter.impl.FocusViewPresenter;
import com.rubbertranslator.mvp.view.controller.IFocusView;
import com.rubbertranslator.system.SystemConfiguration;
import com.rubbertranslator.system.SystemResourceManager;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/9 21:51
 */
public class FocusModeController implements Initializable,IFocusView {
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
    private ToggleButton keepStageTopBt;

    @FXML   // 清空
    private Button clearBt;

    @FXML   // 显示文本： 显示原文或者显示译文
    private Button displayTextBt;

    @FXML   // 复制原文
    private Button copyOriginBt;
    @FXML   // 复制译文
    private Button copyTranslationBt;

    // presenter
    private FocusViewPresenter presenter;


    /**
     * 组件初始化完成后，会调用这个方法
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initParams();
        initClickEvents();
        initListeners();
    }

    /**
     * 初始化view，根据配置，回显view
     * @param configuration
     */
    @Override
    public void initViews(SystemConfiguration configuration) {
        // set window preSize
        rootPane.setPrefSize(550,350);

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
        keepStageTopBt.setSelected(configuration.isKeepTop());

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
    }


    /**
     * 初始化参数
     */
    private void initParams(){
        presenter = (FocusViewPresenter) PresenterFactory.getPresenter(SceneType.FOCUS_SCENE);
        // 拿到presenter，首先注入mode模块，所有mode由systemresource持有
        SystemResourceManager.initPresenter(presenter);
        presenter.setView(this);
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
        keepStageTopBt.setOnAction((event -> presenter.setKeepTop(keepStageTopBt.isSelected())));
        autoCopyMenu.setOnAction((event -> presenter.autoCopySwitch(autoCopyMenu.isSelected())));
        autoPasteMenu.setOnAction((event -> presenter.autoPasteSwitch(autoPasteMenu.isSelected())));
        copyOriginBt.setOnAction((event -> presenter.copyOriginText()));
        copyTranslationBt.setOnAction((event -> presenter.copyTranslatedText()));
        clipboardListenerMenu.setOnAction((event -> presenter.clipboardSwitch(clipboardListenerMenu.isSelected())));
        dragCopyMenu.setOnAction((event -> presenter.dragCopySwitch(dragCopyMenu.isSelected())));
        textFormatMenu.setOnAction((event -> presenter.textFormatSwitch(textFormatMenu.isSelected())));
        translateBt.setOnAction((event -> presenter.translate(textArea.getText())));
        displayTextBt.setOnAction((event -> presenter.switchBetweenOriginAndTranslatedText()));
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
    public void switchBetweenOriginAndTranslatedText(HistoryEntry entry) {
        final String showOrigin = "显示原文";
        final String showTranslation = "显示译文";
        final String currentState = displayTextBt.getText();

        if (showOrigin.equals(currentState)) { // 需要显示原文
            textArea.setText(entry.getOrigin());
            // 下一个状态：显示译文
            displayTextBt.setText(showTranslation);
        } else if (showTranslation.equals(currentState)) {
            textArea.setText(entry.getTranslation());
            // 下一个状态：显示原文
            displayTextBt.setText(showOrigin);
        }
    }



    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onClipboardContentInput(ClipboardContentInputEvent event) {
        if (event == null) return;
        if(!ControllerFxmlPath.FOCUS_CONTROLLER_FXML.equals(rootPane.getScene().getUserData()))
            return;
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

    @Override
    public void translateStart() {
        Platform.runLater(()->{
            translateBt.setDisable(true);
            displayTextBt.setDisable(true);
        });
    }

    @Override
    public void translateEnd() {
        final String showOrigin = "显示原文";
        Platform.runLater(()->{
            translateBt.setDisable(false);
            // displayBt reinitialize
            displayTextBt.setText(showOrigin);
            displayTextBt.setDisable(false);
        });
    }

    @Override
    public void setText(String originText, String translatedText) {
        textArea.setText(translatedText);
    }
}
