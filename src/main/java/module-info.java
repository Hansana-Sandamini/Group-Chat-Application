module lk.ijse.hibernate.serenitymentalhealththerapycenter.chatapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens lk.ijse.hibernate.serenitymentalhealththerapycenter.chatapp to javafx.fxml;
    exports lk.ijse.hibernate.serenitymentalhealththerapycenter.chatapp;
}