package com.yubo.DAO;

import com.yubo.Model.Paciente;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface UsuarioDAO {
    void conectar() throws ClassNotFoundException, SQLException, IOException;

    void desconectar() throws SQLException;

    Paciente resultSetToPaciente(ResultSet resultado) throws SQLException;

    Paciente valiadarUsuario(String nombre, String passwordPlano) throws SQLException;
}
