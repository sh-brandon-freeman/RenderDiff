package renderdiff.persistence.repository.test;

import com.j256.ormlite.support.ConnectionSource;
import renderdiff.domain.entity.test.Test;
import renderdiff.domain.repository.test.TestRepositoryInterface;
import renderdiff.persistence.repository.AbstractRepository;

import java.sql.SQLException;

public class TestRepository extends AbstractRepository<Test> implements TestRepositoryInterface {

    public TestRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource);
    }
}
