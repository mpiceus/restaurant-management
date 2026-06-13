package dao;

public abstract class BaseDAO<T> {

    protected String entityName;

    public BaseDAO(String entityName){
        this.entityName = entityName;
    }

    public abstract int insert(T object) throws Exception;

    public abstract void update(T object) throws Exception;

    public abstract void delete(int id) throws Exception;
}