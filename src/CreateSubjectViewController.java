/* Created by Miho */

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreateSubjectViewController {

    @FXML
    private JFXTextField newSubject;

    // information to access sql ------------------------------------------
    String msUrl = "jdbc:mysql://localhost:3306/studymode_db";
    String user = "root";
    String password = "";

    @FXML
    void addNewSubject(ActionEvent event) {
        String newSbjStr = newSubject.getText();

        try {
            Connection myConn = DriverManager.getConnection(msUrl, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "insert into subject_table (subject_name)"
                    + " values ('" + newSbjStr + "')";
            myStmt.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
