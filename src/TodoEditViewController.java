/* Created by Miho */

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import javafx.fxml.FXML;
import javafx.scene.control.TreeTableColumn;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;

import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.chart.*;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;


public class TodoEditViewController implements Initializable {

    @FXML
    private JFXTreeTableView<TodoInfo> notDoneTable;

    @FXML
    private JFXTreeTableView<TodoInfo> doneTable;

    @FXML
    private Button refreshBtn;




    // information to access sql ------------------------------------------
    String msUrl = "jdbc:mysql://localhost:3306/studymode_db";
    String user = "root";
    String password = "";



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTodoViews();
    }

    void setTodoViews() {
        final ObservableList<TodoInfo> notDoneList = FXCollections.observableArrayList();
        final ObservableList<TodoInfo> doneList = FXCollections.observableArrayList();

        // notDoneTable
        JFXTreeTableColumn<TodoInfo, String> subjectCl1 = new JFXTreeTableColumn<>("Subject");
        subjectCl1.setPrefWidth(100);
        subjectCl1.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TodoInfo, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<TodoInfo, String> param) {
                return param.getValue().getValue().subjectSP;
            }
        });

        JFXTreeTableColumn<TodoInfo, String> categoryCl1 = new JFXTreeTableColumn<>("Category");
        categoryCl1.setPrefWidth(100);
        categoryCl1.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TodoInfo, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<TodoInfo, String> param) {
                return param.getValue().getValue().categorySP;
            }
        });

        JFXTreeTableColumn<TodoInfo, String> todoCl1 = new JFXTreeTableColumn<>("Todo");
        todoCl1.setPrefWidth(250);
        todoCl1.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TodoInfo, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<TodoInfo, String> param) {
                return param.getValue().getValue().todoSP;
            }
        });

        JFXTreeTableColumn<TodoInfo, JFXCheckBox> doneCl1 = new JFXTreeTableColumn<>("Done");
        doneCl1.setPrefWidth(100);
        doneCl1.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TodoInfo, JFXCheckBox>, ObservableValue<JFXCheckBox>>() {
            @Override
            public ObservableValue<JFXCheckBox> call(TreeTableColumn.CellDataFeatures<TodoInfo, JFXCheckBox> param) {
                return new ReadOnlyObjectWrapper(param.getValue().getValue().doneCheck);
            }
        });

        JFXTreeTableColumn<TodoInfo, JFXButton> deleteCl1 = new JFXTreeTableColumn<>("Delete");
        deleteCl1.setPrefWidth(100);
        deleteCl1.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TodoInfo, JFXButton>, ObservableValue<JFXButton>>() {
            @Override
            public ObservableValue<JFXButton> call(TreeTableColumn.CellDataFeatures<TodoInfo, JFXButton> param) {
                return new ReadOnlyObjectWrapper(param.getValue().getValue().deleteCheck);
            }
        });

        // doneTable
        JFXTreeTableColumn<TodoInfo, String> subjectCl2 = new JFXTreeTableColumn<>("Subject");
        subjectCl2.setPrefWidth(100);
        subjectCl2.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TodoInfo, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<TodoInfo, String> param) {
                return param.getValue().getValue().subjectSP;
            }
        });

        JFXTreeTableColumn<TodoInfo, String> categoryCl2 = new JFXTreeTableColumn<>("Category");
        categoryCl2.setPrefWidth(100);
        categoryCl2.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TodoInfo, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<TodoInfo, String> param) {
                return param.getValue().getValue().categorySP;
            }
        });

        JFXTreeTableColumn<TodoInfo, String> todoCl2 = new JFXTreeTableColumn<>("Todo");
        todoCl2.setPrefWidth(250);
        todoCl2.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TodoInfo, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<TodoInfo, String> param) {
                return param.getValue().getValue().todoSP;
            }
        });

        JFXTreeTableColumn<TodoInfo, JFXCheckBox> doneCl2 = new JFXTreeTableColumn<>("Done");
        doneCl2.setPrefWidth(80);
        doneCl2.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TodoInfo, JFXCheckBox>, ObservableValue<JFXCheckBox>>() {
            @Override
            public ObservableValue<JFXCheckBox> call(TreeTableColumn.CellDataFeatures<TodoInfo, JFXCheckBox> param) {
                return new ReadOnlyObjectWrapper(param.getValue().getValue().doneCheck);
            }
        });

        JFXTreeTableColumn<TodoInfo, JFXButton> deleteCl2 = new JFXTreeTableColumn<>("Delete");
        deleteCl2.setPrefWidth(80);
        deleteCl2.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TodoInfo, JFXButton>, ObservableValue<JFXButton>>() {
            @Override
            public ObservableValue<JFXButton> call(TreeTableColumn.CellDataFeatures<TodoInfo, JFXButton> param) {
                return new ReadOnlyObjectWrapper(param.getValue().getValue().deleteCheck);
            }
        });


        try {
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            ResultSet myRs = myStmt.executeQuery("select * from todo_table");
            while(myRs.next()) {
                int i = myRs.getInt("todo_id");
                String s = myRs.getString("subject");
                String c = myRs.getString("category");
                String t = myRs.getString("todo");
                long tsTime = myRs.getLong("total_spent_time");
                boolean isDone = myRs.getBoolean("isDone");
                boolean isVisible = myRs.getBoolean("isVisible");
                if (isVisible) {
                    if (!isDone) {
                        notDoneList.add(new TodoInfo(i, s, c, t, tsTime, isDone, isVisible));
                    } else {
                        doneList.add(new TodoInfo(i, s, c, t, tsTime, isDone, isVisible));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // build tree
        final TreeItem<TodoInfo> root1 = new RecursiveTreeItem<TodoInfo>(notDoneList, RecursiveTreeObject::getChildren);
        notDoneTable.getColumns().setAll(subjectCl1, categoryCl1, todoCl1, doneCl1, deleteCl1);
        notDoneTable.setRoot(root1);
        notDoneTable.setShowRoot(false);

        final TreeItem<TodoInfo> root2 = new RecursiveTreeItem<TodoInfo>(doneList, RecursiveTreeObject::getChildren);
        doneTable.getColumns().setAll(subjectCl2, categoryCl2, todoCl2, doneCl2, deleteCl2);
        doneTable.setRoot(root2);
        doneTable.setShowRoot(false);

    }

    @FXML
    void refreshTable(ActionEvent event) {
        setTodoViews();
    }

}
