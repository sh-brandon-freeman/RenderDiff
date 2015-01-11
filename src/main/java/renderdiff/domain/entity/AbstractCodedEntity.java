package renderdiff.domain.entity;

import com.j256.ormlite.field.DatabaseField;

abstract public class AbstractCodedEntity {

    @DatabaseField(id = true)
    protected String code;

    @DatabaseField(canBeNull = false)
    protected String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
