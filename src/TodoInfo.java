/* Created by Miho */

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

class TodoInfo extends RecursiveTreeObject<TodoInfo> {
    int todoId;
    String subject;
    String category;
    String todo;
    boolean isDone;
    boolean isVisible;
    StringProperty subjectSP;
    StringProperty categorySP;
    StringProperty todoSP;
    AnimationTimer timer;
    StringProperty display;
    JFXButton switchButton;
    JFXCheckBox doneCheck;
    JFXCheckBox deleteCheck;

    java.sql.Timestamp startTime;
    java.sql.Timestamp stopTime;
    long spentTime;

    // information to access sql ------------------------------------------
    String msUrl = "jdbc:mysql://localhost:3306/studymode_db";
    String user = "root";
    String password = "";
    //---------------------------------------------------------------------

    public TodoInfo(int todoId, String subject, String category, String todo, Long totalSpentTime, boolean isDone, boolean isVisible) {
        this.todoId = todoId;
        this.subject = subject;
        this.category = category;
        this.todo = todo;
        this.isDone = isDone;
        this.isVisible = isVisible;
        this.subjectSP = new SimpleStringProperty(subject);
        this.categorySP = new SimpleStringProperty(category);
        this.todoSP = new SimpleStringProperty(todo);

        // calculate initial timer value
        long totalSpentSec = totalSpentTime / 1000;
        long hours = totalSpentSec / (60 * 60);
        String hStr = Long.toString(hours);
        if(hours < 100) {
            hStr = String.format("%02d", hours);
        } else if (hours < 1000) {
            hStr = String.format("%03d", hours);
        } else if (hours < 10000) {
            hStr = String.format("%04d", hours);
        } else {
            hStr = "OVER";
        }

        this.display = new SimpleStringProperty(hStr + ":" + new SimpleDateFormat("mm:ss").format(new Date(totalSpentTime)));
        this.timer = getTimer(totalSpentSec);
        this.switchButton = new JFXButton("Start");
        switchButton.setOnAction(e -> toggleStartStop(switchButton));
        this.doneCheck = new JFXCheckBox();
        if (isDone) {
            doneCheck.setSelected(true);
        }
        doneCheck.setOnAction(e -> toggleIsDone());
        this.deleteCheck = new JFXCheckBox();
        deleteCheck.setOnAction(e -> deleteTodo());
    }

    public AnimationTimer getTimer(Long totalSpentSec) {

        AnimationTimer timer = new AnimationTimer() {
            private long timestamp;
            private long time = totalSpentSec;
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
                    if(hours < 100) {
                        hStr = String.format("%02d", hours);
                    } else if (hours < 1000) {
                        hStr = String.format("%03d", hours);
                    } else if (hours < 10000) {
                        hStr = String.format("%04d", hours);
                    } else {
                        hStr = "OVER";
                    }
                    display.set(hStr + ":" + new SimpleDateFormat("mm:ss").format(new Date(time * 1000)));
                }
            }
        };

        return timer;
    }

    void toggleStartStop(JFXButton switchButton) {

        if(switchButton.getText().equals("Start")) {
            timer.start();
            Date date = new Date();
            startTime = new java.sql.Timestamp(date.getTime());
            switchButton.setText("Stop");
        } else {
            timer.stop();
            Date date = new Date();
            stopTime = new java.sql.Timestamp(date.getTime());
            switchButton.setText("Start");
            spentTime = stopTime.getTime() - startTime.getTime();

            try {
                Connection myConn = DriverManager.getConnection(msUrl, user, password);
                Statement myStmt = myConn.createStatement();
                String sql = "insert into time_tracking_table (start_time, stop_time, spent_time, todo_id, subject, category, todo)"
                        + " values ('" + startTime + "', '" + stopTime + "', '" + spentTime + "', '" + todoId + "', '" + subject + "', '" + category + "', '" + todo + "')";
                myStmt.executeUpdate(sql);

                myStmt = myConn.createStatement();
                sql = "update todo_table " +
                        "set total_spent_time = total_spent_time + " + spentTime +
                        " where todo_id = " + todoId;
                myStmt.executeUpdate(sql);

                // add spent time to daily_studytime_table
                myStmt = myConn.createStatement();
                String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

                sql = "insert ignore into daily_studytime_table (study_date, study_time)" +
                        "values ('" + todayDate + "', '0')";
                myStmt.executeUpdate(sql);

                myStmt = myConn.createStatement();
                sql = "update daily_studytime_table " +
                        "set study_time = study_time + " + spentTime +
                        " where study_date = '" + todayDate + "'";
                myStmt.executeUpdate(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void toggleIsDone() {

        try {
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            if (doneCheck.isSelected()) {
                String sql = "update todo_table SET isDone = '1' WHERE todo_id = " + todoId;
                myStmt.executeUpdate(sql);
            } else {
                String sql = "update todo_table SET isDone = '0' WHERE todo_id = " + todoId;
                myStmt.executeUpdate(sql);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteTodo() {
        if(deleteCheck.isSelected()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you OK to delete \"" + todo + "\"?");
            alert.setGraphic(null);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                try {
                    Connection myConn = DriverManager.getConnection(msUrl, user, password);
                    Statement myStmt = myConn.createStatement();
                    String sql = "update todo_table SET isVisible = '0' WHERE todo_id = " + todoId;
                    myStmt.execute(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                Connection myConn = DriverManager.getConnection(msUrl, user, password);
                Statement myStmt = myConn.createStatement();
                String sql = "update todo_table SET isVisible = '1' WHERE todo_id = " + todoId;
                myStmt.execute(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return subject + ", " + category + ", " + todo;
    }

    public int getTodoId() {
        return todoId;
    }

    public boolean isDone() {
        return isDone;
    }
}