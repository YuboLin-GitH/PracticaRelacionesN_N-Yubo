package com.yubo.DAO;

import com.yubo.Model.Usuarios;
import com.yubo.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class UsuarioDAOImpl implements UsuarioDAO {

    /**
     * 使用 Hibernate 验证登录
     * 注意：为了配合之前数据库插入的 '1234'，这里暂时不用 SHA256 加密。
     * 如果你想用加密，需要在存入数据库时也加密。
     */

    @Override
    public Usuarios login(String nombre, String password) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Usuarios user = null;
        try {
            // HQL 查询：注意是从类名 'Usuarios' 查，不是表名
            String hql = "FROM Usuarios WHERE nombre = :nom AND password = :pass";
            Query<Usuarios> query = session.createQuery(hql, Usuarios.class);
            query.setParameter("nom", nombre);
            query.setParameter("pass", password);

            // 获取唯一结果，没找到则返回 null
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