package renderdiff.domain.repository;

import java.sql.SQLException;

public interface AbstractRepositoryInterface<T> {
    public T getById(int id) throws SQLException;
    public void create(T entity) throws SQLException;
    public void update(T entity) throws SQLException;
    public void delete(T entity) throws SQLException;
    public void close() throws SQLException;
}
