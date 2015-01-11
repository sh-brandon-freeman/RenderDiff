package renderdiff.domain.entity.asset;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Crawl
 */
@DatabaseTable(tableName = "asset.assets")
public class Asset {

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
     * Domain
     */
    @DatabaseField
    protected String domain;

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
    public Asset setId(int id) {
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
    public Asset setName(String name) {
        this.name = name;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public Asset setDomain(String domain) {
        this.domain = domain;
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
    public Asset setLoadCompleteScript(String loadCompleteScript) {
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
    public Asset setLoginNodeUrl(String loginNodeUrl) {
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
    public Asset setNodes(ForeignCollection<Node> nodes) {
        this.nodes = nodes;
        return this;
    }

    /**
     *
     * @param node Node
     * @return Crawl
     */
    public Asset addNode(Node node) {
        nodes.add(node);
        return this;
    }

    /**
     *
     * @param node Node
     * @return Crawl
     */
    public Asset removeNode(Node node) {
        nodes.remove(node);
        return this;
    }
}
