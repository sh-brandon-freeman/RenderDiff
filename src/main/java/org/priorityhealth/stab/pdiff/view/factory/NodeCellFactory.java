package org.priorityhealth.stab.pdiff.view.factory;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.priorityhealth.stab.pdiff.domain.entity.asset.Node;

public class NodeCellFactory implements Callback<ListView<Node>, ListCell<Node>> {
    @Override
    public ListCell<Node> call(ListView<Node> p) {
        return new ListCell<Node>() {
            @Override
            protected void updateItem(Node node, boolean bln) {
                super.updateItem(node, bln);
                if (node != null) {
                    setText(node.getUrl());
                } else {
                    setText(null);
                }
            }
        };
    }
}
