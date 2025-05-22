module lk.ijse.inp.chatapplication {
    requires javafx.controls;
    requires javafx.fxml;


    opens lk.ijse.inp.chatapplication.controller to javafx.fxml;
    exports lk.ijse.inp.chatapplication;
}