package com.rubbertranslator.mvp.view.impl;

import com.rubbertranslator.enumtype.SceneType;
import com.rubbertranslator.mvp.presenter.PresenterFactory;
import com.rubbertranslator.mvp.presenter.impl.FilterViewPresenter;
import com.rubbertranslator.mvp.view.IFilterView;
import com.rubbertranslator.system.SystemConfiguration;
import com.rubbertranslator.system.SystemResourceManager;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/12 21:12
 */
public class FilterController implements IFilterView {
    @FXML
    private VBox vBox;
    @FXML   // processList
    private ListView<String> processList;
    @FXML
    private CheckBox openCheckBox;
    @FXML
    private Button addBt;
    @FXML
    private Button removeBt;

    private FilterViewPresenter presenter;

    // 文件选择器
    private FileChooser fileChooser = new FileChooser();
    {
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Windows进程", "*.exe")
        );
    }

    @FXML
    public void initialize() {
        presenter = (FilterViewPresenter) PresenterFactory.getPresenter(SceneType.FILTER_SCENE);
        SystemResourceManager.initPresenter(presenter);
        presenter.setView(this);
    }


    @Override
    public void initViews(SystemConfiguration configuration) {
        // 进程list初始化
        // 开启多选模式
        processList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Label label = new Label("当前过滤列表为空，请添加要过滤的进程名");
        label.setFont(Font.font(16));
        label.setWrapText(true);
        label.paddingProperty().setValue(new Insets(10));
        processList.setPlaceholder(label);

        // 总开关初始化
        openCheckBox.selectedProperty().addListener(this::onOpenCheckBoxClick);

        openCheckBox.setSelected(configuration.isOpenProcessFilter());
        processList.getItems().addAll(configuration.getProcessList());
    }

    @FXML
    public void onAddButtonClick(){
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(vBox.getScene().getWindow());
        List<String> collect = selectedFiles.stream().map(File::getName).collect(Collectors.toList());
        presenter.addFilterList(collect);
    }


    public <T> void onOpenCheckBoxClick(ObservableValue<? extends T> observable, T oldValue, T newValue){
        presenter.setOpenProcessFilter((Boolean) newValue);
    }

    @FXML
    public void onRemoveButtonClick(){
        ObservableList<String> selectedItems =
                processList.getSelectionModel().getSelectedItems();
        if(selectedItems != null){
            presenter.removeFilterList(selectedItems);;
        }
    }

    @Override
    public void addFilterProcesses(List<String> processNames) {
        processList.getItems().addAll(processNames);
    }

    @Override
    public void removeFilterProcesses(List<String> processNames) {
        processList.getItems().removeAll(processNames);
    }
}
