package org.priorityhealth.stab.pdiff.persistence.repository.profile;

import com.j256.ormlite.support.ConnectionSource;
import org.priorityhealth.stab.pdiff.domain.entity.profile.Profile;
import org.priorityhealth.stab.pdiff.domain.repository.profile.ProfileRepositoryInterface;
import org.priorityhealth.stab.pdiff.persistence.repository.AbstractRepository;

import java.sql.SQLException;

public class ProfileRepository extends AbstractRepository<Profile> implements ProfileRepositoryInterface {

    public ProfileRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource);
    }

}
