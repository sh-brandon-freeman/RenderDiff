package renderdiff.domain.entity.node;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import renderdiff.domain.entity.crawl.Run;

import java.util.Date;

/**
 * Result
 */
@DatabaseTable(tableName = "crawl.results")
public class Result {

    @DatabaseField(generatedId = true)
    protected int id;

    @DatabaseField(canBeNull = false, foreign = true)
    protected Node node;

    @DatabaseField(canBeNull = false, foreign = true)
    protected Run run;

    @DatabaseField
    protected String knownImagePath;

    @DatabaseField(canBeNull = false)
    protected String currentImagePath;

    @DatabaseField
    protected String diffImagePath;

    @DatabaseField
    protected Date created;

    public int getId() {
        return id;
    }

    public Result setId(int id) {
        this.id = id;
        return this;
    }

    public Node getNode() {
        return node;
    }

    public Result setNode(Node node) {
        this.node = node;
        return this;
    }

    public Run getRun() {
        return run;
    }

    public Result setRun(Run run) {
        this.run = run;
        return this;
    }

    public String getKnownImagePath() {
        return knownImagePath;
    }

    public Result setKnownImagePath(String knownImagePath) {
        this.knownImagePath = knownImagePath;
        return this;
    }

    public String getCurrentImagePath() {
        return currentImagePath;
    }

    public Result setCurrentImagePath(String currentImagePath) {
        this.currentImagePath = currentImagePath;
        return this;
    }

    public String getDiffImagePath() {
        return diffImagePath;
    }

    public Result setDiffImagePath(String diffImagePath) {
        this.diffImagePath = diffImagePath;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public Result setCreated(Date created) {
        this.created = created;
        return this;
    }
}
