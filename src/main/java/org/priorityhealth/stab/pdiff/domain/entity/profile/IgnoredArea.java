package org.priorityhealth.stab.pdiff.domain.entity.profile;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "profile.ignored")
public class IgnoredArea {

    @DatabaseField(generatedId = true)
    protected int id;

    @DatabaseField(canBeNull = false, foreign = true)
    protected State state;

    @DatabaseField(canBeNull = false)
    protected int x1;

    @DatabaseField(canBeNull = false)
    protected int x2;

    @DatabaseField(canBeNull = false)
    protected int y1;

    @DatabaseField(canBeNull = false)
    protected int y2;

    @DatabaseField(canBeNull = false)
    protected Date created;

    public int getId() {
        return id;
    }

    public IgnoredArea setId(int id) {
        this.id = id;
        return this;
    }

    public State getState() {
        return state;
    }

    public IgnoredArea setState(State state) {
        this.state = state;
        return this;
    }

    public int getX1() {
        return x1;
    }

    public IgnoredArea setX1(int x1) {
        this.x1 = x1;
        return this;
    }

    public int getX2() {
        return x2;
    }

    public IgnoredArea setX2(int x2) {
        this.x2 = x2;
        return this;
    }

    public int getY1() {
        return y1;
    }

    public IgnoredArea setY1(int y1) {
        this.y1 = y1;
        return this;
    }

    public int getY2() {
        return y2;
    }

    public IgnoredArea setY2(int y2) {
        this.y2 = y2;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public IgnoredArea setCreated(Date created) {
        this.created = created;
        return this;
    }
}
