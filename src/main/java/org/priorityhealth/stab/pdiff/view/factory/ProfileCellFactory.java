package org.priorityhealth.stab.pdiff.view.factory;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.priorityhealth.stab.pdiff.domain.entity.profile.Profile;

public class ProfileCellFactory implements Callback<ListView<Profile>, ListCell<Profile>> {
    @Override
    public ListCell<Profile> call(ListView<Profile> p) {
        return new ListCell<Profile>() {
            @Override
            protected void updateItem(Profile profile, boolean bln) {
                super.updateItem(profile, bln);
                if (profile != null) {
                    setText(profile.getCreated().toString());
                } else {
                    setText(null);
                }
            }
        };
    }
}
