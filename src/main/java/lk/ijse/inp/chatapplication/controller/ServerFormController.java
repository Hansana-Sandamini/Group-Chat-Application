package lk.ijse.inp.chatapplication.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ServerFormController {

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

    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private boolean isRunning = true;

    public void initialize() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(3001);
                textAreaServer.appendText("Server started. Waiting for clients...\n");

                while (isRunning) {
                    Socket socket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(socket);
                    clients.add(clientHandler);
                    clientHandler.start();
                }
            } catch (IOException e) {
                if (isRunning) {
                    textAreaServer.appendText("Server error: " + e.getMessage() + "\n");
                }
            }
        }).start();
        textAreaServer.setEditable(false);
    }

    @FXML
    void btnFileOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File or Image");
        File file = fileChooser.showOpenDialog(serverPane.getScene().getWindow());

        if (file != null) {
            try {
                String fileName = file.getName();
                String fileType = fileName.toLowerCase().matches(".*\\.(png|jpg|jpeg|gif|avif|webp)$") ? "image" : "file";

                if (fileType.equals("image")) {
                    Image image = new Image(file.toURI().toString());
                    imageView.setImage(image);
                    byte[] imageBytes = Files.readAllBytes(file.toPath());
                    sendImageToAll(fileName, imageBytes);
                    sendMessageToAll("Server sent an image [" + fileName + "]");
                } else {
                    sendMessageToAll("Server sent a file [" + fileName + "]");
                }
            } catch (Exception e) {
                textAreaServer.appendText("Error processing file: " + e.getMessage() + "\n");
            }
        }
    }

    @FXML
    void btnSendOnAction(MouseEvent event) {
        sendMessageToAll("Server: " + txtServer.getText());
        txtServer.clear();
    }

    private void sendMessageToAll(String message) {
        textAreaServer.appendText(message + "\n");
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    private void sendImageToAll(String fileName, byte[] imageBytes) {
        for (ClientHandler client : clients) {
            client.sendImage(fileName, imageBytes);
        }
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;
        private String clientName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                this.clientName = dataInputStream.readUTF();
                sendMessageToAll(clientName + " joined the chat!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String message) {
            try {
                dataOutputStream.writeUTF("text:" + message);
                dataOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendImage(String fileName, byte[] imageBytes) {
            try {
                dataOutputStream.writeUTF("image:" + fileName);
                dataOutputStream.writeInt(imageBytes.length);
                dataOutputStream.write(imageBytes);
                dataOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String messageType = dataInputStream.readUTF();
                    if (messageType.equalsIgnoreCase("exit")) {
                        clients.remove(this);
                        sendMessageToAll(clientName + " left the chat!");
                        break;
                    } else if (messageType.startsWith("image:")) {
                        String fileName = messageType.substring(6);
                        int imageLength = dataInputStream.readInt();
                        byte[] imageBytes = new byte[imageLength];
                        dataInputStream.readFully(imageBytes);

                        // Update server's ImageView
                        Platform.runLater(() -> {
                            Image image = new Image(new ByteArrayInputStream(imageBytes));
                            imageView.setImage(image);
                        });

                        // Broadcast image to all clients
                        sendImageToAll(fileName, imageBytes);
                        sendMessageToAll(clientName + " sent an image [" + fileName + "]");
                    } else if (messageType.startsWith("file:")) {
                        String fileName = messageType.substring(5);
                        sendMessageToAll(clientName + " sent a file [" + fileName + "]");
                    } else if (messageType.startsWith("text:")) {
                        String message = messageType.substring(5);
                        sendMessageToAll(clientName + ": " + message);
                    }
                }
            } catch (IOException e) {
                clients.remove(this);
                textAreaServer.appendText(clientName + " disconnected unexpectedly!\n");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stopServer() {
        isRunning = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            for (ClientHandler client : clients) {
                client.sendMessage("Server is shutting down. Goodbye!");
                client.socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
