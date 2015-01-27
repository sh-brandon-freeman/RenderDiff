package org.priorityhealth.stab.pdiff.domain.entity;

import com.j256.ormlite.field.DatabaseField;

abstract public class AbstractEntity implements Cloneable {

    /**
     * ID
     */
    @DatabaseField(generatedId = true)
    protected int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }

}
