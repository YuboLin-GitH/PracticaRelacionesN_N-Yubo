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
 * Description: Implementación DAO para la gestión de libros, autores y editoriales.
 *
 * @Author Yubo
 * @Create 01/01/2026 19:17
 * @Version 1.0
 */
public class LibreriaDAOImpl implements LibreriaDAO {

    // 1. Obtener todos los libros (para rellenar la tabla)
    @Override
    public List<Libros> listarLibros() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Se usa DISTINCT y LEFT JOIN FETCH para evitar duplicados
            // y cargar editoriales y autores en una sola consulta
            String hql = "SELECT DISTINCT l FROM Libros l " +
                    "LEFT JOIN FETCH l.editorial " +
                    "LEFT JOIN FETCH l.autores";
            return session.createQuery(hql, Libros.class).list();
        }
    }

    // 2. Obtener todas las editoriales (para el ComboBox)
    @Override
    public List<Editoriales> listarEditoriales() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Editoriales", Editoriales.class).list();
        }
    }

    // 3. Obtener todos los autores (para el ComboBox)
    @Override
    public List<Autores> listarAutores() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Autores", Autores.class).list();
        }
    }

    // 4. Guardar o actualizar un libro
    @Override
    public void guardarLibro(Libros libro) throws Exception {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            // saveOrUpdate permite insertar o actualizar según el estado del objeto
            session.saveOrUpdate(libro);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            // Se propaga la excepción para que el Controller la gestione
            throw e;
        }
    }

    // 5. Eliminar un libro
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

    // Buscar una editorial por su nombre
    @Override
    public Editoriales buscarEditorialPorNombre(String nombre) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Consulta HQL para buscar editorial por nombre
            String hql = "FROM Editoriales WHERE nombre = :nombre";
            return session.createQuery(hql, Editoriales.class)
                    .setParameter("nombre", nombre)
                    .uniqueResult(); // Devuelve null si no existe
        } catch (Exception e) {
            return null;
        }
    }

    // Guardar o actualizar una editorial de forma independiente
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

    // Buscar un autor por su nombre
    @Override
    public Autores buscarAutorPorNombre(String nombre) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Consulta HQL para buscar autor por nombre
            String hql = "FROM Autores WHERE nombre = :nombre";
            return session.createQuery(hql, Autores.class)
                    .setParameter("nombre", nombre)
                    .uniqueResult();
        } catch (Exception e) {
            return null;
        }
    }

    // Guardar o actualizar un autor de forma independiente
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
