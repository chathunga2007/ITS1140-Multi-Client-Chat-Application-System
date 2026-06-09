package lk.ijse.multiclientauctionsystem;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClientHandler implements Initializable {
    @FXML
    private Label lblUserName;
    @FXML
    private Label lblItemName;
    @FXML
    private Label lblCurrentHighestBid;
    @FXML
    private TextArea txtArea;
    @FXML
    private TextField txtField;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String clientName = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        askForName();

        if (clientName.isEmpty()) {
            Platform.exit();
            return;
        }

        connectToServer();
    }

    private void askForName() {
        // client name fill dialog box
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Welcome Client Auction System");
        dialog.setHeaderText("Enter Your Name");
        dialog.setContentText("Name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            clientName = result.get().trim();
            lblUserName.setText(clientName);
        } else {
            clientName = "Anonymous";
        }
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                // new socket create
                socket = new Socket("127.0.0.1", 6000);
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());

                // output stream add client name
                out.writeUTF(clientName);
                out.flush();

                Platform.runLater(() -> {
                    // client connect to the server show messag text area
                    txtArea.appendText("Connected " +"\n");
                });

                while (true) {
                    String msg = in.readUTF();
                    Platform.runLater(() -> {
                        txtArea.appendText(msg + "\n");
                    });
                }

            } catch (IOException e) {
                // server error
                Platform.runLater(() -> {
                    txtArea.appendText("Server connection failed! Make sure server is running.\n");
                });
            }
        }).start();
    }

    @FXML
    private void btnPlaceBidAction() throws IOException {
        // check the text filed input
        if (out == null || txtField.getText().trim().isEmpty()) {
            return;
        }

        String message = txtField.getText().trim();
        out.writeUTF(message);
        out.flush();

        Platform.runLater(() -> {
            // show the text area input message
            txtArea.appendText("Me: " + message + "\n");
        });

        txtField.clear();
    }

    @FXML
    private void btnDisconnectOnAction() throws IOException {
        System.out.println("Client disconnected.");
        socket.close();
        System.exit(0);
    }
}