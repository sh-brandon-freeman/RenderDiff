package org.priorityhealth.stab.pdiff.view.factory;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.priorityhealth.stab.pdiff.domain.entity.asset.Asset;

public class AssetCellFactory implements Callback<ListView<Asset>, ListCell<Asset>> {
    @Override
    public ListCell<Asset> call(ListView<Asset> p) {
        return new ListCell<Asset>() {
            @Override
            protected void updateItem(Asset asset, boolean bln) {
                super.updateItem(asset, bln);
                if (asset != null) {
                    setText(asset.getName());
                } else {
                    setText(null);
                }
            }
        };
    }
}
