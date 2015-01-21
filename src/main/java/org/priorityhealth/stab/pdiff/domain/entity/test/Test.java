package org.priorityhealth.stab.pdiff.domain.entity.test;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import org.priorityhealth.stab.pdiff.domain.entity.AbstractEntity;
import org.priorityhealth.stab.pdiff.domain.entity.profile.Profile;
import org.priorityhealth.stab.pdiff.domain.entity.profile.State;

import java.util.Date;

@DatabaseTable(tableName = "test.tests")
public class Test extends AbstractEntity {

    @DatabaseField(generatedId = true)
    protected int id;

    @DatabaseField(canBeNull = false, foreign = true)
    protected Profile known;

    @DatabaseField(canBeNull = false, foreign = true)
    protected Profile current;

    @DatabaseField
    protected Date created;

    @ForeignCollectionField(eager = true)
    protected ForeignCollection<Result> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Profile getKnown() {
        return known;
    }

    public void setKnown(Profile known) {
        this.known = known;
    }

    public Profile getCurrent() {
        return current;
    }

    public void setCurrent(Profile current) {
        this.current = current;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public ForeignCollection<Result> getResults() {
        return results;
    }

    public void setResults(ForeignCollection<Result> results) {
        this.results = results;
    }

    public void addResult(Result result) {
        this.results.add(result);
    }

    public void removeResult(Result result) {
        this.results.remove(result);
    }
}
