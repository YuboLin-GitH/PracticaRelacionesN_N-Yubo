package com.yubo.Model;

import javax.persistence.*;
import java.util.List;

/**
 * ClassName: Editoriales
 * Package: com.yubo.Model
 * Description:
 *
 * @Author Yubo
 * @Create 01/01/2026 14:46
 * @Version 1.0
 */


@Entity
@Table(name = "editoriales")
public class Editoriales {
    @Id
    @Column(name = "ideditorial")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int ideditorial;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "ciudad")
    private String ciudad;

    //ojo la siguiente defincion va toda junta
    @OneToMany(mappedBy="editorial",
            cascade= {CascadeType.PERSIST,CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private List<Libros> libros;



    public Editoriales() {
    }

    public Editoriales(String nombre) {
        this.nombre = nombre;
    }


    // metodo get y set
    public List<Libros> getLibros() {
        return libros;
    }

    public void setLibros(List<Libros> libros) {
        this.libros = libros;
    }


    public int getIdeditorial() {
        return ideditorial;
    }

    public void setIdeditorial(int ideditorial) {
        this.ideditorial = ideditorial;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    @Override
    public String toString() {
        return "Editoriales{" +
                "ideditorial=" + ideditorial +
                ", nombre='" + nombre + '\'' +
                ", ciudad='" + ciudad + '\'' +
                '}';
    }
}
