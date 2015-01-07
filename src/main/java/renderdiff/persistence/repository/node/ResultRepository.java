package renderdiff.persistence.repository.node;

import com.j256.ormlite.support.ConnectionSource;
import renderdiff.domain.entity.node.Result;
import renderdiff.domain.repository.node.ResultRepositoryInterface;
import renderdiff.persistence.repository.AbstractRepository;

import java.sql.SQLException;

public class ResultRepository extends AbstractRepository<Result> implements ResultRepositoryInterface {

    public ResultRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource);
    }
}
