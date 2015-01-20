package org.priorityhealth.stab.pdiff.domain.repository.profile;

import org.priorityhealth.stab.pdiff.domain.repository.AbstractRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.entity.profile.State;

import java.sql.SQLException;
import java.util.List;

public interface StateRepositoryInterface extends AbstractRepositoryInterface<State> {

    public List<State> getByProfileId(int id) throws SQLException;

}
