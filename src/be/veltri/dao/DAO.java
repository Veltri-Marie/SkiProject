package be.veltri.dao;

import java.sql.Connection;
import java.util.List;

public abstract class DAO<T> {
    protected Connection connect = null;

    public DAO(Connection conn) {
        this.connect = conn;
    }

    public abstract int getNextIdDAO();
    public abstract boolean createDAO(T obj); 
    public abstract boolean deleteDAO(T obj);
    public abstract boolean updateDAO(T obj);
    public abstract T findDAO(int id);
    public abstract List<T> findAllDAO(); 
}
