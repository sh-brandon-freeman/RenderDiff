package org.priorityhealth.stab.pdiff.domain.entity.test;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.priorityhealth.stab.pdiff.domain.entity.profile.Profile;

import java.util.Date;

@DatabaseTable(tableName = "test.tests")
public class Test {

    @DatabaseField(generatedId = true)
    protected int id;

    @DatabaseField
    protected String name;

    @DatabaseField(canBeNull = false, foreign = true)
    protected Type type;

    @DatabaseField(canBeNull = false, foreign = true)
    protected Profile known;

    @DatabaseField(canBeNull = false, foreign = true)
    protected Profile current;

    @DatabaseField
    protected Date created;
}
