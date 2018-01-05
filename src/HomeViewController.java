/* Created by Miho */

        import com.jfoenix.controls.*;
        import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
        import com.jfoenix.controls.RecursiveTreeItem;
        import com.jfoenix.controls.JFXComboBox;
        import com.jfoenix.controls.JFXDatePicker;

        import javafx.animation.AnimationTimer;
        import javafx.beans.Observable;
        import javafx.beans.property.ReadOnlyObjectWrapper;
        import javafx.fxml.FXMLLoader;
        import javafx.scene.Parent;
        import javafx.scene.Scene;
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
        import javafx.scene.input.MouseEvent;
        import javafx.stage.Stage;
        import javafx.util.Callback;
        import jdk.jfr.Category;

        import java.io.File;
        import java.io.IOException;
        import java.net.URL;
        import java.sql.Connection;
        import java.sql.DriverManager;
        import java.sql.ResultSet;
        import java.sql.Statement;
        import java.text.SimpleDateFormat;
        import java.time.LocalDate;
        import java.util.Date;
        import java.util.Optional;
        import java.util.ResourceBundle;

public class HomeViewController implements Initializable {

    //[Declarations]
    long dailyStudyTime;

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
    private JFXComboBox<String> subject;

    @FXML
    private JFXComboBox<String> category;

    @FXML
    private Button ok;

    @FXML
    private JFXTextField newSubject;

    // todolist with timer ----------------------------------------------
    @FXML
    private JFXTreeTableView<TodoInfo> todoListTable;

    @FXML
    private Button editBtn;

    @FXML
    private Button refreshTableBtn;

    // spent time chart ----------------------------------------------
    @FXML
    private BarChart<Number, String> timeBarChart1;

    @FXML
    private BarChart<Number, String> timeBarChart2;

    @FXML
    private JFXComboBox<String> subjectSelector;


    @FXML
    PieChart timePieChart;

    // date pick ----------------------------------------------

    @FXML
    private JFXDatePicker datePicker;

    // daily study time ----------------------------------------------
    private static AnimationTimer dailyTimer;

    @FXML
    private JFXTextField dailyTotalTime;

    @FXML
    private JFXTextField selectedDate;

    // Note ------------------------------------------------------------
    @FXML
    private JFXListView<String> noteList;

    @FXML
    private JFXTextArea noteEditArea;

    @FXML
    private Button noteSaveBtn;

    @FXML
    private Button newNoteBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private Button editTitleBtn;

    // record screen video ----------------------------------------------
    @FXML
    private Button recordBtn;

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

        setInputSubjectOption();
        setTableView();

        setDefaultDate();

        showDailyStudyTime();

        setTimeBarChartBySubject();
        setChartSubjectOption();
        setInitialBarchart2();

        setNoteListView();

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
                    targetHour.setText(hourStr);
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
        showDailyStudyTime();
    }

    void setDefaultDate() {
        datePicker.setValue(LocalDate.now());
    }

    LocalDate getPickedDate() {
        return datePicker.getValue();
    }

    // daily time watch  ------------------------------------------------------

    void showDailyStudyTime() {
        dailyStudyTime = 0;
        try {
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            ResultSet myRs = myStmt.executeQuery("select * from daily_studytime_table where study_date = '" + datePicker.getValue() + "'");
            //datePicker.getValue()
            while(myRs.next()) {
                dailyStudyTime = myRs.getLong("study_time");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        setDailyTimePieChart(dailyStudyTime);
        setInitialTime(dailyStudyTime / 1000);
        setDailyTimer(dailyStudyTime / 1000);
    }

    void setInitialTime(long actualStudySec) {
        long hours = actualStudySec / (60 * 60);
        String hStr = Long.toString(hours);
        if (hours < 100) {
            hStr = String.format("%02d", hours);
        } else if (hours < 1000) {
            hStr = String.format("%03d", hours);
        } else if (hours < 10000) {
            hStr = String.format("%04d", hours);
        } else {
            hStr = "OVER";
        }
        dailyTotalTime.setText(hStr + ":" + new SimpleDateFormat("mm").format(new Date(actualStudySec * 1000)));
    }

    void setDailyTimer(long actualStudySec) {
        dailyTimer = new AnimationTimer() {
            private long timestamp;
            private long time = actualStudySec;
            private long fraction = 0;

            @Override
            public void start() {
                // current time adjusted by remaining time from last run
                timestamp = System.currentTimeMillis() - fraction;
                super.start();
            }

            @Override
            public void stop() {
                super.stop();
                // save leftover time not handled with the last update
                fraction = System.currentTimeMillis() - timestamp;
            }

            @Override
            public void handle(long now) {
                long newTime = System.currentTimeMillis();
                if (timestamp + 1000 <= newTime) {
                    long deltaT = (newTime - timestamp) / 1000;
                    time += deltaT;
                    timestamp += 1000 * deltaT;
                    long hours = time / (60 * 60);
                    String hStr = Long.toString(hours);
                    if (hours < 100) {
                        hStr = String.format("%02d", hours);
                    } else if (hours < 1000) {
                        hStr = String.format("%03d", hours);
                    } else if (hours < 10000) {
                        hStr = String.format("%04d", hours);
                    } else {
                        hStr = "OVER";
                    }
                    dailyTotalTime.setText(hStr + ":" + new SimpleDateFormat("mm").format(new Date(time * 1000)));
                }
            }
        };
    }
    public static void startDailyTimer() {
        dailyTimer.start();
    }

    public static void stopDailyTimer() {
        dailyTimer.stop();
    }


    // daily time pie chart-----------------------------------------------------

    void setDailyTimePieChart(long actualStudyTime) {
        int targetStudyTime = 0;
        double actualStudyTimeHour = 0;
        try {
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            ResultSet myRs = myStmt.executeQuery("select * from user_profile_table where user_id = 1");
            while(myRs.next()) {
                targetStudyTime = myRs.getInt("target_hour");
            }
            actualStudyTimeHour = (double)Math.round(actualStudyTime / 1000.0 / 60 / 60 * 100) / 100;
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

    void setTimeBarChartBySubject() {
        XYChart.Series set = new XYChart.Series<>();
        timeBarChart1.setLegendVisible(false);

        try {
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            ResultSet myRs = myStmt.executeQuery("select subject, sum(total_spent_time) as 'sum_time' from todo_table group by subject");
            while(myRs.next()) {
                double sumTimeHour = myRs.getLong("sum_time") / 1000.0 / 60 / 60;
                sumTimeHour = (double)Math.round(sumTimeHour * 100)/100;
                String subjectName = myRs.getString("subject");
                set.getData().addAll(new XYChart.Data(sumTimeHour, subjectName));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        timeBarChart1.getData().addAll(set);
    }

    void setChartSubjectOption() {

        try {
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            ResultSet myRs = myStmt.executeQuery("select * from subject_table");
            while(myRs.next()) {
                subjectSelector.getItems().add(myRs.getString("subject_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void selectorAction(ActionEvent event) {
        timeBarChart2.getData().clear();

        XYChart.Series set = new XYChart.Series<>();
        timeBarChart2.setLegendVisible(false);
        String selectedSubject = subjectSelector.getValue();

        try {
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            ResultSet myRs = myStmt.executeQuery("select category, sum(total_spent_time) as 'sum_time' from todo_table where subject = '" + selectedSubject + "' group by category");
            while(myRs.next()) {
                double sumTimeHour = myRs.getLong("sum_time") / 1000.0 / 60 / 60;
                sumTimeHour = (double)Math.round(sumTimeHour * 100)/100;
                String categoryName = myRs.getString("category");
                set.getData().add(new XYChart.Data(sumTimeHour, categoryName));
                System.out.println(new XYChart.Data(sumTimeHour, categoryName));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        timeBarChart2.getData().addAll(set);
    }

    void setInitialBarchart2() {
        timeBarChart2.getData().clear();

        XYChart.Series set = new XYChart.Series<>();
        timeBarChart2.setLegendVisible(false);
        String selectedSubject = subjectSelector.getValue();

        subjectSelector.setValue("Programming");

        try {
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            ResultSet myRs = myStmt.executeQuery("select category, sum(total_spent_time) as 'sum_time' from todo_table where subject = 'Programming' group by category");
            while(myRs.next()) {
                double sumTimeHour = myRs.getLong("sum_time") / 1000.0 / 60 / 60;
                sumTimeHour = (double)Math.round(sumTimeHour * 100)/100;
                String categoryName = myRs.getString("category");
                set.getData().add(new XYChart.Data(sumTimeHour, categoryName));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        timeBarChart2.getData().addAll(set);
    }

    // todothing input field -------------------------------------------------


    void setInputSubjectOption() {
        subject.getItems().clear();
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

        if (subjectStr != null && categoryStr != null && todoStr != null && !subjectStr.trim().isEmpty() && !categoryStr.trim().isEmpty() && !todoStr.trim().isEmpty()){
            try {
                Connection myConn = DriverManager.getConnection(msUrl, user, password);
                Statement myStmt = myConn.createStatement();
                String sql = "insert into todo_table (subject, category, todo)"
                        + " values ('" + subjectStr + "', '" + categoryStr + "', '" + todoStr + "')";
                myStmt.executeUpdate(sql);
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
        setInputSubjectOption();
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
        ObservableList<TodoInfo> todoInfoList = FXCollections.observableArrayList();

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
                long tsTime = myRs.getLong("total_spent_time");
                boolean isDone = myRs.getBoolean("isDone");
                boolean isVisible = myRs.getBoolean("isVisible");
                if (!isDone && isVisible) {
                    todoInfoList.add(new TodoInfo(i, s, c, t, tsTime, isDone, isVisible));
                }
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

    @FXML
    void openEditWindow(ActionEvent event) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("TodoEditView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Edit Todo");
            stage.setScene(new Scene(root, 655, 700));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void refreshTable(ActionEvent event) {
        setTableView();
    }

    // note -------------------------------------------------------------
    @FXML
    void createNewNote(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setContentText("Enter note title:");
        dialog.setTitle("Create new note");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newTitle -> addNewNote(newTitle));

        try {
            String updatedNote = noteEditArea.getText();
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "update note_table set note = '" + updatedNote + "' where title = '" + noteList.getSelectionModel().getSelectedItem() + "'";
            myStmt.executeUpdate(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void addNewNote(String newTitle) {
        try {
            String title = newTitle;
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "insert into note_table (title, note)"
                    + " values ('" + title + "', " + "\"\")";
            myStmt.executeUpdate(sql);
            setNoteListView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void saveNote(ActionEvent event) {
        try {
            String updatedNote = noteEditArea.getText();
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "update note_table set note = '" + updatedNote + "' where title = '" + noteList.getSelectionModel().getSelectedItem() + "'";
            myStmt.executeUpdate(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int editingNoteId;
    @FXML
    void showNoteContent(MouseEvent event) {
        String title = noteList.getSelectionModel().getSelectedItem();
        try {
        Connection myConn = DriverManager.getConnection(msUrl, user, password);
        Statement myStmt = myConn.createStatement();
        ResultSet myRs = myStmt.executeQuery("select * from note_table where title = '" + title + "'");
        while (myRs.next()) {
            String noteContent = myRs.getString("note");
            editingNoteId = myRs.getInt("note_id");
            noteEditArea.setText(noteContent);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    @FXML
    void editTitle(ActionEvent event) {
        String selectedTitle = noteList.getSelectionModel().getSelectedItem();
        int temp = -1;
        try {
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            ResultSet myRs = myStmt.executeQuery("select * from note_table where title = '" + selectedTitle + "'");
            while (myRs.next()) {
                temp = myRs.getInt("note_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        final int selectedId = temp;

        TextInputDialog dialog = new TextInputDialog(selectedTitle);
        dialog.setContentText("Edit note title:");
        dialog.setTitle("Edit note title");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(updatedTitle -> updateNoteTitle(updatedTitle, selectedId));

    }

    void updateNoteTitle(String updatedTitle, int selectedId) {
        try {
            String updatedNote = noteEditArea.getText();
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "update note_table set title = '" + updatedTitle + "' where note_id = '" + selectedId + "'";
            myStmt.executeUpdate(sql);
            setNoteListView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setNoteListView() {
        noteList.getItems().clear();
        try {
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            ResultSet myRs = myStmt.executeQuery("select * from note_table");
            while (myRs.next()) {
                String title = myRs.getString("title");
                int isVisible = myRs.getInt("isVisible");
                if (isVisible == 1) {
                    noteList.getItems().add(title);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void deleteNote() {

        String selectedTitle = noteList.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you OK to delete \"" + selectedTitle + "\"?");
        alert.setGraphic(null);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            try {
                Connection myConn = DriverManager.getConnection(msUrl, user, password);
                Statement myStmt = myConn.createStatement();
                String sql = "update note_table set isVisible = '0' where title = '" + noteList.getSelectionModel().getSelectedItem() + "'";
                myStmt.executeUpdate(sql);
                setNoteListView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void updateTitle() {
        System.out.println("updateTitle");
    }



    // record screen video ----------------------------------------------

    @FXML
    void startStopRecord(ActionEvent event) {
        if(recordBtn.getText().equals("●")) {
            recordBtn.setText("■");
            // let QuickTime start recording
            try {
                File file = new File("src/resources/ScreenRecord.scpt");
                String filePath = file.getAbsolutePath();
                String command = "osascript " + filePath;
                Runtime.getRuntime().exec(command);
            } catch(Exception e) {
                e.printStackTrace();
            }

        } else {
            recordBtn.setText("●");
            // let QuickTime stop recording
            try {
                File file = new File("src/resources/Stoprecording.scpt");
                String filePath = file.getAbsolutePath();
                String command = "osascript " + filePath;
                Runtime.getRuntime().exec(command);
            } catch(Exception e) {
                e.printStackTrace();
            }

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
