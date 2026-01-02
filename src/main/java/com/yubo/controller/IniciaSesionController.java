package com.yubo.controller;


import com.yubo.DAO.UsuarioDAO;
import com.yubo.DAO.UsuarioDAOImpl;
import com.yubo.Model.Usuarios;

import com.yubo.util.AlertUtils;
import com.yubo.util.R;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import java.io.IOException;

public class IniciaSesionController {


    private UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    @FXML private TextField tfUser;
    @FXML private PasswordField pfPassword;
    @FXML private Button btIniciar;

    @FXML
    public void initialize() {
        tfUser.setOnKeyPressed(this::handleEnterKey);
        pfPassword.setOnKeyPressed(this::handleEnterKey);
    }

    private void handleEnterKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            validarUsuario();
        }
    }

    @FXML
    private void validarUsuario() {
        String nombre = tfUser.getText().trim();
        String password = pfPassword.getText().trim();

        if (nombre.isEmpty() || password.isEmpty()) {
            AlertUtils.mostrarError("Por favor, rellene todos los campos.");
            return;
        }


        Usuarios usuario = usuarioDAO.login(nombre, password);

        if (usuario != null) {
            AlertUtils.mostrarInformacion("Bienvenido: " + usuario.getNombre());

            Stage stage = (Stage) btIniciar.getScene().getWindow();
            stage.close();


            abrirVentanaPrincipal();
        } else {
            AlertUtils.mostrarError("Usuario o contraseña incorrectos");
        }
    }


    private void abrirVentanaPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(R.getUI("libreria.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Sistema Libreria");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.mostrarError("No se pudo abrir la ventana principal");
        }
    }
}