package org.priorityhealth.stab.pdiff.view.converter;

import javafx.util.StringConverter;
import org.priorityhealth.stab.pdiff.domain.entity.asset.Asset;

public class AssetStringConverter extends StringConverter<Asset> {
    @Override
    public String toString(Asset asset) {
        if (asset == null){
            return null;
        } else {
            return asset.getName();
        }
    }

    @Override
    public Asset fromString(String name) {
        return null;
    }
}
