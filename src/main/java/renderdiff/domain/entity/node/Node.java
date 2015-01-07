package renderdiff.domain.entity.node;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import renderdiff.domain.entity.crawl.Crawl;

import java.util.Date;

@DatabaseTable(tableName = "crawl.nodes")
public class Node {

    @DatabaseField(generatedId = true)
    protected int id;

    @DatabaseField
    protected String name;

    @DatabaseField(canBeNull = false, foreign = true)
    protected Crawl crawl;

    @DatabaseField
    protected String url;

    @DatabaseField
    protected Date created;

    public int getId() {
        return id;
    }

    public Node setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Node setName(String name) {
        this.name = name;
        return this;
    }

    public Crawl getCrawl() {
        return crawl;
    }

    public Node setCrawl(Crawl crawl) {
        this.crawl = crawl;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Node setUrl(String url) {
        this.url = url;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public Node setCreated(Date created) {
        this.created = created;
        return this;
    }
}
