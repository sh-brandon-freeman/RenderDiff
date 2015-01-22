package org.priorityhealth.stab.pdiff.persistence.repository.profile;

import com.j256.ormlite.support.ConnectionSource;
import org.priorityhealth.stab.pdiff.domain.entity.profile.IgnoredArea;
import org.priorityhealth.stab.pdiff.domain.repository.profile.IgnoredAreaRepositoryInterface;
import org.priorityhealth.stab.pdiff.persistence.repository.AbstractRepository;

import java.sql.SQLException;
import java.util.List;

public class IgnoredAreaRepository extends AbstractRepository<IgnoredArea> implements IgnoredAreaRepositoryInterface {

    public IgnoredAreaRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource);
    }
}
