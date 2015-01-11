package renderdiff.persistence.repository;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import renderdiff.domain.repository.AbstractRepositoryInterface;

import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.List;

abstract public class AbstractRepository<T> implements AbstractRepositoryInterface<T> {
    protected Dao<T, String> dao;
    protected ConnectionSource connectionSource;
    protected Class<T> genericClass;

    @SuppressWarnings("unchecked") // I don't know how to get around this ...
    public AbstractRepository(ConnectionSource connectionSource) throws SQLException {
        this.connectionSource = connectionSource;
        genericClass = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        dao = DaoManager.createDao(connectionSource, genericClass);
    }

    public T getById(int id) throws SQLException {
        return dao.queryBuilder().where().eq("id", id).queryForFirst();
    }

    public List<T> getAll() throws SQLException {
        return dao.queryBuilder().query();
    }

    public void create(T entity) throws SQLException {
        dao.create(entity);
    }

    public void update(T entity) throws SQLException {
        dao.update(entity);
    }

    public void delete(T entity) throws SQLException {
        dao.delete(entity);
    }

    public void close() throws SQLException {
        connectionSource.close();
    }

    public void createTable() throws SQLException{
        TableUtils.createTable(connectionSource, genericClass);
    }
}
