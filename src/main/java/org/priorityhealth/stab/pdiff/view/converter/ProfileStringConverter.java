package org.priorityhealth.stab.pdiff.view.converter;

import javafx.util.StringConverter;
import org.priorityhealth.stab.pdiff.domain.entity.profile.Profile;

public class ProfileStringConverter extends StringConverter<Profile> {
    @Override
    public String toString(Profile profile) {
        if (profile == null){
            return null;
        } else {
            return profile.getCreated().toString();
        }
    }

    @Override
    public Profile fromString(String name) {
        return null;
    }
}
