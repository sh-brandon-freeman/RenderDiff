package org.priorityhealth.stab.pdiff.persistence.repository.test;

import com.j256.ormlite.support.ConnectionSource;
import org.priorityhealth.stab.pdiff.persistence.repository.AbstractRepository;
import org.priorityhealth.stab.pdiff.domain.entity.test.Type;
import org.priorityhealth.stab.pdiff.domain.repository.test.TypeRepositoryInterface;

import java.sql.SQLException;

public class TypeRepository extends AbstractRepository<Type> implements TypeRepositoryInterface {

    public TypeRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource);
    }
}
