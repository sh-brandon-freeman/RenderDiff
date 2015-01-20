package org.priorityhealth.stab.pdiff.persistence.repository.profile;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import org.priorityhealth.stab.pdiff.domain.entity.profile.Profile;
import org.priorityhealth.stab.pdiff.domain.repository.profile.ProfileRepositoryInterface;
import org.priorityhealth.stab.pdiff.persistence.repository.AbstractRepository;

import java.sql.SQLException;
import java.util.List;

public class ProfileRepository extends AbstractRepository<Profile> implements ProfileRepositoryInterface {

    public ProfileRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource);
    }

    public List<Profile> getByAssetId(int id) throws SQLException {
        QueryBuilder<Profile, String> queryBuilder = dao.queryBuilder();
        Where where = queryBuilder.where();
        where.eq("asset_id", id);
        where.and();
        where.eq("complete", true);
        queryBuilder.orderBy("created", false);
        return queryBuilder.query();
    }
}
