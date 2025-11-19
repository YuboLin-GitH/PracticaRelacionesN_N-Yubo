package com.yubo.Model;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;



@Entity
@Table(name = "Puntos")
public class Puntos {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "idPunto")
    private int idPunto;

    @Column(name = "puntos")
    private int puntos;


    @ManyToOne
    @JoinColumn(name = "idJugador")
    private Jugador jugador;

    public Puntos() {
    }

    public Puntos(int idPunto, int puntos, Jugador jugador) {
        this.idPunto = idPunto;
        this.puntos = puntos;
        this.jugador = jugador;
    }

    @Override
    public String toString() {
        return "Puntos{" +
                "idPunto=" + idPunto +
                ", puntos=" + puntos +
                ", jugador=" + jugador +
                '}';
    }

    public int getIdPunto() {
        return idPunto;
    }

    public void setIdPunto(int idPunto) {
        this.idPunto = idPunto;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
    }

}
