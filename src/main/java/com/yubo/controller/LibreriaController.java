package com.yubo.controller;

import com.yubo.DAO.LibreriaDAO;
import com.yubo.DAO.LibreriaDAOImpl;
import com.yubo.Model.Autores;
import com.yubo.Model.Editoriales;
import com.yubo.Model.Libros;
import com.yubo.util.AlertUtils;
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
            AlertUtils.mostrarError("Por favor, rellena todos los campos obligatorios.");
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

            AlertUtils.mostrarInformacion("Libro guardado correctamente");
            limpiarFormulario(null);
            cargarDatos();

        } catch (Exception e) {
            AlertUtils.mostrarError("No se pudo guardar: "+ e.getMessage());

            e.printStackTrace();
        }
    }

    // === 瘦身后的删除 ===
    @FXML
    void borrarLibro(ActionEvent event) {
        Libros libroSeleccionado = tvLibros.getSelectionModel().getSelectedItem();
        if (libroSeleccionado == null) {
            AlertUtils.mostrarAviso("Selecciona un libro de la tabla ");
            return;
        }

        ultimoLibroBorrado = libroSeleccionado;

        try {
            // 调用 DAO 删除
            libreriaDAO.borrarLibro(libroSeleccionado);

            cargarDatos();
            AlertUtils.mostrarInformacion("Libro borrado. Puedes 'Deshacer' ahora.");
        } catch (Exception e) {
            AlertUtils.mostrarError("Error al borrar: " + e.getMessage());
        }
    }

    // === 瘦身后的恢复删除 ===
    @FXML
    void recuperarBorrado(ActionEvent event) {
        if (ultimoLibroBorrado == null) {
            AlertUtils.mostrarAviso("No hay nada que recuperar ");
            return;
        }
        try {
            // 直接调用 DAO 的保存方法把旧对象存回去
            ultimoLibroBorrado.setIdlibro(0);

            libreriaDAO.guardarLibro(ultimoLibroBorrado);

            ultimoLibroBorrado = null;
            cargarDatos();
            AlertUtils.mostrarInformacion("Libro recuperado");
        } catch (Exception e) {
            AlertUtils.mostrarError("Error al recuperar: " + e.getMessage());
        }
    }

    // ... 其他辅助方法 (limpiar, agregarAutor, mostrarAlerta) 保持不变 ...
    @FXML
    void agregarAutorALibro(ActionEvent event) {
        Autores autorSeleccionado = cbAutores.getValue();
        if (autorSeleccionado != null) {
            autoresTemporales.add(autorSeleccionado);
            AlertUtils.mostrarInformacion("Autor " + autorSeleccionado.getNombre() + " añadido.");
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
                AlertUtils.mostrarAviso("l archivo JSON está vacío o no se encontró.");
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
            AlertUtils.mostrarInformacion("Importación completada. Se han guardado " + count + " libros.");

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.mostrarError("No se pudo leer el archivo JSON: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarError("Error al guardar en la base de datos: " + e.getMessage());
        }
    }


}