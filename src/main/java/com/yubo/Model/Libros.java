package com.yubo.Model;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "libros")
public class Libros {

    @Id
    @Column(name = "idLibro")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int idlibro;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "isbn")
    private String isbn;

    //ojo la siguiente defincion va toda junta
    @ManyToOne(cascade = CascadeType.ALL,
            fetch=FetchType.LAZY)
    @JoinColumn(name="ideditorial")
    private Editoriales editorial;


    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "libro_autor", //OJO es donde le digo que se crea la tabla
            joinColumns = @JoinColumn(name="idLibro"),
            inverseJoinColumns = @JoinColumn(name="idAutor"))
    private List<Autores> autores;


    public Libros() {
    }

    public Libros(String titulo, String isbn) {
        super();
        this.titulo = titulo;
        this.isbn = isbn;
    }


    public int getIdlibro() {
        return idlibro;
    }

    public void setIdlibro(int idlibro) {
        this.idlibro = idlibro;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Editoriales getEditorial() {
        return editorial;
    }

    public void setEditorial(Editoriales editorial) {
        this.editorial = editorial;
    }

    public List<Autores> getAutores() {
        return autores;
    }

    public void setAutores(List<Autores> autores) {
        this.autores = autores;
    }

    @Override
    public String toString() {
        return "Libros{" +
                "idlibro=" + idlibro +
                ", titulo='" + titulo + '\'' +
                ", isbn='" + isbn + '\'' +
                '}';
    }

    public void addAutor(Autores autor) {
        if (autores == null) {
            autores = new ArrayList<Autores>();
            //mas eficiente utilizando set
            //private Set<Proveedores> proveedores=new HashSet();
        }
        autores.add(autor);
    }

}
