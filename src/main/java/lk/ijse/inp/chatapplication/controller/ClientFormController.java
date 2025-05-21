package lk.ijse.inp.chatapplication.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientFormController implements Initializable {

    @FXML
    private Button btnFile;

    @FXML
    private Button btnSend;

    @FXML
    private AnchorPane clientPane;

    @FXML
    private ImageView imageView;

    @FXML
    private Label lblName;

    @FXML
    private TextArea textAreaClient;

    @FXML
    private TextField txtClient;

    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    String message;

    public void setClientName(String name) {
        lblName.setText(name);
    }

    @FXML
    void btnFileOnAction(ActionEvent event) {

    }

    @FXML
    void btnSendOnAction(MouseEvent event) {
        sendClient();
    }

    private void sendClient() {
        try {
            String clientMessage = txtClient.getText();
            dataOutputStream.writeUTF(clientMessage);
            textAreaClient.appendText("Client : " + clientMessage + "\n");
            txtClient.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(() -> {
            try {
                socket = new Socket("localhost", 4000);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    message = dataInputStream.readUTF();

                    if (message.equals("IMAGE")) {
                        int length = dataInputStream.readInt();
                        byte[] imageBytes = new byte[length];
                        dataInputStream.readFully(imageBytes);
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
                        Image image = new Image(byteArrayInputStream);
                        imageView.setImage(image);
                    }

                    textAreaClient.appendText("Server : " + message + "\n");

                    if (message.equalsIgnoreCase("exit")) {
                        break;
                    }
                }
                socket.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
        textAreaClient.setEditable(false);
    }
}
