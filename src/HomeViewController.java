/* Created by Miho */

        import com.jfoenix.controls.*;
        import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
        import com.jfoenix.controls.RecursiveTreeItem;
        import com.jfoenix.controls.JFXComboBox;
        import com.jfoenix.controls.JFXDatePicker;

        import javafx.beans.property.ReadOnlyObjectWrapper;
        import javafx.fxml.FXMLLoader;
        import javafx.scene.Parent;
        import javafx.scene.Scene;
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
        import javafx.scene.layout.Pane;
        import javafx.stage.Stage;
        import javafx.util.Callback;

        import java.io.IOException;
        import java.net.URL;
        import java.sql.Connection;
        import java.sql.DriverManager;
        import java.sql.ResultSet;
        import java.sql.Statement;
        import java.util.Optional;
        import java.util.ResourceBundle;

public class HomeViewController implements Initializable {

    //[Declarations]

    //search window -----------------------------------------------------
    @FXML
    private TextField search;

    // todothing input field -------------------------------------------------
    @FXML
    private TextField todo;

    @FXML
    private ObservableList<String> subList = FXCollections.observableArrayList("aaa");

    @FXML
    private JFXComboBox<String> subject;

    @FXML
    private JFXComboBox<String> category;

    @FXML
    private Button ok;

    @FXML
    private JFXTextField newSubject;

    @FXML
    private JFXButton addSbjBtn;

    // todolist with timer ----------------------------------------------
    @FXML
    private JFXTreeTableView<TodoInfo> todoListTable;
    final ObservableList<TodoInfo> todoInfoList = FXCollections.observableArrayList();


    // dammy ----------------------------------------------

    @FXML
    private JFXTextField dailyTotalTime;

    @FXML
    private JFXTextField selectedDate;

    @FXML
    private JFXDatePicker datePicker;

    // information to access sql ------------------------------------------
    String msUrl = "jdbc:mysql://localhost:3306/studymode_db";
    String user = "root";
    String password = "";



    // [Methods]

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        setSubjectOption();
        setTableView();
    }

    //search window -----------------------------------------------------
    @FXML
    void searchWeb(ActionEvent event) {
        String word = search.getText();
        runSearchCommand(word);
    }


    public void runSearchCommand(String word) {
        try {
            String url = "http://www.google.com/search?q=" + word;
            String command = "open " + url;
            Runtime.getRuntime().exec(command);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // todothing input field -------------------------------------------------


    void setSubjectOption() {
        try {
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            ResultSet myRs = myStmt.executeQuery("select * from subject_table");
            while(myRs.next()) {
                subject.getItems().add(myRs.getString("subject_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        subject.getItems().add("create new");
    }

    @FXML
    void subjectAction(ActionEvent event) {
        setCategoryOption();
    }

    void setCategoryOption() {
        category.getItems().clear();
        if(subject.getValue() != "create new") {
            try {
                Connection myConn = DriverManager.getConnection(msUrl, user, password);
                Statement myStmt = myConn.createStatement();
                ResultSet myRs = myStmt.executeQuery("select * from category_table where subject_name = '" + subject.getValue() + "'");
                while (myRs.next()) {
                    category.getItems().add(myRs.getString("category_name"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            category.getItems().add("create new");
        } else {
            openNewSbjDialog();
        }
    }

    @FXML
    void categoryAction(ActionEvent event) {

        if(category.getValue() == "create new") {
            openNewCtgDialog();
        }
    }

    @FXML
    void addNewTodo(ActionEvent event) {

        String subjectStr = subject.getValue();
        String categoryStr = category.getValue();
        String todoStr = todo.getText();

        if (!(subjectStr == "" && categoryStr == "" && todoStr == "")){
            try {
                Connection myConn = DriverManager.getConnection(msUrl, user, password);
                Statement myStmt = myConn.createStatement();
                String sql = "insert into todo_table (subject, category, todo)"
                        + " values ('" + subjectStr + "', '" + categoryStr + "', '" + todoStr + "')";
                myStmt.executeUpdate(sql);
                //URL viewUrl = getClass().getResource("HomeView.fxml");
                setTableView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        subject.setValue("");
        category.setValue("");
        todo.setText("");
    }

    // create new Subject

    void openNewSbjDialog() {

        TextInputDialog dialog = new TextInputDialog("");
        dialog.setContentText("Enter new subject:");
        dialog.setTitle("Create new Subject");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newSubject -> addNewSubject(newSubject));
        result.ifPresent(newSubject -> setNewSubject(newSubject));
    }

    void addNewSubject(String newSubject) {
        try {
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "insert into subject_table (subject_name)"
                    + " values ('" + newSubject + "')";
            myStmt.executeUpdate(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setNewSubject(String str) {
        setSubjectOption();
        subject.setValue(str);
    }

    // create new category

    void openNewCtgDialog() {

        TextInputDialog dialog = new TextInputDialog("");
        dialog.setContentText("Enter new category:");
        dialog.setTitle("Create new category");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newCategory -> addNewCategory(newCategory));
        result.ifPresent(newCategory -> setNewCategory(newCategory));
    }

    void addNewCategory(String newCategory) {
        try {
            String subjectStr = subject.getValue();
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "insert into category_table (subject_name, category_name)"
                    + " values ('" + subjectStr + "', " + "'" + newCategory + "')";
            myStmt.executeUpdate(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setNewCategory(String str) {
        setCategoryOption();
        category.setValue(str);
    }

    // todolist with timer ----------------------------------------------
    void setTableView() {
        JFXTreeTableColumn<TodoInfo, String> subjectCl = new JFXTreeTableColumn<>("Subject");
        subjectCl.setPrefWidth(100);
        subjectCl.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TodoInfo, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<TodoInfo, String> param) {
                return param.getValue().getValue().subjectSP;
            }
        });

        JFXTreeTableColumn<TodoInfo, String> categoryCl = new JFXTreeTableColumn<>("Category");
        categoryCl.setPrefWidth(100);
        categoryCl.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TodoInfo, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<TodoInfo, String> param) {
                return param.getValue().getValue().categorySP;
            }
        });

        JFXTreeTableColumn<TodoInfo, String> todoCl = new JFXTreeTableColumn<>("Todo");
        todoCl.setPrefWidth(250);
        todoCl.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TodoInfo, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<TodoInfo, String> param) {
                return param.getValue().getValue().todoSP;
            }
        });

        JFXTreeTableColumn<TodoInfo, String> timerCl = new JFXTreeTableColumn<>("Timer");
        timerCl.setPrefWidth(100);
        timerCl.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TodoInfo, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<TodoInfo, String> param) {
                return param.getValue().getValue().display;
            }
        });

        JFXTreeTableColumn<TodoInfo, JFXButton> switchCl = new JFXTreeTableColumn<>("Switch");
        switchCl.setPrefWidth(100);
        switchCl.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TodoInfo, JFXButton>, ObservableValue<JFXButton>>() {
            @Override
            public ObservableValue<JFXButton> call(TreeTableColumn.CellDataFeatures<TodoInfo, JFXButton> param) {
                return new ReadOnlyObjectWrapper(param.getValue().getValue().switchButton);
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
                Long tsTime = myRs.getLong("total_spent_time");
                todoInfoList.add(new TodoInfo(i, s, c, t, tsTime));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // build tree
        final TreeItem<TodoInfo> root = new RecursiveTreeItem<TodoInfo>(todoInfoList, RecursiveTreeObject::getChildren);
        todoListTable.getColumns().setAll(subjectCl, categoryCl, todoCl, timerCl, switchCl);
        todoListTable.setRoot(root);
        todoListTable.setShowRoot(false);
    }
}
