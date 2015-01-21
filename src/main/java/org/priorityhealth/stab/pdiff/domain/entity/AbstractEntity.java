package org.priorityhealth.stab.pdiff.domain.entity;

import com.j256.ormlite.field.DatabaseField;

abstract public class AbstractEntity implements Cloneable {

    public Object clone()throws CloneNotSupportedException{
        return super.clone();
    }

}
