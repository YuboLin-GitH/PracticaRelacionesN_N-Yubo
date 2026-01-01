/*package com.yubo;

import com.yubo.util.R;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(R.getUI("taller.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("panel de taller");
        stage.setScene(scene);
        stage.show();


    }


    public static void main(String[] args) {
        launch();
    }
}
*/

package com.yubo;

import com.yubo.Model.Autores;
import com.yubo.Model.Editoriales;
import com.yubo.Model.Libros;
import com.yubo.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class App {
    public static void main(String[] args) {

        // 1. 创建 SessionFactory (记得检查 hibernate.cfg.xml 里的配置)
        SessionFactory factory = HibernateUtil.getSessionFactory();


        // 2. 获取 Session
        Session session = HibernateUtil.getSession();

        try {
            // --- 开始测试 ---
            session.beginTransaction();

            // A. 创建对象
            System.out.println("1. 创建对象...");

            // 创建一个出版社
            Editoriales edit1 = new Editoriales("O'Reilly Media");
            edit1.setCiudad("California");

            // 创建一本书
            Libros libro1 = new Libros("Java Programming", "978-0001");

            // 创建两个作者
            Autores autor1 = new Autores("Kathy Sierra", "USA");
            Autores autor2 = new Autores("Bert Bates", "USA");

            // B. 建立关联
            System.out.println("2. 建立关联...");

            // 关联 书 -> 出版社
            libro1.setEditorial(edit1);
            // 注意：因为是双向关系，建议最好两边都加一下 (可选，但推荐)
            // edit1.getLibros().add(libro1);

            // 关联 书 <-> 作者 (N-M)
            // 使用你在 Libros 类里写的辅助方法 addAutor
            libro1.addAutor(autor1);
            libro1.addAutor(autor2);

            // 如果你想让双向关系更健壮，也可以反向加一下
            // autor1.addLibro(libro1);
            // autor2.addLibro(libro1);

            // C. 保存数据
            System.out.println("3. 保存到数据库...");

// 1. 先保存出版社 (推荐先存 "1" 的一端)
            session.save(edit1);

// 2. 必须保存作者！ (这是你缺失的步骤)
            session.save(autor1); // 保存 Kathy Sierra
            session.save(autor2); // 保存 Bert Bates

// 3. 最后保存书 (因为它关联了上面所有的对象)
            session.save(libro1);

            session.getTransaction().commit();
            System.out.println("成功！数据已保存！");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            factory.close();
        }
    }
}