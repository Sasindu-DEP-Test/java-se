package lk.ijse.dep12.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class LoginViewController {
    public Button btnLogin;
    public TextField txtNickName;
    public TextField txtPort;
    public TextField txtHostIP;

    public void btnLoginOnAction(ActionEvent actionEvent) {

        String nickName = txtNickName.getText();
        String port = txtPort.getText();
        String hostIP = txtHostIP.getText();

        if (nickName.isBlank()){
            txtNickName.requestFocus();
            txtNickName.selectAll();
            return;
        }

        if (port.isBlank()){
            txtPort.requestFocus();
            txtPort.selectAll();
            return;
        }

        if (hostIP.isBlank()){
            txtHostIP.requestFocus();
            txtHostIP.selectAll();
            return;
        }

        try {
            Socket remoteSocket = new Socket(hostIP.strip(), Integer.parseInt(port));
            ((Stage)(btnLogin.getScene().getWindow())).close();
            Stage mainStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
            mainStage.setScene(new Scene(fxmlLoader.load()));
            MainViewController controller = fxmlLoader.getController();

            controller.initData(remoteSocket,nickName);
            mainStage.setTitle("Dep 12 General Chat App");
            mainStage.show();
            mainStage.centerOnScreen();

        }catch (UnknownHostException e){
            new Alert(Alert.AlertType.ERROR, "Unknown Host").show();
            txtHostIP.requestFocus();
            txtHostIP.selectAll();

        }catch (NumberFormatException e){
            new Alert(Alert.AlertType.ERROR, "Invalid Port").show();
            txtPort.requestFocus();
            txtPort.selectAll();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,"Failed to connect to server").show();
        }

    }
}
