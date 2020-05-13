package com.rubbertranslator.controller;

import com.rubbertranslator.modules.system.SystemResourceManager;
import com.rubbertranslator.modules.textprocessor.post.WordsPair;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/13 8:53
 */
public class WordsReplacerController implements Initializable {
    @FXML
    private VBox vBox;
    @FXML   //
    private TableView<WordsPair> wordsPairTableView;
    @FXML
    private TableColumn<WordsPair,String> sourceCol;
    @FXML
    private TableColumn<WordsPair,String> destCol;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // placeholder
        Label label = new Label("当前替换词组为空");
        label.setFont(Font.font(16));
        label.setWrapText(true);
        label.paddingProperty().setValue(new Insets(10));
        wordsPairTableView.setPlaceholder(label);
        // 多选
        wordsPairTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // 单元格可编辑
        //给需要编辑的列设置属性
        sourceCol.setCellFactory(TextFieldTableCell.forTableColumn());
        sourceCol.setOnEditCommit(
                (TableColumn.CellEditEvent<WordsPair, String> t) -> {
                    String newValue = t.getNewValue();
                    if("".equals(newValue)){
                        return;
                    }
                    if(checkDuplicateItem(newValue)){
//                        t.getTableView().getItems().get(
//                                t.getTablePosition().getRow()).setSrc("存在相同key！");
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("提示");
                        alert.setHeaderText("重复元素");
                        alert.setContentText("<<"+newValue+">>条目已存在");
                        alert.initOwner(vBox.getScene().getWindow());
                        alert.showAndWait();
                    }else{
                        t.getTableView().getItems().get(
                                t.getTablePosition().getRow()).setSrc(newValue);
                    }

                });
        destCol.setCellFactory(TextFieldTableCell.forTableColumn());
        destCol.setOnEditCommit(
                (TableColumn.CellEditEvent<WordsPair, String> t) -> {
                    String newValue = t.getNewValue();
                    if(!"".equals(newValue)){
                        t.getTableView().getItems().get(
                                t.getTablePosition().getRow()).setDest(newValue);
                    }

                });

        // 单元格数据绑定
        sourceCol.setCellValueFactory(new PropertyValueFactory<>("src"));
        destCol.setCellValueFactory(new PropertyValueFactory<>("dest"));
        // 回显
        wordsPairTableView.getItems().addAll(
                SystemResourceManager.getConfigurationProxy().getTextProcessConfig().getTextPostProcessConfig().getWordsReplacerConfig().getWordsPairs()
        );
    }

    /**
     * 重复条目
     * @param text 需要检查的文本
     * @return true 存在重复条目
     *          false 不存在
     */
    private boolean checkDuplicateItem(String text){
        return wordsPairTableView.getItems().parallelStream().anyMatch(
                wordsPair -> wordsPair.getSrc().equals(text)
        );
    }


    @FXML
    public void onAddButtonClick(){
        wordsPairTableView.getItems().add(new WordsPair("译文样例","替换样例"));
    }

    @FXML
    public void onRemoveButtonClick(){
        ObservableList<WordsPair> selectedItems = wordsPairTableView.getSelectionModel().getSelectedItems();
        wordsPairTableView.getItems().removeAll(selectedItems);
    }

    @FXML
    public void onConfirmButtonClick(){
        // 应用生效并持久化
        Set<WordsPair> set = new HashSet<>(wordsPairTableView.getItems());
        SystemResourceManager.getConfigurationProxy().getTextProcessConfig().getTextPostProcessConfig()
                .getWordsReplacerConfig().setWordsPairs(set);
        ((Stage)(vBox.getScene().getWindow())).close();
    }



}
