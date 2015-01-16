package org.priorityhealth.stab.pdiff.persistence.repository.profile;

import com.j256.ormlite.support.ConnectionSource;
import org.priorityhealth.stab.pdiff.domain.entity.profile.State;
import org.priorityhealth.stab.pdiff.domain.repository.profile.StateRepositoryInterface;
import org.priorityhealth.stab.pdiff.persistence.repository.AbstractRepository;

import java.sql.SQLException;

public class StateRepository extends AbstractRepository<State> implements StateRepositoryInterface {

    public StateRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource);
    }
}
