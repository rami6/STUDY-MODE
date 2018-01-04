/* Created by Miho */

        import com.jfoenix.controls.*;
        import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
        import com.jfoenix.controls.RecursiveTreeItem;
        import com.jfoenix.controls.JFXComboBox;
        import com.jfoenix.controls.JFXDatePicker;

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

        import java.net.URL;
        import java.sql.Connection;
        import java.sql.DriverManager;
        import java.sql.ResultSet;
        import java.sql.Statement;
        import java.util.Optional;
        import java.util.ResourceBundle;

public class HomeViewController implements Initializable {

    //[Declarations]

    // user name -----------------------------------------------------
    @FXML
    private JFXTextField userName;

    // target study time -----------------------------------------------------
    @FXML
    private JFXTextField targetHour;

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

    // spent time chart ----------------------------------------------
    @FXML
    private BarChart<?, ?> timeBarChart1;

    @FXML
    private BarChart<?, ?> timeBarChart2;

    @FXML
    private JFXComboBox<String> subjectSelector1;

    @FXML
    private JFXComboBox<String> subjectSelector2;

    @FXML
    private CategoryAxis x;

    @FXML
    private NumberAxis y;

    @FXML
    PieChart timePieChart;

    // date pick ----------------------------------------------

    @FXML
    private JFXDatePicker datePicker;

    // daily study time ----------------------------------------------

    @FXML
    private JFXTextField dailyTotalTime;

    @FXML
    private JFXTextField selectedDate;

    // note ------------------------------------------------------------
    @FXML
    private JFXListView noteList;

    @FXML
    private JFXTextArea noteEditArea;

    @FXML
    private JFXButton noteSaveBtn;

    @FXML
    private JFXButton newNoteBtn;

    // record screen video ----------------------------------------------
    @FXML
    private JFXButton recordBtn;

    @FXML
    private JFXButton watchBtn;

    // information to access sql ------------------------------------------
    String msUrl = "jdbc:mysql://localhost:3306/studymode_db";
    String user = "root";
    String password = "";



    // [Methods]

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        showUserName();
        showTargetHour();

        setSubjectOption(subject);
        setTableView();

        showDailyStudyTime();

        setSubjectOption(subjectSelector1);
        setSubjectOption(subjectSelector2);
    }

    // user name -----------------------------------------------------
    @FXML
    void userNameAction(ActionEvent event) {
        String userNameStr = userName.getText();
        try {
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "update user_profile_table SET user_name ='" + userNameStr + "' WHERE user_id = 1";
            myStmt.executeUpdate(sql);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    void showUserName() {
        try {
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            ResultSet myRs = myStmt.executeQuery("select * from user_profile_table where user_id = 1");
            while(myRs.next()) {
                userName.setText(myRs.getString("user_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // target study time -----------------------------------------------------

    @FXML
    void targetHourAction(ActionEvent event) {
        String targetHpurStr = targetHour.getText();
        try {
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "update user_profile_table SET target_hour ='" + targetHpurStr + "' WHERE user_id = 1";
            myStmt.executeUpdate(sql);
            showTargetHour();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    void showTargetHour() {
        try {
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            ResultSet myRs = myStmt.executeQuery("select * from user_profile_table where user_id = 1");
            while(myRs.next()) {
                String hourStr = myRs.getString("target_hour");
                if (hourStr.equals("0")) {
                    targetHour.setPromptText("Target study hour");
                } else {
                    targetHour.setText("Study " + hourStr + " hours/day!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    // date pick ------------------------------------------------------
    @FXML
    void datePickAction(ActionEvent event) {

    }

    // daily time watch  ------------------------------------------------------
    double actualStudyTime = 0;
    void showDailyStudyTime() {
        try {
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            ResultSet myRs = myStmt.executeQuery("select * from daily_studytime_table where study_date = '2018-01-03'");
            //datePicker.getValue()
            while(myRs.next()) {
                actualStudyTime = myRs.getDouble("study_time");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        // Animation timer
        String text;
        //dailyTotalTime.setText(text);
        setDailyTimePieChart(actualStudyTime);
    }


    // daily time pie chart-----------------------------------------------------

    void setDailyTimePieChart(double actualStudyTime) {
        int targetStudyTime = 0;
        double actualStudyTimeHour = 0;
        try {
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            ResultSet myRs = myStmt.executeQuery("select * from user_profile_table where user_id = 1");
            while(myRs.next()) {
                targetStudyTime = myRs.getInt("target_hour");
            }
            actualStudyTimeHour = actualStudyTime / 1000 / 60 / 60;
        }catch (Exception e) {
            e.printStackTrace();
        }
        double gap = targetStudyTime - actualStudyTimeHour;

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Actual", actualStudyTimeHour),
                new PieChart.Data("Gap", gap));
        timePieChart.setData(pieChartData);
        //remove labels
        timePieChart.setStyle("-fx-pie-label-visible: false;");
    }

    // time bar chart -----------------------------------------------------

    void setTimeBarChart(BarChart barChart, JFXComboBox<String> subjectSelector) {

        XYChart.Series set = new XYChart.Series<>();
        barChart.setLegendVisible(false);
        String selectedSubject = subjectSelector.getValue();

        try {
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            ResultSet myRs = myStmt.executeQuery("select category, sum(total_spent_time) as 'sum_time' from todo_table where subject = '" + selectedSubject + "' group by category");
            while(myRs.next()) {
                double sumTimeHour = myRs.getLong("sum_time") / 1000.0 / 60 / 60;
                String legend = myRs.getString("category");
                set.getData().add(new XYChart.Data(sumTimeHour, legend));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        barChart.getData().addAll(set);
    }

    @FXML
    void selector1Action(ActionEvent event) {
        setTimeBarChart(timeBarChart1, subjectSelector1);
    }

    @FXML
    void selector2Action(ActionEvent event) {
        setTimeBarChart(timeBarChart2, subjectSelector2);
    }

    // todothing input field -------------------------------------------------


    void setSubjectOption(JFXComboBox subjectCombo) {
        try {
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            ResultSet myRs = myStmt.executeQuery("select * from subject_table");
            while(myRs.next()) {
                subjectCombo.getItems().add(myRs.getString("subject_name"));
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
        setSubjectOption(subject);
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

    // note -------------------------------------------------------------
    @FXML
    void createNewNote(ActionEvent event) {

    }

    @FXML
    void saveNote(ActionEvent event) {

    }

    // record screen video ----------------------------------------------

    @FXML
    void startStopRecord(ActionEvent event) {
        if(recordBtn.getText().equals("Record")) {
            recordBtn.setText("Stop");
            // let QuickTime start recording

        } else {
            recordBtn.setText("Record");
            // let QuickTime stop recording

        }
    }
    @FXML
    void openVideoFolder(ActionEvent event) {
        // get path to default folder that QuickTime store screen video

        // open the folder using command

    }

    // for reference. This method will be deleted at last ----------
    void runCommand() {
        try {
            String command = "some command";
            Runtime.getRuntime().exec(command);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    // -------------------------------------------------------------

}
