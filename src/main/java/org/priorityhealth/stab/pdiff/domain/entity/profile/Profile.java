package org.priorityhealth.stab.pdiff.domain.entity.profile;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import org.priorityhealth.stab.pdiff.domain.entity.AbstractEntity;
import org.priorityhealth.stab.pdiff.domain.entity.asset.Asset;

import java.util.Date;

@DatabaseTable(tableName = "profile.profiles")
public class Profile extends AbstractEntity {

    @DatabaseField(generatedId = true)
    protected int id;

    @DatabaseField(canBeNull = false, foreign = true)
    protected Asset asset;

    /**
     * States
     */
    @ForeignCollectionField(eager = true)
    protected ForeignCollection<State> states;

    @DatabaseField
    protected boolean complete;

    @DatabaseField
    protected Date created;

    public int getId() {
        return id;
    }

    public Profile setId(int id) {
        this.id = id;
        return this;
    }

    public Asset getAsset() {
        return asset;
    }

    public Profile setAsset(Asset asset) {
        this.asset = asset;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public Profile setCreated(Date created) {
        this.created = created;
        return this;
    }

    public ForeignCollection<State> getStates() {
        return states;
    }

    public Profile setStates(ForeignCollection<State> states) {
        this.states = states;
        return this;
    }

    public Profile addState(State state) {
        states.add(state);
        return this;
    }

    public Profile removeState(State state) {
        states.remove(state);
        return this;
    }

    public boolean isComplete() {
        return complete;
    }

    public Profile setComplete(boolean complete) {
        this.complete = complete;
        return this;
    }
}
