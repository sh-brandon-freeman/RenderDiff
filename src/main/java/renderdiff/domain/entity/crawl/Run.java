package renderdiff.domain.entity.crawl;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "crawl.runs")
public class Run {

    @DatabaseField(generatedId = true)
    protected int id;

    @DatabaseField(canBeNull = false, foreign = true)
    protected Crawl crawl;

    @DatabaseField
    protected Date created;

    public int getId() {
        return id;
    }

    public Run setId(int id) {
        this.id = id;
        return this;
    }

    public Crawl getCrawl() {
        return crawl;
    }

    public Run setCrawl(Crawl crawl) {
        this.crawl = crawl;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public Run setCreated(Date created) {
        this.created = created;
        return this;
    }
}
