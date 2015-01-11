package renderdiff.persistence.repository.profile;

import com.j256.ormlite.support.ConnectionSource;
import renderdiff.domain.entity.profile.State;
import renderdiff.domain.repository.profile.StateRepositoryInterface;
import renderdiff.persistence.repository.AbstractRepository;

import java.sql.SQLException;

public class StateRepository extends AbstractRepository<State> implements StateRepositoryInterface {

    public StateRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource);
    }
}
