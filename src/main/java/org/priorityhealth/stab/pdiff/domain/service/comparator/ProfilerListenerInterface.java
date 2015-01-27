package org.priorityhealth.stab.pdiff.domain.service.comparator;

import org.priorityhealth.stab.pdiff.domain.entity.profile.Profile;

public interface ProfilerListenerInterface {
    public void onProfileComplete(Profile profile);
}
