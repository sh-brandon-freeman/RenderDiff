package org.priorityhealth.stab.pdiff.domain.entity.profile;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import org.priorityhealth.stab.pdiff.domain.entity.AbstractEntity;
import org.priorityhealth.stab.pdiff.domain.entity.asset.Node;

import java.util.Date;

/**
 * Result
 */
@DatabaseTable(tableName = "profile.states")
public class State extends AbstractEntity {

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh=true, maxForeignAutoRefreshLevel=3)
    protected Node node;

    @DatabaseField(canBeNull = false, foreign = true)
    protected Profile profile;

    @DatabaseField
    protected String imagePath;

    @DatabaseField
    protected Date created;

    @ForeignCollectionField(eager = true)
    protected ForeignCollection<IgnoredArea> ignoredAreas;

    public Node getNode() {
        return node;
    }

    public State setNode(Node node) {
        this.node = node;
        return this;
    }

    public Profile getProfile() {
        return profile;
    }

    public State setProfile(Profile profile) {
        this.profile = profile;
        return this;
    }

    public String getImagePath() {
        return imagePath;
    }

    public State setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public State setCreated(Date created) {
        this.created = created;
        return this;
    }

    public ForeignCollection<IgnoredArea> getIgnoredAreas() {
        return ignoredAreas;
    }

    public State setIgnoredAreas(ForeignCollection<IgnoredArea> ignoredAreas) {
        this.ignoredAreas = ignoredAreas;
        return this;
    }

    public State addIgnoredArea(IgnoredArea ignoredArea) {
        this.ignoredAreas.add(ignoredArea);
        return this;
    }

    public State removeIgnoredArea(IgnoredArea ignoredArea) {
        this.ignoredAreas.remove(ignoredArea);
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                "node=" + node +
                ", profile=" + profile +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
