package com.yubo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;
import java.util.stream.Collectors;

public class DatabaseImporta {


    private static final String MARKER_FILE_NAME = "db_setup_done.marker";

    public static void inicializarBaseDatos() {


        File marker = new File(MARKER_FILE_NAME);
        if (marker.exists()) {
            System.out.println(" La base de datos ya fue inicializada anteriormente. Saltando SQL.");
            return;
        }

        System.out.println("Iniciando importación de script SQL (Primera vez)...");

        try {
            Properties props = new Properties();
            InputStream input = null;


            File externalProps = new File("db.properties");

            if (externalProps.exists()) {
                System.out.println(" Leyendo configuración externa (db.properties)...");
                input = new java.io.FileInputStream(externalProps);
            } else {
                System.out.println(" Leyendo configuración interna del JAR...");

                input = DatabaseImporta.class.getResourceAsStream("/configuration/database.properties");

            }

            if (input == null) {
                System.err.println("ERROR: No se encuentra el archivo de propiedades.");
                return;
            }

            props.load(input);
            if (input instanceof java.io.FileInputStream) {
                input.close();
            }

            String host = props.getProperty("host");
            String port = props.getProperty("port");
            String user = props.getProperty("username");
            String pass = props.getProperty("password");


            String fullUrl = "jdbc:mysql://" + host + ":" + port + "/?serverTimezone=UTC&allowMultiQueries=true";


            InputStream sqlStream = DatabaseImporta.class.getResourceAsStream("/BaseDatos/libreria.sql");

            if (sqlStream == null) {
                System.err.println("ERROR: No se encuentra el archivo /BaseDatos/libreria.sql");
                return;
            }

            String sqlScript = new BufferedReader(new InputStreamReader(sqlStream, "UTF-8"))
                    .lines()
                    .collect(Collectors.joining("\n"));

            try (Connection conn = DriverManager.getConnection(fullUrl, user, pass);
                 Statement stmt = conn.createStatement()) {

                stmt.execute(sqlScript);
                System.out.println("Script SQL importado correctamente.");

                if (marker.createNewFile()) {
                    System.out.println("Archivo de marca creado: " + MARKER_FILE_NAME);
                }
            }

        } catch (Exception e) {
            System.err.println(" Error al importar SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}