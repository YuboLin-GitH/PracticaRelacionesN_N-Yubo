package com.yubo.DAO;

import com.yubo.Model.Autores;
import com.yubo.Model.Editoriales;
import com.yubo.Model.Libros;

import java.util.List;

/**
 * ClassName: LibreriaDAO
 * Package: com.yubo.DAO
 * Description:
 *
 * @Author Yubo
 * @Create 01/01/2026 19:17
 * @Version 1.0
 */
public interface LibreriaDAO {


    List<Libros> listarLibros();


    List<Editoriales> listarEditoriales();


    List<Autores> listarAutores();


    void guardarLibro(Libros libro) throws Exception;


    void borrarLibro(Libros libro) throws Exception;
}
