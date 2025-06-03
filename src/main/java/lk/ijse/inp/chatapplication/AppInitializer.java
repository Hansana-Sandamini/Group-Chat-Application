package lk.ijse.inp.chatapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.ijse.inp.chatapplication.controller.ServerFormController;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ServerForm.fxml"));
        Parent serverLoad = loader.load();

        // Get the controller to properly shut down the server
        ServerFormController serverController = loader.getController();

        Scene serverScene = new Scene(serverLoad);
        stage.setScene(serverScene);
        stage.setTitle("Server");
        stage.setOnCloseRequest(e -> {
            serverController.stopServer();
            System.exit(0);
        });
        stage.show();

        loadNameInputForm();
    }

    public static void loadNameInputForm() throws Exception {
        Stage nameStage = new Stage();
        Parent nameLoad = FXMLLoader.load(AppInitializer.class.getResource("/view/NameInputForm.fxml"));
        Scene nameScene = new Scene(nameLoad);
        nameStage.setScene(nameScene);
        nameStage.setTitle("Enter Client Name");
        nameStage.show();
    }
}