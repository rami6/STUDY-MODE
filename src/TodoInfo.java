/* Created by Miho */

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

class TodoInfo extends RecursiveTreeObject<TodoInfo> {
    StringProperty subjectSP;
    StringProperty categorySP;
    StringProperty todoSP;
    AnimationTimer timer;
    StringProperty display;
    JFXButton switchButton;

    public TodoInfo(String subject, String category, String todo) {
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
            switchButton.setText("Stop");
        } else {
            timer.stop();
            switchButton.setText("Start");
        }
    }
}