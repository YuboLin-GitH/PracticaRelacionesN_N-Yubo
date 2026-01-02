package com.yubo.DAO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yubo.Model.Libros;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: LibroJSON
 * Package: com.yubo.DAO
 * Description:
 *
 * @Author Yubo
 * @Create 01/01/2026 19:48
 * @Version 1.0
 */
public class LibroJSON {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    public static List<Libros> obtenerLibrosDesdeJSON(File archivo) throws IOException {


        ArrayList<Libros> listaLibros = JSON_MAPPER.readValue(
                archivo,
                JSON_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, Libros.class)
        );
        return listaLibros;
    }
}
