package org.priorityhealth.stab.pdiff.domain.entity.asset;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.priorityhealth.stab.pdiff.domain.entity.AbstractEntity;

import java.util.Date;

@DatabaseTable(tableName = "asset.nodes")
public class Node extends AbstractEntity {

    @DatabaseField
    protected String name;

    @DatabaseField(canBeNull = false, foreign = true)
    protected Asset asset;

    @DatabaseField
    protected String url;

    @DatabaseField
    protected Date created;

    public String getName() {
        return name;
    }

    public Node setName(String name) {
        this.name = name;
        return this;
    }

    public Asset getAsset() {
        return asset;
    }

    public Node setAsset(Asset asset) {
        this.asset = asset;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Node setUrl(String url) {
        this.url = url;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public Node setCreated(Date created) {
        this.created = created;
        return this;
    }

    @Override
    public String toString() {
        return "{url='" + url + '\'' +
                '}';
    }
}
