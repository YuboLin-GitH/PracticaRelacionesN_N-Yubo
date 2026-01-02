package com.yubo.DAO;

import com.yubo.Model.Usuarios;
import com.yubo.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public Usuarios login(String nombre, String password) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Usuarios user = null;

        try {
            // Consulta HQL: se consulta la clase 'Usuarios', no la tabla
            String hql = "FROM Usuarios WHERE nombre = :nom AND password = :pass";
            Query<Usuarios> query = session.createQuery(hql, Usuarios.class);
            query.setParameter("nom", nombre);
            query.setParameter("pass", password);

            // Obtiene un único resultado;
            user = query.uniqueResult();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return user;
    }
}
