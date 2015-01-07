package renderdiff.persistence.repository.crawl;

import com.j256.ormlite.support.ConnectionSource;
import renderdiff.domain.entity.crawl.Crawl;
import renderdiff.domain.repository.AbstractRepositoryInterface;
import renderdiff.domain.repository.crawl.CrawlRepositoryInterface;
import renderdiff.persistence.repository.AbstractRepository;

import java.sql.SQLException;

public class CrawlRepository extends AbstractRepository<Crawl> implements AbstractRepositoryInterface<Crawl>, CrawlRepositoryInterface {

    public CrawlRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource);
    }

}
