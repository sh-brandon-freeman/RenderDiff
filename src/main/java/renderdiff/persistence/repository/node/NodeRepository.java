package renderdiff.persistence.repository.node;

import com.j256.ormlite.support.ConnectionSource;
import renderdiff.domain.entity.node.Node;
import renderdiff.domain.repository.node.NodeRepositoryInterface;
import renderdiff.persistence.repository.AbstractRepository;

import java.sql.SQLException;

public class NodeRepository extends AbstractRepository<Node> implements NodeRepositoryInterface {

    public NodeRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource);
    }
}
