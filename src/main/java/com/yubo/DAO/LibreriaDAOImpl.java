package com.yubo.DAO;

import com.yubo.Model.Autores;
import com.yubo.Model.Editoriales;
import com.yubo.Model.Libros;
import com.yubo.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * ClassName: LibreriaDAOImpl
 * Package: com.yubo.DAO
 * Description:
 *
 * @Author Yubo
 * @Create 01/01/2026 19:17
 * @Version 1.0
 */
public class LibreriaDAOImpl implements LibreriaDAO {

    // 1. 获取所有书籍 (用于填充表格)
    @Override
    public List<Libros> listarLibros() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // 修改点：加了 DISTINCT 和 LEFT JOIN FETCH l.autores
            // 意思：查书的同时，把出版社和作者都带出来。DISTINCT 防止因为有多个作者导致书名重复显示。
            String hql = "SELECT DISTINCT l FROM Libros l " +
                    "LEFT JOIN FETCH l.editorial " +
                    "LEFT JOIN FETCH l.autores";
            return session.createQuery(hql, Libros.class).list();
        }
    }

    // 2. 获取所有出版社 (用于填充下拉框)
    @Override
    public List<Editoriales> listarEditoriales() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Editoriales", Editoriales.class).list();
        }
    }

    // 3. 获取所有作者 (用于填充下拉框)
    @Override
    public List<Autores> listarAutores() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Autores", Autores.class).list();
        }
    }

    // 4. 保存或更新书籍
    @Override
    public void guardarLibro(Libros libro) throws Exception {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.saveOrUpdate(libro); // saveOrUpdate 既能保存也能修改
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e; // 抛出异常让 Controller 去显示错误弹窗
        }
    }

    // 5. 删除书籍
    @Override
    public void borrarLibro(Libros libro) throws Exception {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.delete(libro);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }



    @Override
    public Editoriales buscarEditorialPorNombre(String nombre) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // HQL 查询：根据名字找出版社
            String hql = "FROM Editoriales WHERE nombre = :nombre";
            return session.createQuery(hql, Editoriales.class)
                    .setParameter("nombre", nombre)
                    .uniqueResult(); // 如果找到返回对象，找不到返回 null
        } catch (Exception e) {
            return null;
        }
    }

    // ⭐【新增】实现单独保存出版社
    @Override
    public void guardarEditorial(Editoriales editorial) throws Exception {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.saveOrUpdate(editorial);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }


    @Override
    public Autores buscarAutorPorNombre(String nombre) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // HQL 查询：根据名字找作者
            String hql = "FROM Autores WHERE nombre = :nombre";
            return session.createQuery(hql, Autores.class)
                    .setParameter("nombre", nombre)
                    .uniqueResult();
        } catch (Exception e) {
            return null;
        }
    }



    // ⭐【新增】实现单独保存作者
    @Override
    public void guardarAutor(Autores autor) throws Exception {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.saveOrUpdate(autor);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
