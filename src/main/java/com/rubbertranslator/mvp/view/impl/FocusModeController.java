package com.rubbertranslator.mvp.view.impl;

import com.rubbertranslator.App;
import com.rubbertranslator.entity.ControllerFxmlPath;
import com.rubbertranslator.enumtype.HistoryEntryIndex;
import com.rubbertranslator.enumtype.SceneType;
import com.rubbertranslator.event.ClipboardContentInputEvent;
import com.rubbertranslator.event.MouseClickPositionEvent;
import com.rubbertranslator.modules.history.HistoryEntry;
import com.rubbertranslator.modules.textinput.ocr.OCRUtils;
import com.rubbertranslator.modules.translate.TranslatorType;
import com.rubbertranslator.mvp.presenter.PresenterFactory;
import com.rubbertranslator.mvp.presenter.impl.FocusViewPresenter;
import com.rubbertranslator.mvp.view.IFocusView;
import com.rubbertranslator.system.SystemConfiguration;
import com.rubbertranslator.system.SystemResourceManager;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/9 21:51
 */
public class FocusModeController implements Initializable,ChangeListener<Boolean>, IFocusView {
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

    @FXML   // 隐匿模式开关
    private ToggleButton autoHideBt;

    // 辅助变量，记录鼠标点击位置
    private Point currentMouseClickPos;
    // 屏幕缩放率
    private double screenScaleRatio;

    // window stage 引用
    private Stage appStage;

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

    @Override
    public void initViews(SystemConfiguration configuration) {
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
        // auto hide
        autoHideBt.setSelected(configuration.isAutoHide());
        // resize window
        Point lastSize = configuration.getLastSize();
        if (lastSize.getX() != 0 && lastSize.getY() != 0) {
            rootPane.setPrefSize(lastSize.getX(),lastSize.getY());
        }
    }


    /**
     * 初始化参数
     */
    private void initParams(){
        screenScaleRatio = Toolkit.getDefaultToolkit().getScreenResolution()/96.0;
        presenter = (FocusViewPresenter) PresenterFactory.getPresenter(SceneType.FOCUS_SCENE);
        // 拿到presenter，首先注入mode模块，所有mode由systemresource持有
        SystemResourceManager.initPresenter(presenter);
        presenter.setView(this);

        // 延迟load
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //初始化stage
                appStage = (Stage) rootPane.getScene().getWindow();
                appStage.setAlwaysOnTop(keepStageTopBt.isSelected());
                appStage.focusedProperty().addListener(FocusModeController.this);
            }
        },500);

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
        displayTextBt.setOnAction((event -> ((FocusViewPresenter)presenter).switchBetweenOriginAndTranslatedText()));
        autoHideBt.setOnAction((event -> ((FocusViewPresenter)presenter).autoHideWindow(autoHideBt.isSelected())));
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

    /**
     * 隐匿模式：hide window
     */
    @Override
    public void hideWindow(){
        Stage window  = appStage;
        // 回到ui线程
        Platform.runLater(()->{
            if(window.isShowing())
            {
                window.hide();
            }
        });
    }

    @Override
    public void setKeepTop(boolean isKeep) {
        appStage.setAlwaysOnTop(isKeep);
    }

    @Override
    public void switchScene(SceneType type) {
        App.switchScene(type);
    }

    /**
     * 隐匿模式：show window
     * 跟随鼠标显示，同时window不能超过界面宽度
     */
    @Override
    public void showWindow(){
        Stage window  = appStage;
        // 回到ui线程
        Platform.runLater(()->{
            if(currentMouseClickPos!=null)
            {
                double mouseX = currentMouseClickPos.getX()/screenScaleRatio;
                double mouseY= currentMouseClickPos.getY()/screenScaleRatio;
                double width = window.getWidth();
                double height = window.getHeight();
                double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
                double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
                if(mouseX + width > screenWidth){
                    mouseX = screenWidth - width - 20;
                }
                if(mouseY + height > screenHeight){
                    mouseY = screenHeight - height - 20;
                }

                window.setX(mouseX);
                window.setY(mouseY+20);    // y 方向+一个delta，避免覆盖选中文字

                if(!window.isShowing())
                {
                    window.show();
                    Logger.getLogger(this.getClass().getName()).info("window show");
                }
            }
        });
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean focus) {
        if(!focus){
            if(autoHideBt.isSelected()
            &&  clipboardListenerMenu.isSelected()     // 耦合监听剪切板线程，因为触发showWindow的动作是当剪切板有新内容时
            && ControllerFxmlPath.FOCUS_CONTROLLER_FXML.equals(appStage.getScene().getUserData())){
                Logger.getLogger(this.getClass().getName()).info("window hide");
                hideWindow();
            }
        }
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

        /*
          下面的代码for 隐匿模式
          隐匿模式下， 自动显示
         */
        // 是否需要显示
        if(autoHideBt.isSelected()
                // 耦合FOCUS界面， 避免当从FOCUS CONTROLLER切换到MAIN CONTROLLER时，main controller也有隐匿效果
        && ControllerFxmlPath.FOCUS_CONTROLLER_FXML.equals(appStage.getScene().getUserData()))
        {
            // 获取焦点
            appStage.requestFocus();
            // 展示
            showWindow();
        }
    }

    /**
     * 鼠标点击时
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMouseClick(MouseClickPositionEvent event) {
        if (event == null) return;
        currentMouseClickPos = event.getPoint();
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
