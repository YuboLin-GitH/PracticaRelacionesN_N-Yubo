package com.yubo.controller;

import com.yubo.DAO.LibreriaDAO;
import com.yubo.DAO.LibreriaDAOImpl;
import com.yubo.Model.Autores;
import com.yubo.Model.Editoriales;
import com.yubo.Model.Libros;
import com.yubo.util.AlertUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LibreriaController {

    // === Componentes FXML ===
    @FXML private TextField tfIsbn, tfTitulo;
    @FXML private ComboBox<Editoriales> cbEditorial;
    @FXML private ComboBox<Autores> cbAutores;

    @FXML private Button btImportarJSON;
    // Tabla principal
    @FXML private TableView<Libros> tvLibros;
    @FXML private TableColumn<Libros, Integer> tcId;
    @FXML private TableColumn<Libros, String> tcTitulo;
    @FXML private TableColumn<Libros, String> tcIsbn;
    @FXML private TableColumn<Libros, String> tcEditorial;

    // Tabla secundaria (gestión relación N-M)
    @FXML private TableView<Autores> tvAutoresSeleccionados;
    @FXML private TableColumn<Autores, String> tcNombreAutor;


    // === Capa de datos y variables de control ===
    private LibreriaDAO libreriaDAO = new LibreriaDAOImpl();
    private Libros ultimoLibroBorrado = null;

    // Lista observable para mostrar los autores del libro en edición
    private ObservableList<Autores> listaAutoresEditando = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Configuración de columnas de la tabla principal
        tcId.setCellValueFactory(new PropertyValueFactory<>("idlibro"));
        tcTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        tcIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        tcEditorial.setCellValueFactory(cellData -> {
            if (cellData.getValue().getEditorial() != null) {
                return new SimpleStringProperty(
                        cellData.getValue().getEditorial().getNombre()
                );
            } else {
                return new SimpleStringProperty("Sin Editorial");
            }
        });

        // 2. Configuración de la tabla secundaria (autores)
        tcNombreAutor.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tvAutoresSeleccionados.setItems(listaAutoresEditando);

        // 3. Carga inicial de datos
        cargarDatos();

        // 4. Listener de selección para editar al hacer clic
        tvLibros.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldLibro, libro) -> {
                    if (libro != null) {
                        tfTitulo.setText(libro.getTitulo());
                        tfIsbn.setText(libro.getIsbn());
                        cbEditorial.setValue(libro.getEditorial());

                        listaAutoresEditando.clear();
                        if (libro.getAutores() != null) {
                            listaAutoresEditando.addAll(libro.getAutores());
                        }
                    }
                }
        );
    }

    private void cargarDatos() {
        tvLibros.setItems(FXCollections.observableArrayList(libreriaDAO.listarLibros()));
        cbEditorial.setItems(FXCollections.observableArrayList(libreriaDAO.listarEditoriales()));
        cbAutores.setItems(FXCollections.observableArrayList(libreriaDAO.listarAutores()));
    }

    @FXML
    void guardarLibro(ActionEvent event) {
        String titulo = tfTitulo.getText();
        String isbn = tfIsbn.getText();
        Editoriales editorial = cbEditorial.getValue();

        if (titulo.isEmpty() || isbn.isEmpty() || editorial == null) {
            AlertUtils.mostrarError("Por favor, rellena todos los campos para guardar.");
            return;
        }

        try {
            Libros libroNuevo = new Libros(titulo, isbn);
            libroNuevo.setEditorial(editorial);
            libroNuevo.setAutores(new ArrayList<>(listaAutoresEditando));

            libreriaDAO.guardarLibro(libroNuevo);

            AlertUtils.mostrarInformacion("Libro nuevo guardado correctamente.");
            limpiarFormulario(null);
            cargarDatos();

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    void modificarLibro(ActionEvent event) {
        Libros libroSeleccionado = tvLibros.getSelectionModel().getSelectedItem();

        if (libroSeleccionado == null) {
            AlertUtils.mostrarAviso("Selecciona un libro de la tabla para modificar.");
            return;
        }

        String titulo = tfTitulo.getText();
        String isbn = tfIsbn.getText();
        Editoriales editorial = cbEditorial.getValue();

        if (titulo.isEmpty() || isbn.isEmpty() || editorial == null) {
            AlertUtils.mostrarError("No puedes dejar campos vacíos al modificar.");
            return;
        }

        try {
            libroSeleccionado.setTitulo(titulo);
            libroSeleccionado.setIsbn(isbn);
            libroSeleccionado.setEditorial(editorial);
            libroSeleccionado.setAutores(new ArrayList<>(listaAutoresEditando));

            libreriaDAO.guardarLibro(libroSeleccionado);

            AlertUtils.mostrarInformacion("Libro modificado correctamente.");
            limpiarFormulario(null);
            cargarDatos();

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarError("Error al modificar: " + e.getMessage());
        }
    }

    @FXML
    void agregarAutorALibro(ActionEvent event) {
        Autores autorSeleccionado = cbAutores.getValue();

        if (autorSeleccionado == null) {
            AlertUtils.mostrarAviso("Selecciona un autor del combo primero.");
            return;
        }

        if (listaAutoresEditando.contains(autorSeleccionado)) {
            AlertUtils.mostrarAviso("Este autor ya está en la lista.");
            return;
        }

        listaAutoresEditando.add(autorSeleccionado);
    }

    @FXML
    void quitarAutorDeLibro(ActionEvent event) {
        Autores autorAQuitar = tvAutoresSeleccionados
                .getSelectionModel()
                .getSelectedItem();

        if (autorAQuitar == null) {
            AlertUtils.mostrarAviso(
                    "Selecciona un autor de la tabla secundaria para quitar."
            );
            return;
        }

        listaAutoresEditando.remove(autorAQuitar);
    }

    @FXML
    void borrarLibro(ActionEvent event) {
        Libros libroSeleccionado = tvLibros.getSelectionModel().getSelectedItem();

        if (libroSeleccionado == null) {
            AlertUtils.mostrarAviso(
                    "Selecciona un libro de la tabla para borrar."
            );
            return;
        }

        ultimoLibroBorrado = libroSeleccionado;

        try {
            libreriaDAO.borrarLibro(libroSeleccionado);

            limpiarFormulario(null);
            cargarDatos();

            AlertUtils.mostrarInformacion(
                    "Libro borrado. Puedes deshacer la acción."
            );
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarError("Error al borrar: " + e.getMessage());
        }
    }

    @FXML
    void recuperarBorrado(ActionEvent event) {
        if (ultimoLibroBorrado == null) {
            AlertUtils.mostrarAviso("No hay ningún libro para recuperar.");
            return;
        }

        try {
            Libros libroNuevo = new Libros();
            libroNuevo.setTitulo(ultimoLibroBorrado.getTitulo());
            libroNuevo.setIsbn(ultimoLibroBorrado.getIsbn());
            libroNuevo.setEditorial(ultimoLibroBorrado.getEditorial());

            if (ultimoLibroBorrado.getAutores() != null) {
                libroNuevo.setAutores(
                        new ArrayList<>(ultimoLibroBorrado.getAutores())
                );
            }

            libreriaDAO.guardarLibro(libroNuevo);

            ultimoLibroBorrado = null;
            cargarDatos();

            AlertUtils.mostrarInformacion(
                    "Libro recuperado correctamente."
            );
        } catch (Exception e) {
            AlertUtils.mostrarError("Error al recuperar: " + e.getMessage());
        }
    }

    @FXML
    void limpiarFormulario(ActionEvent event) {
        tfTitulo.clear();
        tfIsbn.clear();
        cbEditorial.getSelectionModel().clearSelection();
        cbAutores.getSelectionModel().clearSelection();
        listaAutoresEditando.clear();
        tvLibros.getSelectionModel().clearSelection();
    }

    @FXML
    void importarJSON(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos JSON", "*.json"));


        File archivoSeleccionado = fileChooser.showOpenDialog(btImportarJSON.getScene().getWindow());

        if (archivoSeleccionado == null) {
            return;
        }

        try {
            List<Libros> librosImportados = com.yubo.DAO.LibroJSON.obtenerLibrosDesdeJSON(archivoSeleccionado);

            if (librosImportados == null || librosImportados.isEmpty()) {
                AlertUtils.mostrarAviso(
                        "El archivo JSON está vacío."
                );
                return;
            }

            List<Libros> librosExistentes = libreriaDAO.listarLibros();
            Set<String> isbnsExistentes = librosExistentes.stream()
                    .map(Libros::getIsbn)
                    .collect(Collectors.toSet());

            int guardados = 0;
            int omitidos = 0;

            for (Libros libroNuevo : librosImportados) {
                try {
                    if (isbnsExistentes.contains(libroNuevo.getIsbn())) {
                        omitidos++;
                        continue;
                    }

                    if (libroNuevo.getEditorial() != null) {
                        String nombreEd =
                                libroNuevo.getEditorial().getNombre();
                        Editoriales edExistente =
                                libreriaDAO.buscarEditorialPorNombre(nombreEd);

                        if (edExistente != null) {
                            libroNuevo.setEditorial(edExistente);
                        } else {
                            libreriaDAO.guardarEditorial(
                                    libroNuevo.getEditorial()
                            );
                        }
                    }

                    if (libroNuevo.getAutores() != null
                            && !libroNuevo.getAutores().isEmpty()) {

                        List<Autores> autoresFinales = new ArrayList<>();

                        for (Autores autorJson : libroNuevo.getAutores()) {
                            Autores autorExistente =
                                    libreriaDAO.buscarAutorPorNombre(
                                            autorJson.getNombre()
                                    );

                            if (autorExistente != null) {
                                autoresFinales.add(autorExistente);
                            } else {
                                libreriaDAO.guardarAutor(autorJson);
                                autoresFinales.add(autorJson);
                            }
                        }
                        libroNuevo.setAutores(autoresFinales);
                    }

                    libreriaDAO.guardarLibro(libroNuevo);
                    guardados++;

                } catch (Exception e) {
                    System.err.println("Error importando libro: " + libroNuevo.getTitulo());
                    e.printStackTrace();
                }
            }

            cargarDatos();


            AlertUtils.mostrarInformacion("Importación completada.\n"
                    + "Nuevos: " + guardados + "\n"
                    + "Repetidos: " + omitidos);

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarError("Error al importar: " + e.getMessage());
        }
    }
}
