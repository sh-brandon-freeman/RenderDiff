package renderdiff.domain.entity.crawl;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import renderdiff.domain.entity.node.Node;

/**
 * Crawl
 */
@DatabaseTable(tableName = "crawl.crawls")
public class Crawl {

    /**
     * ID
     */
    @DatabaseField(generatedId = true)
    protected int id;

    /**
     * Name
     */
    @DatabaseField
    protected String name;

    /**
     * Load Complete Script
     */
    @DatabaseField
    protected String loadCompleteScript;

    /**
     * Login Node Url
     */
    @DatabaseField
    protected String loginNodeUrl;

    /**
     * Node Urls
     */
    @ForeignCollectionField(eager = false)
    protected ForeignCollection<Node> nodes;

    /**
     * Constructor
     */
    public Crawl() {

    }

    /**
     *
     * @return Crawl ID
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id Crawl ID
     * @return Crawl
     */
    public Crawl setId(int id) {
        this.id = id;
        return this;
    }

    /**
     *
     * @return Crawl
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name Crawl Name
     * @return Crawl
     */
    public Crawl setName(String name) {
        this.name = name;
        return this;
    }

    /**
     *
     * @return Load Complete Script
     */
    public String getLoadCompleteScript() {
        return loadCompleteScript;
    }

    /**
     *
     * @param loadCompleteScript Load Complete Script
     * @return Crawl
     */
    public Crawl setLoadCompleteScript(String loadCompleteScript) {
        this.loadCompleteScript = loadCompleteScript;
        return this;
    }

    /**
     *
     * @return Login Node URL
     */
    public String getLoginNodeUrl() {
        return loginNodeUrl;
    }

    /**
     *
     * @param loginNodeUrl Login Node Url
     * @return Crawl
     */
    public Crawl setLoginNodeUrl(String loginNodeUrl) {
        this.loginNodeUrl = loginNodeUrl;
        return this;
    }

    /**
     *
     * @return Nodes
     */
    public ForeignCollection<Node> getNodes() {
        return nodes;
    }

    /**
     *
     * @param nodes Nodes
     * @return Crawl
     */
    public Crawl setNodes(ForeignCollection<Node> nodes) {
        this.nodes = nodes;
        return this;
    }

    /**
     *
     * @param node Node
     * @return Crawl
     */
    public Crawl addNode(Node node) {
        nodes.add(node);
        return this;
    }

    /**
     *
     * @param node Node
     * @return Crawl
     */
    public Crawl removeNode(Node node) {
        nodes.remove(node);
        return this;
    }
}
