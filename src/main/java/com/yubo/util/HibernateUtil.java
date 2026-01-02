package com.yubo.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.InputStream;
import java.util.Properties;

public class HibernateUtil {

	private static SessionFactory sessionFactory;

	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			try {
				// 1. Crear el objeto de configuración
				Configuration configuration = new Configuration();


				configuration.configure("configuration/hibernate.cfg.xml");


				// 2. Cargar database.properties
				Properties settings = new Properties();
				try (InputStream input = HibernateUtil.class.getClassLoader().getResourceAsStream("configuration/database.properties")) {
					if (input == null) {
						System.out.println("No se pudo encontrar database.properties. ");
						return null;
					}
					settings.load(input);
				}

				// 3. Leer propiedades
				String host = settings.getProperty("host");
				String port = settings.getProperty("port");
				String dbName = settings.getProperty("name");
				String user = settings.getProperty("username");
				String pass = settings.getProperty("password");

				// 4. Construir la URL de conexión y configurar Hibernate
				String connectionUrl = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", host, port, dbName);

				configuration.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
				configuration.setProperty("hibernate.connection.url", connectionUrl);
				configuration.setProperty("hibernate.connection.username", user);
				configuration.setProperty("hibernate.connection.password", pass);

				// 5. Crear la SessionFactory
				sessionFactory = configuration.buildSessionFactory();
				System.out.println("Hibernate iniciado correctamente. URL: " + connectionUrl);

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Error al inicializar Hibernate: " + e.getMessage());
			}
		}
		return sessionFactory;
	}
}
