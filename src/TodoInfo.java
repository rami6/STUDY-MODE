/* Created by Miho */

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

class TodoInfo extends RecursiveTreeObject<TodoInfo> {
    int todoId;
    String subject;
    String category;
    String todo;
    StringProperty subjectSP;
    StringProperty categorySP;
    StringProperty todoSP;
    AnimationTimer timer;
    StringProperty display;
    JFXButton switchButton;

    java.sql.Timestamp startTime;
    java.sql.Timestamp stopTime;
    long spentTime;

    // information to access sql ------------------------------------------
    String msUrl = "jdbc:mysql://localhost:3306/studymode_db";
    String user = "root";
    String password = "";
    //---------------------------------------------------------------------

    public TodoInfo(int todoId, String subject, String category, String todo) {
        this.todoId = todoId;
        this.subject = subject;
        this.category = category;
        this.todo = todo;
        this.subjectSP = new SimpleStringProperty(subject);
        this.categorySP = new SimpleStringProperty(category);
        this.todoSP = new SimpleStringProperty(todo);
        this.display = new SimpleStringProperty();
        this.timer = getTimer();
        this.switchButton = new JFXButton("Start");
        switchButton.setOnAction(e -> toggleStartStop(switchButton));

    }

    public AnimationTimer getTimer() {

        AnimationTimer timer = new AnimationTimer() {
            private long timestamp;
            private long time = 0;
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
                    display.set(Long.toString(time));
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}