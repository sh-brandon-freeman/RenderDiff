package renderdiff.persistence.repository.crawl;

import com.j256.ormlite.support.ConnectionSource;
import renderdiff.domain.entity.crawl.Run;
import renderdiff.domain.repository.crawl.RunRepositoryInterface;
import renderdiff.persistence.repository.AbstractRepository;

import java.sql.SQLException;

/**
 * Created by bra50311 on 1/7/15.
 */
public class RunRepository extends AbstractRepository<Run> implements RunRepositoryInterface {

    public RunRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource);
    }

}
