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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ClientForm.fxml"));
            Parent root = loader.load();

            ClientFormController clientController = loader.getController();
            clientController.setClientName(name);

            Stage clientStage = new Stage();
            clientStage.setScene(new Scene(root));
            clientStage.setTitle("Client: " + name);
            clientStage.setOnCloseRequest(e -> clientController.disconnect());
            clientStage.show();

            Stage currentStage = (Stage) btnSubmit.getScene().getWindow();
            currentStage.close();

            ServerInitializer.loadNameInputForm();
        }
    }
}
