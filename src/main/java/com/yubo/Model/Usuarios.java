package com.yubo.Model;

import javax.persistence.*;

/**
 * ClassName: Usuarios
 * Package: com.yubo.Model
 * Description:
 *
 * @Author Yubo
 * @Create 01/01/2026 17:32
 * @Version 1.0
 */
@Entity
@Table(name = "usuarios")
public class Usuarios {

    @Id
    @Column(name = "idUsuario")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUsuario;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "password")
    private String password;

    @Column(name = "permisos")
    private String permisos;

    public Usuarios() {}

    public Usuarios(String nombre, String password, String permisos) {
        this.nombre = nombre;
        this.password = password;
        this.permisos = permisos;
    }

    // Getters and Setters...


    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPermisos() {
        return permisos;
    }

    public void setPermisos(String permisos) {
        this.permisos = permisos;
    }
}