package org.priorityhealth.stab.pdiff.view.factory;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.priorityhealth.stab.pdiff.domain.entity.asset.Node;
import org.priorityhealth.stab.pdiff.domain.entity.profile.State;

public class StateCellFactory implements Callback<ListView<State>, ListCell<State>> {

    @Override
    public ListCell<State> call(ListView<State> param) {
        return new ListCell<State>() {
            @Override
            protected void updateItem(State state, boolean bln) {
                super.updateItem(state, bln);
                if (state != null) {
                    Node node = state.getNode();
                    if (node != null) {
                        setText(node.getUrl());
                    } else {
                        setText(Integer.toString(state.getId()));
                    }
                } else {
                    setText(null);
                }
            }
        };
    }
}
