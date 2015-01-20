package org.priorityhealth.stab.pdiff.domain.entity.test;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.priorityhealth.stab.pdiff.domain.entity.profile.State;

import java.util.Date;

@DatabaseTable(tableName = "test.results")
public class Result {

    @DatabaseField(canBeNull = false, foreign = true)
    protected Test test;

    @DatabaseField(canBeNull = false, foreign = true)
    protected State current;

    @DatabaseField(canBeNull = false, foreign = true)
    protected State known;

    @DatabaseField
    protected String diffImagePath;

    @DatabaseField
    protected Date created;

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public State getCurrent() {
        return current;
    }

    public void setCurrent(State current) {
        this.current = current;
    }

    public State getKnown() {
        return known;
    }

    public void setKnown(State known) {
        this.known = known;
    }

    public String getDiffImagePath() {
        return diffImagePath;
    }

    public void setDiffImagePath(String diffImagePath) {
        this.diffImagePath = diffImagePath;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
