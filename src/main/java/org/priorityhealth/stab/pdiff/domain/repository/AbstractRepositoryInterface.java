package org.priorityhealth.stab.pdiff.domain.repository;

import java.sql.SQLException;
import java.util.List;

abstract public interface AbstractRepositoryInterface<T> {
    public T getById(int id) throws SQLException;
    public List<T> getAll() throws SQLException;
    public void create(T entity) throws SQLException;
    public void update(T entity) throws SQLException;
    public void delete(T entity) throws SQLException;
    public void close() throws SQLException;
}
