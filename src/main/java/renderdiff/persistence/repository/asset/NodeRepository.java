package renderdiff.persistence.repository.asset;

import com.j256.ormlite.support.ConnectionSource;
import renderdiff.domain.entity.asset.Node;
import renderdiff.domain.repository.asset.NodeRepositoryInterface;
import renderdiff.persistence.repository.AbstractRepository;

import java.sql.SQLException;

public class NodeRepository extends AbstractRepository<Node> implements NodeRepositoryInterface {

    public NodeRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource);
    }
}
