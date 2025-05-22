package lk.ijse.inp.chatapplication.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import javafx.application.Platform;

public class ClientFormController {

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

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String clientName;

    public void setClientName(String name) {
        this.clientName = name;
        lblName.setText(name);
        connectToServer();
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                socket = new Socket("localhost", 3001);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                dataOutputStream.writeUTF(clientName);
                dataOutputStream.flush();

                while (true) {
                    String messageType = dataInputStream.readUTF();
                    if (messageType.startsWith("text:")) {
                        String message = messageType.substring(5);
                        Platform.runLater(() -> textAreaClient.appendText(message + "\n"));
                    } else if (messageType.startsWith("image:")) {
                        String fileName = messageType.substring(6);
                        int imageLength = dataInputStream.readInt();
                        byte[] imageBytes = new byte[imageLength];
                        dataInputStream.readFully(imageBytes);

                        Platform.runLater(() -> {
                            Image image = new Image(new ByteArrayInputStream(imageBytes));
                            imageView.setImage(image);
                        });
                    }
                }
            } catch (IOException e) {
                Platform.runLater(() -> textAreaClient.appendText("Connection to server lost.\n"));
            }
        }).start();
        textAreaClient.setEditable(false);
    }

    @FXML
    void btnFileOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File or Image");
        File file = fileChooser.showOpenDialog(clientPane.getScene().getWindow());

        if (file != null) {
            try {
                String fileName = file.getName();
                if (fileName.toLowerCase().matches(".*\\.(png|jpg|jpeg|gif|bmp)$")) {
                    Image image = new Image(file.toURI().toString());
                    imageView.setImage(image);
                    byte[] imageBytes = Files.readAllBytes(file.toPath());
                    dataOutputStream.writeUTF("image:" + fileName);
                    dataOutputStream.writeInt(imageBytes.length);
                    dataOutputStream.write(imageBytes);
                    dataOutputStream.flush();
                } else {
                    dataOutputStream.writeUTF("file:" + fileName);
                    dataOutputStream.flush();
                }
            } catch (Exception e) {
                textAreaClient.appendText("Error sending file: " + e.getMessage() + "\n");
            }
        }
    }

    @FXML
    void btnSendOnAction(MouseEvent event) {
        try {
            String message = txtClient.getText();
            if (!message.isEmpty()) {
                dataOutputStream.writeUTF("text:" + message);
                dataOutputStream.flush();
                txtClient.clear();
            }
        } catch (IOException e) {
            textAreaClient.appendText("Failed to send message.\n");
        }
    }

    public void disconnect() {
        try {
            if (socket != null) {
                dataOutputStream.writeUTF("exit");
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
