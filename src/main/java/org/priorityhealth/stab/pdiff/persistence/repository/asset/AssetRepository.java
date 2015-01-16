package org.priorityhealth.stab.pdiff.persistence.repository.asset;

import com.j256.ormlite.support.ConnectionSource;
import org.priorityhealth.stab.pdiff.persistence.repository.AbstractRepository;
import org.priorityhealth.stab.pdiff.domain.entity.asset.Asset;
import org.priorityhealth.stab.pdiff.domain.repository.asset.AssetRepositoryInterface;

import java.sql.SQLException;

public class AssetRepository extends AbstractRepository<Asset> implements AssetRepositoryInterface {

    public AssetRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource);
    }

}
