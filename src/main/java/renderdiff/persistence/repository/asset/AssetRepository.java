package renderdiff.persistence.repository.asset;

import com.j256.ormlite.support.ConnectionSource;
import renderdiff.domain.entity.asset.Asset;
import renderdiff.domain.repository.asset.AssetRepositoryInterface;
import renderdiff.persistence.repository.AbstractRepository;

import java.sql.SQLException;

public class AssetRepository extends AbstractRepository<Asset> implements AssetRepositoryInterface {

    public AssetRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource);
    }

}
