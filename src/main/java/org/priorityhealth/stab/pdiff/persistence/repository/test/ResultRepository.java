package org.priorityhealth.stab.pdiff.persistence.repository.test;

import com.j256.ormlite.support.ConnectionSource;
import org.priorityhealth.stab.pdiff.domain.entity.test.Result;
import org.priorityhealth.stab.pdiff.persistence.repository.AbstractRepository;
import org.priorityhealth.stab.pdiff.domain.repository.test.ResultRepositoryInterface;

import java.sql.SQLException;

public class ResultRepository extends AbstractRepository<Result> implements ResultRepositoryInterface {

    public ResultRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource);
    }
}
