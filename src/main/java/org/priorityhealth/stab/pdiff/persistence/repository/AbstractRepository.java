package org.priorityhealth.stab.pdiff.persistence.repository;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.priorityhealth.stab.pdiff.domain.repository.AbstractRepositoryInterface;

import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.List;

abstract public class AbstractRepository<AbstractEntity> implements AbstractRepositoryInterface<AbstractEntity> {
    protected Dao<AbstractEntity, String> dao;
    protected ConnectionSource connectionSource;
    protected Class<AbstractEntity> genericClass;

    @SuppressWarnings("unchecked") // I don't know how to get around this ...
    public AbstractRepository(ConnectionSource connectionSource) throws SQLException {
        this.connectionSource = connectionSource;
        genericClass = ((Class<AbstractEntity>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        dao = DaoManager.createDao(connectionSource, genericClass);
        if (!dao.isTableExists()) {
            TableUtils.createTable(connectionSource, genericClass);
        }
    }

    public AbstractEntity getById(int id) throws SQLException {
        return dao.queryForFirst(dao.queryBuilder().where().eq("id", id).prepare());
    }

    public List<AbstractEntity> getAll() throws SQLException {
        return dao.queryBuilder().query();
    }

    public void create(AbstractEntity entity) throws SQLException {
        dao.create(entity);
        dao.refresh(entity);
    }

    public void update(AbstractEntity entity) throws SQLException {
        dao.update(entity);
        dao.refresh(entity);
    }

    public void delete(AbstractEntity entity) throws SQLException {
        dao.delete(entity);
    }

    public void close() throws SQLException {
        connectionSource.close();
    }

    public Dao<AbstractEntity, String> getDao() {
        return dao;
    }

    public void refresh(AbstractEntity entity) throws SQLException {
        dao.refresh(entity);
    }
}
