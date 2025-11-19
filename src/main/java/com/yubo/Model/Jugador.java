package com.yubo.Model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Jugador")
public class Jugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idJugador;

    @Column(name = "nombre")
    private String nombre;
    @Column(name = "password")
    private String password;

    @Column(name = "telefono")
    private int telefono;


    @OneToMany(mappedBy = "jugador", cascade = CascadeType.ALL)
    private List<Puntos> puntosHistorial;

    public Jugador() {
    }

    public Jugador(int idJugador, String nombre, String password, int telefono) {
        this.idJugador = idJugador;
        this.nombre = nombre;
        this.password = password;
        this.telefono = telefono;
    }


    public int getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(int idJugador) {
        this.idJugador = idJugador;
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

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public List<Puntos> getPuntosHistorial() {
        return puntosHistorial;
    }

    public void setPuntosHistorial(List<Puntos> puntosHistorial) {
        this.puntosHistorial = puntosHistorial;
    }
}
