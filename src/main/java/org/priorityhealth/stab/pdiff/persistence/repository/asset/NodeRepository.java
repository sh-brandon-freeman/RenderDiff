package org.priorityhealth.stab.pdiff.persistence.repository.asset;

import com.j256.ormlite.support.ConnectionSource;
import org.priorityhealth.stab.pdiff.domain.entity.asset.Node;
import org.priorityhealth.stab.pdiff.domain.repository.asset.NodeRepositoryInterface;
import org.priorityhealth.stab.pdiff.persistence.repository.AbstractRepository;

import java.sql.SQLException;

public class NodeRepository extends AbstractRepository<Node> implements NodeRepositoryInterface {

    public NodeRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource);
    }
}
