package org.priorityhealth.stab.pdiff.domain.repository.profile;

import org.priorityhealth.stab.pdiff.domain.entity.profile.Profile;
import org.priorityhealth.stab.pdiff.domain.repository.AbstractRepositoryInterface;

import java.sql.SQLException;
import java.util.List;

public interface ProfileRepositoryInterface extends AbstractRepositoryInterface<Profile> {
    public List<Profile> getByAssetId(int id) throws SQLException;
}
