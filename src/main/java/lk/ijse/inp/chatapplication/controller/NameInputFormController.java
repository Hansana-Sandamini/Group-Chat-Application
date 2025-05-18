package lk.ijse.inp.chatapplication.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lk.ijse.inp.chatapplication.ServerInitializer;

public class NameInputFormController {

    @FXML
    private Button btnSubmit;

    @FXML
    private TextField txtName;

    @FXML
    void btnSubmitOnAction(ActionEvent event) throws Exception {
        String name = txtName.getText().trim();
        if (!name.isEmpty()) {
            // Load client form with the entered name
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ClientForm.fxml"));
            Parent root = loader.load();

            // Get the controller and set the name
            ClientFormController clientController = loader.getController();
            clientController.setClientName(name);

            Stage clientStage = new Stage();
            clientStage.setScene(new Scene(root));
            clientStage.setTitle("Client: " + name);
            clientStage.show();

            // Close the name input form
            Stage currentStage = (Stage) btnSubmit.getScene().getWindow();
            currentStage.close();

            // Open a new name input form for another client
            ServerInitializer.loadNameInputForm();
        }
    }

}
