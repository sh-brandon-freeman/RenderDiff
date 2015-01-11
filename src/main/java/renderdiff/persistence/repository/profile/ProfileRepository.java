package renderdiff.persistence.repository.profile;

import com.j256.ormlite.support.ConnectionSource;
import renderdiff.domain.entity.profile.Profile;
import renderdiff.domain.repository.profile.ProfileRepositoryInterface;
import renderdiff.persistence.repository.AbstractRepository;

import java.sql.SQLException;

public class ProfileRepository extends AbstractRepository<Profile> implements ProfileRepositoryInterface {

    public ProfileRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource);
    }

}
