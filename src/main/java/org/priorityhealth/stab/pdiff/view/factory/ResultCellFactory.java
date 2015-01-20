package org.priorityhealth.stab.pdiff.view.factory;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.priorityhealth.stab.pdiff.domain.entity.test.Result;

public class ResultCellFactory implements Callback<ListView<Result>, ListCell<Result>> {

    @Override
    public ListCell<Result> call(ListView<Result> param) {
        return new ListCell<Result>() {
            @Override
            protected void updateItem(Result result, boolean bln) {
                super.updateItem(result, bln);

                if (result == null) {
                    setText(null);
                    return;
                }

                String imagePath = result.getDiffImagePath();
                String label = result.getKnown().getNode().getUrl();
                if (imagePath != null && imagePath.length() > 0) {
                    label += " [Y]";
                } else {
                    label += " [N]";
                }
                setText(label);
            }
        };
    }
}
