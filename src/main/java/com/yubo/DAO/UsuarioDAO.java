package com.yubo.DAO;



import com.yubo.Model.Usuarios;

public interface UsuarioDAO {
    Usuarios login(String nombre, String password);
}
