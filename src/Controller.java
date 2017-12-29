/* Created by Miho */

        import com.jfoenix.controls.*;
        import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
        import com.jfoenix.controls.RecursiveTreeItem;

        import javafx.animation.AnimationTimer;
        import javafx.scene.control.TreeItem;
        import javafx.scene.control.TreeTableColumn;
        import com.jfoenix.controls.JFXTreeTableColumn;
        import com.jfoenix.controls.JFXTreeTableView;

        import javafx.beans.property.SimpleStringProperty;
        import javafx.beans.property.StringProperty;
        import javafx.beans.value.ObservableValue;
        import javafx.collections.FXCollections;
        import javafx.collections.ObservableList;
        import javafx.event.ActionEvent;
        import javafx.fxml.FXML;
        import javafx.fxml.Initializable;
        import javafx.scene.control.*;
        import javafx.scene.control.cell.PropertyValueFactory;
        import javafx.util.Callback;

        import java.net.URL;
        import java.sql.Connection;
        import java.sql.DriverManager;
        import java.sql.ResultSet;
        import java.sql.Statement;
        import java.util.ResourceBundle;

public class Controller implements Initializable {

    //[Declarations]

    //search window -----------------------------------------------------
    @FXML
    private TextField search;

    // task input field -------------------------------------------------
    @FXML
    private TextField todo;

    @FXML
    private TextField subject;

    @FXML
    private TextField category;

    @FXML
    private Button ok;


    // todolist with timer ----------------------------------------------
    @FXML
    private JFXTreeTableView<TodoInfo> todoListTable;
    final ObservableList<TodoInfo> todoInfoList = FXCollections.observableArrayList();

    // information to access sql ------------------------------------------
    String msUrl = "jdbc:mysql://localhost:3306/studymode_db";
    String user = "root";
    String password = "";



    // [Methods]


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

    // task input field -------------------------------------------------
    @FXML
    void addNewTodo(ActionEvent event) {
        String subjectStr = subject.getText();
        String categoryStr = category.getText();
        String todoStr = todo.getText();

        if (!(subjectStr == "" && categoryStr == "" && todoStr == "")){
            try {
                Connection myConn = DriverManager.getConnection(msUrl, user, password);
                Statement myStmt = myConn.createStatement();
                String sql = "insert into todo_table (subject, category, task)"
                        + " values ('" + subjectStr + "', '" + categoryStr + "', '" + todoStr + "')";
                myStmt.executeUpdate(sql);
                URL viewUrl = getClass().getResource("View.fxml");
                initialize(viewUrl, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        subject.setText("");
        category.setText("");
        todo.setText("");
    }

    // todolist with timer ----------------------------------------------
    @Override
    public void initialize(URL url, ResourceBundle rb) {

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
                return param.getValue().getValue().timerSP;
            }
        });

        JFXTreeTableColumn<TodoInfo, String> switchCl = new JFXTreeTableColumn<>("Switch");
        switchCl.setPrefWidth(100);
//        switchCl.setCellValueFactory(
//                new PropertyValueFactory<TodoInfo, String>("switchButton")
//        );

        try {
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            ResultSet myRs = myStmt.executeQuery("select * from todo_table");
            while(myRs.next()) {
                String s = myRs.getString("subject");
                String c = myRs.getString("category");
                String t = myRs.getString("task");
                todoInfoList.add(new TodoInfo(s, c, t));
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
