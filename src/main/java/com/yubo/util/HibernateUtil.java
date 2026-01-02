package com.yubo.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.Properties;
import java.io.InputStream;

public class HibernateUtil {
	private static SessionFactory sessionFactory;

	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			try {
				// 1. 创建配置对象
				Configuration configuration = new Configuration();

				// ⚠️ 注意：如果你之前的代码里写的是 "configuration/hibernate.cfg.xml"
				// 说明你的 xml 文件在一个子文件夹里。请保留这个路径：
				configuration.configure("configuration/hibernate.cfg.xml");
				// 如果你的 xml 直接在 src/main/resources 下，就用空的 .configure();

				// 2. 加载 db.properties
				Properties settings = new Properties();
				try (InputStream input = HibernateUtil.class.getClassLoader().getResourceAsStream("configuration/database.properties")) {
					if (input == null) {
						System.out.println("⚠️ 无法找到 db.properties，请确保它在 src/main/resources 下！");
						return null;
					}
					settings.load(input);
				}

				// 3. 读取字段
				String host = settings.getProperty("host");
				String port = settings.getProperty("port");
				String dbName = settings.getProperty("name");
				String user = settings.getProperty("username");
				String pass = settings.getProperty("password");

				// 4. 拼接 URL 并设置属性
				String connectionUrl = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", host, port, dbName);

				configuration.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
				configuration.setProperty("hibernate.connection.url", connectionUrl);
				configuration.setProperty("hibernate.connection.username", user);
				configuration.setProperty("hibernate.connection.password", pass);

				// 5. 创建工厂
				sessionFactory = configuration.buildSessionFactory();
				System.out.println("✅ Hibernate 启动成功！连接地址: " + connectionUrl);

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("❌ Hibernate 初始化失败: " + e.getMessage());
			}
		}
		return sessionFactory;
	}
}