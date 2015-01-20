package org.priorityhealth.stab.pdiff.persistence.repository.profile;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import org.priorityhealth.stab.pdiff.domain.entity.profile.State;
import org.priorityhealth.stab.pdiff.domain.repository.profile.StateRepositoryInterface;
import org.priorityhealth.stab.pdiff.persistence.repository.AbstractRepository;

import java.sql.SQLException;
import java.util.List;

public class StateRepository extends AbstractRepository<State> implements StateRepositoryInterface {

    public StateRepository(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource);
    }

    public List<State> getByProfileId(int id) throws SQLException {
        QueryBuilder<State, String> queryBuilder = dao.queryBuilder();
        Where where = queryBuilder.where();
        where.eq("profile_id", id);
        queryBuilder.orderBy("created", false);
        return queryBuilder.query();
    }
}
