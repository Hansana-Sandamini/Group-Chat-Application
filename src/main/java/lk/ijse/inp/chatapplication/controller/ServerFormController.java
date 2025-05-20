package lk.ijse.inp.chatapplication.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ServerFormController implements Initializable {

    @FXML
    private Button btnFile;

    @FXML
    private Button btnSend;

    @FXML
    private ImageView imageView;

    @FXML
    private AnchorPane serverPane;

    @FXML
    private TextArea textAreaServer;

    @FXML
    private TextField txtServer;

    ServerSocket serverSocket;
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    String message;

    @FXML
    void btnFileOnAction(ActionEvent event) {

    }

    @FXML
    void btnSendOnAction(MouseEvent event) {
        sendServer();
    }

    private void sendServer() {
        try {
            String serverMessage = txtServer.getText();
            dataOutputStream.writeUTF(serverMessage);
            textAreaServer.appendText("Server : " + serverMessage + "\n");
            txtServer.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(4000);
                socket = serverSocket.accept();
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

                    if (message.equalsIgnoreCase("exit")) {
                        break;
                    }
                }
                socket.close();
                serverSocket.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
        textAreaServer.setEditable(false);
    }
}
