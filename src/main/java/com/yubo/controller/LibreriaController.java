package com.yubo.controller;

import com.yubo.DAO.LibreriaDAO;
import com.yubo.DAO.LibreriaDAOImpl;
import com.yubo.Model.Autores;
import com.yubo.Model.Editoriales;
import com.yubo.Model.Libros;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LibreriaController {

    @FXML private TextField tfIsbn, tfTitulo;
    @FXML private ComboBox<Editoriales> cbEditorial;
    @FXML private ComboBox<Autores> cbAutores;
    @FXML private TableView<Libros> tvLibros;
    @FXML private TableColumn<Libros, Integer> tcId;
    @FXML private TableColumn<Libros, String> tcTitulo;
    @FXML private TableColumn<Libros, String> tcIsbn;
    @FXML private TableColumn<Libros, String> tcEditorial;
    @FXML private TableColumn<Libros, String> tcAutores;


    private LibreriaDAO libreriaDAO = new LibreriaDAOImpl();

    // 内部变量
    private Set<Autores> autoresTemporales = new HashSet<>();
    private Libros ultimoLibroBorrado = null;

    @FXML
    public void initialize() {
        // ... 列配置保持不变 ...
        tcId.setCellValueFactory(new PropertyValueFactory<>("idlibro"));
        tcTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        tcIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        tcEditorial.setCellValueFactory(cellData -> {
            if (cellData.getValue().getEditorial() != null) {
                return new SimpleStringProperty(cellData.getValue().getEditorial().getNombre());
            } else {
                return new SimpleStringProperty("Sin Editorial");
            }
        });
        tcAutores.setCellValueFactory(cellData -> {
            List<Autores> autores = cellData.getValue().getAutores();

            if (autores == null || autores.isEmpty()) {
                return new SimpleStringProperty("Sin autores");
            }

            // 使用 Stream 把所有作者名字提取出来，用逗号连接
            // 例如： "Miguel de Cervantes, Gabriel García Márquez"
            String nombres = autores.stream()
                    .map(Autores::getNombre)
                    .collect(Collectors.joining(", "));

            return new SimpleStringProperty(nombres);
        });

        cargarDatos();
    }

    // === 瘦身后的加载数据 ===
    private void cargarDatos() {
        // 一行代码搞定！不用再写 Session session = ...
        tvLibros.setItems(FXCollections.observableArrayList(libreriaDAO.listarLibros()));
        cbEditorial.setItems(FXCollections.observableArrayList(libreriaDAO.listarEditoriales()));
        cbAutores.setItems(FXCollections.observableArrayList(libreriaDAO.listarAutores()));
    }

    // === 瘦身后的保存 ===
    @FXML
    void guardarLibro(ActionEvent event) {
        String titulo = tfTitulo.getText();
        String isbn = tfIsbn.getText();
        Editoriales editorial = cbEditorial.getValue();

        if (titulo.isEmpty() || isbn.isEmpty() || editorial == null) {
            mostrarAlerta("Error", "Rellena título, ISBN y editorial", Alert.AlertType.WARNING);
            return;
        }

        try {
            Libros libro = new Libros(titulo, isbn);
            libro.setEditorial(editorial);

            if (!autoresTemporales.isEmpty()) {
                // 记得这里要用 ArrayList
                libro.setAutores(new ArrayList<>(autoresTemporales));
            }

            // 调用 DAO 保存
            libreriaDAO.guardarLibro(libro);

            mostrarAlerta("Éxito", "Libro guardado correctamente", Alert.AlertType.INFORMATION);
            limpiarFormulario(null);
            cargarDatos();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo guardar: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // === 瘦身后的删除 ===
    @FXML
    void borrarLibro(ActionEvent event) {
        Libros libroSeleccionado = tvLibros.getSelectionModel().getSelectedItem();
        if (libroSeleccionado == null) {
            mostrarAlerta("Aviso", "Selecciona un libro de la tabla", Alert.AlertType.WARNING);
            return;
        }

        ultimoLibroBorrado = libroSeleccionado;

        try {
            // 调用 DAO 删除
            libreriaDAO.borrarLibro(libroSeleccionado);

            cargarDatos();
            mostrarAlerta("Info", "Libro borrado. Puedes 'Deshacer' ahora.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al borrar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // === 瘦身后的恢复删除 ===
    @FXML
    void recuperarBorrado(ActionEvent event) {
        if (ultimoLibroBorrado == null) {
            mostrarAlerta("Aviso", "No hay nada que recuperar", Alert.AlertType.WARNING);
            return;
        }
        try {
            // 直接调用 DAO 的保存方法把旧对象存回去
            ultimoLibroBorrado.setIdlibro(0);

            libreriaDAO.guardarLibro(ultimoLibroBorrado);

            ultimoLibroBorrado = null;
            cargarDatos();
            mostrarAlerta("Éxito", "Libro recuperado", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al recuperar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ... 其他辅助方法 (limpiar, agregarAutor, mostrarAlerta) 保持不变 ...
    @FXML
    void agregarAutorALibro(ActionEvent event) {
        Autores autorSeleccionado = cbAutores.getValue();
        if (autorSeleccionado != null) {
            autoresTemporales.add(autorSeleccionado);
            mostrarAlerta("Info", "Autor " + autorSeleccionado.getNombre() + " añadido.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    void limpiarFormulario(ActionEvent event) {
        tfTitulo.clear();
        tfIsbn.clear();
        cbEditorial.getSelectionModel().clearSelection();
        cbAutores.getSelectionModel().clearSelection();
        autoresTemporales.clear();
    }

    @FXML
    void importarJSON(ActionEvent event) {
        try {
            // 1. 调用刚才写的辅助类读取 JSON
            List<Libros> librosImportados = com.yubo.DAO.LibroJSON.obtenerLibrosDesdeJSON();

            if (librosImportados == null || librosImportados.isEmpty()) {
                mostrarAlerta("Aviso", "El archivo JSON está vacío o no se encontró.", Alert.AlertType.WARNING);
                return;
            }

            // 2. 遍历并保存到数据库
            int count = 0;
            for (Libros libro : librosImportados) {
                // 调用 Hibernate DAO 保存每一本书
                // 因为级联配置 (Cascade)，它会自动处理里面的 Editorial 和 Autores
                libreriaDAO.guardarLibro(libro);
                count++;
            }

            // 3. 刷新表格
            cargarDatos();
            mostrarAlerta("Éxito", "Importación completada. Se han guardado " + count + " libros.", Alert.AlertType.INFORMATION);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error IO", "No se pudo leer el archivo JSON: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error Base de Datos", "Error al guardar en la base de datos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}