package org.priorityhealth.stab.pdiff.persistence.repository.test;

import com.j256.ormlite.support.ConnectionSource;
import org.priorityhealth.stab.pdiff.domain.repository.test.TestRepositoryInterface;
import org.priorityhealth.stab.pdiff.persistence.repository.AbstractRepository;
import org.priorityhealth.stab.pdiff.domain.entity.test.Test;

import java.sql.SQLException;

public class TestRepository extends AbstractRepository<Test> implements TestRepositoryInterface {

    public TestRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource);
    }
}
