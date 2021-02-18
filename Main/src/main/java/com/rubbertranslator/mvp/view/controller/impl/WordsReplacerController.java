package com.rubbertranslator.mvp.view.controller.impl;

import com.rubbertranslator.entity.WordsPair;
import com.rubbertranslator.enumtype.SceneType;
import com.rubbertranslator.mvp.presenter.PresenterFactory;
import com.rubbertranslator.mvp.presenter.impl.WordsReplacerPresenter;
import com.rubbertranslator.mvp.view.controller.IWordsReplacerView;
import com.rubbertranslator.system.ProgramPaths;
import com.rubbertranslator.system.SystemConfiguration;
import com.rubbertranslator.system.SystemResourceManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/13 8:53
 */
public class WordsReplacerController implements Initializable, IWordsReplacerView {
    @FXML
    private VBox vBox;
    @FXML   //
    private TableView<WordsPair> wordsPairTableView;
    @FXML
    private TableColumn<WordsPair, String> sourceCol;
    @FXML
    private TableColumn<WordsPair, String> destCol;

    private WordsReplacerPresenter presenter;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        presenter = PresenterFactory.getPresenter(SceneType.WORDS_REPLACE_SCENE);
        SystemResourceManager.initPresenter(presenter);
        presenter.setView(this);
        presenter.initView();
    }


    @Override
    public void initViews(SystemConfiguration configuration) {
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
                    if ("".equals(newValue)) {
                        return;
                    }
                    if (checkDuplicateItem(newValue)) {
//                        t.getTableView().getItems().get(
//                                t.getTablePosition().getRow()).setSrc("存在相同key！");
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("提示");
                        alert.setHeaderText("重复元素");
                        alert.setContentText("<<" + newValue + ">>条目已存在");
                        alert.initOwner(vBox.getScene().getWindow());
                        alert.showAndWait();
                    } else {
                        t.getTableView().getItems().get(
                                t.getTablePosition().getRow()).setFirst(newValue);
                    }

                });
        destCol.setCellFactory(TextFieldTableCell.forTableColumn());
        destCol.setOnEditCommit(
                (TableColumn.CellEditEvent<WordsPair, String> t) -> {
                    String newValue = t.getNewValue();
                    if (!"".equals(newValue)) {
                        t.getTableView().getItems().get(
                                t.getTablePosition().getRow()).setSecond(newValue);
                    }

                });

        // 单元格数据绑定
        sourceCol.setCellValueFactory(new PropertyValueFactory<>("first"));
        destCol.setCellValueFactory(new PropertyValueFactory<>("second"));
        // 回显
        wordsPairTableView.getItems().addAll(
                configuration.getWordsPairs()
        );
    }

    /**
     * 重复条目
     *
     * @param text 需要检查的文本
     * @return true 存在重复条目
     * false 不存在
     */
    private boolean checkDuplicateItem(String text) {
        return wordsPairTableView.getItems().parallelStream().anyMatch(
                wordsPair -> wordsPair.getSecond().equals(text)
        );
    }

    @FXML
    public void onImportFromTxtButtonClick() {
        // show filechooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        File selectedFile = fileChooser.showOpenDialog(vBox.getScene().getWindow());
        if (selectedFile == null) return;

        List<WordsPair> wordsPairs = parseWordTxt(selectedFile);
        wordsPairs.forEach(System.out::println);
        // do add
        wordsPairTableView.getItems().addAll(wordsPairs);
    }

    @FXML
    public void onExportToTxtButtonClick() {
        Set<WordsPair> set = new HashSet<>(wordsPairTableView.getItems());
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("提示");
        if (set.isEmpty()) {
            alert.setHeaderText("当前词组为空");
        } else {
            String exportPath = ProgramPaths.exportDir;
            if (exportPath == null) return;
            if (exportPath.charAt(exportPath.length() - 1) == '/') {
                exportPath = exportPath + "/RubberTranslator/export/words.txt";
            } else {
                exportPath = exportPath + "RubberTranslator/export/words.txt";
            }
            doExport(set, exportPath);
            alert.setHeaderText("已导出至：" + exportPath);
        }
        alert.initOwner(vBox.getScene().getWindow());
        alert.showAndWait();
    }

    private void doExport(Set<WordsPair> set, String exportPath) {
        if (exportPath == null) return;
        File file = new File(exportPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            for (WordsPair wp : set) {
                String wpStr = wp.getFirst() + ":" + wp.getSecond();
                out.write(wpStr + "\n");
            }
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).info(e.getLocalizedMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 从txt中解析词组
     * 词组格式
     * 原文:译文
     *
     * @param file
     * @return
     */
    List<WordsPair> parseWordTxt(File file) {
        BufferedReader br = null;
        List<WordsPair> wordsPairs = new ArrayList<>();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = br.readLine()) != null) {
                String[] words = line.split(":|：");
                if (words.length == 1) {
                    continue;
                }
                wordsPairs.add(new WordsPair(words[0], words[1]));
            }
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).info(e.getLocalizedMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return wordsPairs;
    }


    @FXML
    public void onAddButtonClick() {
        wordsPairTableView.getItems().add(new WordsPair("译文样例(支持java正则表达式)", "替换样例"));
    }

    @FXML
    public void onRemoveButtonClick() {
        ObservableList<WordsPair> selectedItems = wordsPairTableView.getSelectionModel().getSelectedItems();
        wordsPairTableView.getItems().removeAll(selectedItems);
    }

    @FXML
    public void onConfirmButtonClick() {
        // 应用生效并持久化
        Set<WordsPair> set = new HashSet<>(wordsPairTableView.getItems());
        presenter.apply(set);

    }

    @Override
    public void apply() {
        ((Stage) (vBox.getScene().getWindow())).close();
    }
}
