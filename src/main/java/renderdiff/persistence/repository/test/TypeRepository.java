package renderdiff.persistence.repository.test;

import com.j256.ormlite.support.ConnectionSource;
import renderdiff.domain.entity.test.Type;
import renderdiff.domain.repository.test.TypeRepositoryInterface;
import renderdiff.persistence.repository.AbstractRepository;

import java.sql.SQLException;

public class TypeRepository extends AbstractRepository<Type> implements TypeRepositoryInterface {

    public TypeRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource);
    }
}
