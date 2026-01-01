package com.yubo.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "autores")
public class Autores {

    @Id
    @Column(name = "idAutor")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int idautor;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "nacionalidad")
    private String nacionalidad;

    //OJO la siguieente definicion va toda juna
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH,
            CascadeType.REFRESH }, fetch = FetchType.LAZY)
    @JoinTable(name = "libro_autor",
            joinColumns = @JoinColumn(name="idAutor"),
            inverseJoinColumns = @JoinColumn(name="idLibro"))
    private List<Libros> libros;


    public Autores() {
    }

    public Autores(String nombre, String nacionalidad) {
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;
    }

    // metodos get y set


    public int getIdautor() {
        return idautor;
    }

    public void setIdautor(int idautor) {
        this.idautor = idautor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public List<Libros> getLibros() {
        return libros;
    }

    public void setLibros(List<Libros> libros) {
        this.libros = libros;
    }

    @Override
    public String toString() {
        return nombre;
    }

    public void addLibro(Libros libro) {
        if (libros == null) {
            libros = new ArrayList<Libros>();
        }
        libros.add(libro);
    }

}
