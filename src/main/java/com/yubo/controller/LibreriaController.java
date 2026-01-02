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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LibreriaController {

    // === FXML 组件 ===
    @FXML private TextField tfIsbn, tfTitulo;
    @FXML private ComboBox<Editoriales> cbEditorial;
    @FXML private ComboBox<Autores> cbAutores;

    // 主表格
    @FXML private TableView<Libros> tvLibros;
    @FXML private TableColumn<Libros, Integer> tcId;
    @FXML private TableColumn<Libros, String> tcTitulo;
    @FXML private TableColumn<Libros, String> tcIsbn;
    @FXML private TableColumn<Libros, String> tcEditorial;

    // 小表格 (N-M 关联管理)
    @FXML private TableView<Autores> tvAutoresSeleccionados;
    @FXML private TableColumn<Autores, String> tcNombreAutor;

    // === 数据层 & 逻辑变量 ===
    private LibreriaDAO libreriaDAO = new LibreriaDAOImpl();
    private Libros ultimoLibroBorrado = null;

    // ⭐ 核心：用来绑定小表格的列表，实时显示当前编辑书籍的作者
    private ObservableList<Autores> listaAutoresEditando = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. 配置主表格列
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


        // 2. 配置小表格 (作者列表)
        tcNombreAutor.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tvAutoresSeleccionados.setItems(listaAutoresEditando);

        // 3. 加载初始数据
        cargarDatos();

        // 4. 监听主表格点击事件 (实现 "点击即编辑")
        tvLibros.getSelectionModel().selectedItemProperty().addListener((obs, oldLibro, libro) -> {
            if (libro != null) {
                // 填充基本信息
                tfTitulo.setText(libro.getTitulo());
                tfIsbn.setText(libro.getIsbn());
                cbEditorial.setValue(libro.getEditorial());

                // 填充小表格 (处理 N-M)
                listaAutoresEditando.clear();
                if (libro.getAutores() != null) {
                    // 只要 Entity 里加了 FetchType.EAGER，这里就能直接获取
                    listaAutoresEditando.addAll(libro.getAutores());
                }
            }
        });
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
            // 无论表格选没选中，这里都强制创建一个新对象
            Libros libroNuevo = new Libros(titulo, isbn);
            libroNuevo.setEditorial(editorial);

            // 从小表格获取作者
            libroNuevo.setAutores(new ArrayList<>(listaAutoresEditando));

            // 保存
            libreriaDAO.guardarLibro(libroNuevo);

            AlertUtils.mostrarInformacion("Libro NUEVO guardado correctamente.");
            limpiarFormulario(null);
            cargarDatos();

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    // === 2. 纯粹的修改方法 ===
    @FXML
    void modificarLibro(ActionEvent event) {
        // 先检查是否选中了要修改的书
        Libros libroSeleccionado = tvLibros.getSelectionModel().getSelectedItem();

        if (libroSeleccionado == null) {
            AlertUtils.mostrarAviso("¡Selecciona un libro de la tabla para modificar!");
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
            // 直接修改选中的那个对象 (保留 ID)
            libroSeleccionado.setTitulo(titulo);
            libroSeleccionado.setIsbn(isbn);
            libroSeleccionado.setEditorial(editorial);

            // 更新作者 (从小表格拿)
            libroSeleccionado.setAutores(new ArrayList<>(listaAutoresEditando));

            // Hibernate 更新
            libreriaDAO.guardarLibro(libroSeleccionado);

            AlertUtils.mostrarInformacion("Libro modificado correctamente.");
            limpiarFormulario(null);
            cargarDatos();

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarError("Error al modificar: " + e.getMessage());
        }
    }
    // === 添加作者到小表格 ===
    @FXML
    void agregarAutorALibro(ActionEvent event) {
        Autores autorSeleccionado = cbAutores.getValue();

        if (autorSeleccionado == null) {
            AlertUtils.mostrarAviso("Selecciona un autor del combo primero.");
            return;
        }

        // 防止重复添加
        if (listaAutoresEditando.contains(autorSeleccionado)) {
            AlertUtils.mostrarAviso("Este autor ya está en la lista.");
            return;
        }

        // 加到 ObservableList，界面小表格会自动刷新
        listaAutoresEditando.add(autorSeleccionado);
    }

    // === 从小表格移除作者 ===
    @FXML
    void quitarAutorDeLibro(ActionEvent event) {
        Autores autorAQuitar = tvAutoresSeleccionados.getSelectionModel().getSelectedItem();

        if (autorAQuitar == null) {
            AlertUtils.mostrarAviso("Selecciona un autor de la tabla pequeña para quitar.");
            return;
        }

        listaAutoresEditando.remove(autorAQuitar);
    }

    // === 删除书籍 ===
    @FXML
    void borrarLibro(ActionEvent event) {
        Libros libroSeleccionado = tvLibros.getSelectionModel().getSelectedItem();
        if (libroSeleccionado == null) {
            AlertUtils.mostrarAviso("Selecciona un libro de la tabla para borrar.");
            return;
        }

        ultimoLibroBorrado = libroSeleccionado;

        try {
            // 直接删除 (Hibernate 配置好级联后会自动处理中间表)
            libreriaDAO.borrarLibro(libroSeleccionado);

            // 删除后也要清空表单
            limpiarFormulario(null);
            cargarDatos();

            AlertUtils.mostrarInformacion("Libro borrado. Puedes 'Deshacer' ahora.");
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarError("Error al borrar: " + e.getMessage());
        }
    }

    // === 撤销删除 ===
    @FXML
    void recuperarBorrado(ActionEvent event) {
        if (ultimoLibroBorrado == null) {
            AlertUtils.mostrarAviso("No hay nada que recuperar.");
            return;
        }
        try {
            // 创建新对象以避免 ID 冲突，并恢复数据
            Libros libroNuevo = new Libros();
            libroNuevo.setTitulo(ultimoLibroBorrado.getTitulo());
            libroNuevo.setIsbn(ultimoLibroBorrado.getIsbn());
            libroNuevo.setEditorial(ultimoLibroBorrado.getEditorial());

            if (ultimoLibroBorrado.getAutores() != null) {
                libroNuevo.setAutores(new ArrayList<>(ultimoLibroBorrado.getAutores()));
            }

            libreriaDAO.guardarLibro(libroNuevo);

            ultimoLibroBorrado = null;
            cargarDatos();
            AlertUtils.mostrarInformacion("Libro recuperado correctamente");
        } catch (Exception e) {
            AlertUtils.mostrarError("Error al recuperar: " + e.getMessage());
        }
    }

    // === 清空表单 (重置为新增模式) ===
    @FXML
    void limpiarFormulario(ActionEvent event) {
        tfTitulo.clear();
        tfIsbn.clear();
        cbEditorial.getSelectionModel().clearSelection();
        cbAutores.getSelectionModel().clearSelection();

        // 清空小表格数据
        listaAutoresEditando.clear();

        // 取消主表格的选中状态 (这意味着变回 "新增模式")
        tvLibros.getSelectionModel().clearSelection();
    }

    @FXML
    void importarJSON(ActionEvent event) {
        try {
            List<Libros> librosImportados = com.yubo.DAO.LibroJSON.obtenerLibrosDesdeJSON();

            if (librosImportados == null || librosImportados.isEmpty()) {
                AlertUtils.mostrarAviso("El archivo JSON está vacío.");
                return;
            }

            // 获取现有 ISBN 防止重复书籍
            List<Libros> librosExistentes = libreriaDAO.listarLibros();
            Set<String> isbnsExistentes = librosExistentes.stream()
                    .map(Libros::getIsbn)
                    .collect(Collectors.toSet());

            int guardados = 0;
            int omitidos = 0;

            for (Libros libroNuevo : librosImportados) {
                try {
                    // 1. 检查书籍本身是否重复 (ISBN)
                    if (isbnsExistentes.contains(libroNuevo.getIsbn())) {
                        omitidos++;
                        continue;
                    }

                    // === ⭐ 2. 智能处理出版社 (Editorial) ⭐ ===
                    if (libroNuevo.getEditorial() != null) {
                        String nombreEd = libroNuevo.getEditorial().getNombre();
                        Editoriales edExistente = libreriaDAO.buscarEditorialPorNombre(nombreEd);

                        if (edExistente != null) {
                            // 情况A：数据库有，用旧的
                            libroNuevo.setEditorial(edExistente);
                        } else {
                            // 情况B：数据库没有，手动保存这个新的出版社！
                            // 这一步是为了防止 TransientObjectException
                            libreriaDAO.guardarEditorial(libroNuevo.getEditorial());
                        }
                    }

                    // === ⭐ 3. 智能处理作者 (Autores) ⭐ ===
                    if (libroNuevo.getAutores() != null && !libroNuevo.getAutores().isEmpty()) {
                        List<Autores> autoresFinales = new ArrayList<>();

                        for (Autores autorJson : libroNuevo.getAutores()) {
                            Autores autorExistente = libreriaDAO.buscarAutorPorNombre(autorJson.getNombre());

                            if (autorExistente != null) {
                                // 情况A：数据库有，加入旧作者
                                autoresFinales.add(autorExistente);
                            } else {
                                // 情况B：数据库没有，手动保存新作者！
                                libreriaDAO.guardarAutor(autorJson);
                                autoresFinales.add(autorJson);
                            }
                        }
                        // 替换书的作者列表
                        libroNuevo.setAutores(autoresFinales);
                    }

                    // 4. 最后保存书籍
                    // 此时 Editorial 和 Autores 都在数据库里有 ID 了，绝对不会报错
                    libreriaDAO.guardarLibro(libroNuevo);
                    guardados++;

                } catch (Exception e) {
                    System.err.println("Error importando libro: " + libroNuevo.getTitulo());
                    e.printStackTrace();
                }
            }

            cargarDatos();
            String msg = "Importación completada.\n✔ Nuevos: " + guardados + "\n⚠ Repetidos: " + omitidos;
            AlertUtils.mostrarInformacion(msg);

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.mostrarError("Error al importar: " + e.getMessage());
        }
    }
}