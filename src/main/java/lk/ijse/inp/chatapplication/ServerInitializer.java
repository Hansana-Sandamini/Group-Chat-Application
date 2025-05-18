package lk.ijse.inp.chatapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Load server form
        Parent serverLoad = FXMLLoader.load(getClass().getResource("/view/ServerForm.fxml"));
        Scene serverScene = new Scene(serverLoad);
        stage.setScene(serverScene);
        stage.setTitle("Server");
        stage.show();

        // Load name input form
        loadNameInputForm();
    }

    public static void loadNameInputForm() throws Exception {
        Stage nameStage = new Stage();
        Parent nameLoad = FXMLLoader.load(ServerInitializer.class.getResource("/view/NameInputForm.fxml"));
        Scene nameScene = new Scene(nameLoad);
        nameStage.setScene(nameScene);
        nameStage.show();
    }
}