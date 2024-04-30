package lk.ijse.dep12.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import lk.ijse.dep12.shared.to.Media;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class MainViewController {
    public TextFlow txtDesplay;
    public TextField txtMesage;
    public Button btnSend;
    public Button btnImgChoose;
    public Label lblImageFile;

    private String nickName;
    private Socket remoteSocket;
    private ObjectOutputStream oos;


    public void initData(Socket remoteSocket, String nickName) {
        this.nickName = nickName;
        this.remoteSocket = remoteSocket;
        appendText("You : Entered into chat room \n");
        try {
            oos = new ObjectOutputStream(remoteSocket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        new Thread(() -> {
            try {

                try (InputStream is = remoteSocket.getInputStream();
                    ObjectInputStream ois = new ObjectInputStream(is)){

                    while (true) {

                        Media media = (Media) ois.readObject();
                        if (media.isImage()) {
                            showImage(media.getImageFile());
                        }

                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                if (!remoteSocket.isClosed())
                    throw new RuntimeException(e);
            }
        }).start();
    }


    public void appendText(String message) {
        Text text = new Text(message);
        Platform.runLater(() -> txtDesplay.getChildren().add(text));
    }

    public void btnSendOnAction(ActionEvent actionEvent) throws IOException {

        String message = nickName + " : " + txtMesage.getText().strip() + "\n";
        System.out.println(message.getBytes());
        remoteSocket.getOutputStream().write(message.getBytes());
        Text text = new Text(message);
        txtDesplay.getChildren().add(text);
    }

    public void btnImgChooseOnAction(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image Files");
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("JPEG Image", "*.jpeg", "*.jpg"));
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("GIF Image", "*.gif"));
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fileChooser.showOpenDialog(btnImgChoose.getScene().getWindow());
        if (file == null) {
            lblImageFile.setText("No File Selected");
        } else {
            lblImageFile.setText(file.getAbsolutePath());

            new Thread(()->{
                try {
                    FileInputStream fis = new FileInputStream(file);

                    byte[] imageBuffer = fis.readAllBytes();
                    Media media = new Media(true, imageBuffer);
                    oos.writeObject(media);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            ImageView imageView = new ImageView(file.toURI().toString());
            imageView.setFitWidth(200);
            imageView.setPreserveRatio(true);
            txtDesplay.getChildren().add(imageView);
        }
    }


    public void showImage(byte[] imgMessage) throws IOException {

        new Thread(()->{
            System.out.println("Input Byte Buffer" + imgMessage);
            System.out.println(Arrays.toString(imgMessage));

            File output = new File("chatimage.jpg");

            try {
                output.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            try (FileOutputStream fos = new FileOutputStream(output)) {
                fos.write(imgMessage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("FileIn");

            Platform.runLater(() -> {
                ImageView imageView = new ImageView(output.toURI().toString());
                imageView.setFitWidth(200);
                imageView.setPreserveRatio(true);

                txtDesplay.getChildren().add(imageView);
            });
        }).start();

    }
}
