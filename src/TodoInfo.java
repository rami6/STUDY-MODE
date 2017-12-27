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
    StringProperty timerSP;
    JFXButton switchButton;

    public TodoInfo(String subject, String category, String todo) {
        this.subjectSP = new SimpleStringProperty(subject);
        this.categorySP = new SimpleStringProperty(category);
        this.todoSP = new SimpleStringProperty(todo);
        this.timerSP = new SimpleStringProperty("");
        this.switchButton = new JFXButton("Start");
    }
}